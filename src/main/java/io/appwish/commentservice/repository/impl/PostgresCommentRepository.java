package io.appwish.commentservice.repository.impl;

import com.google.protobuf.Timestamp;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.ItemType;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.model.query.CommentSelector;
import io.appwish.commentservice.repository.CommentRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Enables storing comments in PostgreSQL
 */
public class PostgresCommentRepository implements CommentRepository {

  private static final String ID_COLUMN = "id";
  private static final String CREATED_AT_COLUMN = "created_at";
  private static final String UPDATED_AT_COLUMN = "updated_at";
  private static final String USER_ID_COLUMN = "user_id";
  private static final String ITEM_ID_COLUMN = "item_id";
  private static final String ITEM_TYPE_COLUMN = "item_type";
  private static final String CONTENT_COLUMN = "content";

  private final PgPool client;

  public PostgresCommentRepository(final PgPool client) {
    this.client = client;
  }

  @Override
  public Future<List<Comment>> findAll(final CommentSelector query) {
    final Promise<List<Comment>> promise = Promise.promise();

    client.preparedQuery(Query.FIND_COMMENTS_BY_ITEM.sql(), Tuple.of(query.getItemId(), query.getItemType().toString()), event -> {
      if (event.succeeded()) {
        final List<Comment> comments = StreamSupport
            .stream(event.result().spliterator(), false)
            .map(this::commentFromRow)
            .collect(Collectors.toList());

        final List<Future> childrenFutures = comments.stream().map(this::getAllChildren).collect(Collectors.toList());

        // TODO build tree instead of returning flat list
        // TODO fetch all with one DB query
        CompositeFuture.all(childrenFutures).onComplete(ar -> {
          final List<List<Comment>> collect = ar.result().list().stream().map(o -> (List<Comment>) o).collect(Collectors.toList());
          final List<Comment> children = collect.stream().flatMap(Collection::stream).collect(Collectors.toList());
          comments.addAll(children);
          promise.complete(comments);
        });
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Comment> addOne(final CommentInput comment, final String userId) {
    final Promise<Comment> promise = Promise.promise();
    final LocalDateTime now = LocalDateTime.now();

    client.preparedQuery(Query.INSERT_COMMENT_QUERY.sql(),
        Tuple.of(userId, String.valueOf(comment.getItemId()), comment.getItemType().toString(), comment.getContent(), now, now),
        event -> {
          if (event.succeeded()) {
            if (event.result().iterator().hasNext()) {
              final Row row = event.result().iterator().next();
              promise.complete(commentFromRow(row));
            } else {
              promise.fail(new AssertionError("Adding a comment should always succeed"));
            }
          } else {
            promise.fail(event.cause());
          }
        });

    return promise.future();
  }

  @Override
  public Future<Boolean> deleteOne(final CommentQuery query) {
    final Promise<Boolean> promise = Promise.promise();

    client.preparedQuery(Query.DELETE_COMMENT_QUERY.sql(), Tuple.of(query.getId()), event -> {
      if (event.succeeded()) {
        if (event.result().rowCount() == 1) {
          promise.complete(true);
        } else {
          promise.complete(false);
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Optional<Comment>> updateOne(final UpdateCommentInput comment) {
    final Promise<Optional<Comment>> promise = Promise.promise();

    client.preparedQuery(Query.UPDATE_COMMENT_QUERY.sql(),
        Tuple.of(comment.getId(), comment.getContent()),
        event -> {
          if (event.succeeded() && event.result().rowCount() == 1) {
            final Row row = event.result().iterator().next();
            promise.complete(Optional.of(commentFromRow(row)));
          } else if (event.succeeded() && event.result().rowCount() == 0) {
            promise.complete(Optional.empty());
          } else if (event.failed()) {
            promise.fail(event.cause());
          }
        });

    return promise.future();
  }

  @Override
  public Future<Boolean> isAuthor(CommentQuery query, String userId) {
    final Promise<Boolean> promise = Promise.promise();

    client.preparedQuery(Query.UPDATE_COMMENT_QUERY.sql(),
        Tuple.of(query.getId(), userId),
        event -> {
          if (event.succeeded() && event.result().rowCount() == 1) {
            promise.complete(true);
          } else if (event.succeeded() && event.result().rowCount() == 0) {
            promise.complete(false);
          } else if (event.failed()) {
            promise.fail(event.cause());
          }
        });

    return promise.future();
  }

  public Future<List<Comment>> getAllChildren(final Comment comment) {
    final Promise<List<Comment>> promise = Promise.promise();
    final List<Future> futures = new ArrayList<>();

    client.preparedQuery(Query.GET_CHILD_COMMENTS.sql(),
        Tuple.of(String.valueOf(comment.getId()), ItemType.COMMENT.toString()),
        event -> {
          if (event.succeeded() && event.result().rowCount() > 0) {
            final List<Comment> parents = new ArrayList<>();

            for (final Row row : event.result()) {
              final Comment parent = commentFromRow(row);
              final Future<List<Comment>> children = getAllChildren(parent);
              parents.add(parent);
              futures.add(children);
            }

            CompositeFuture.all(futures).onComplete(ar -> {
              final List<List<Comment>> collect = ar.result().list().stream().map(o -> (List<Comment>) o).collect(Collectors.toList());
              final List<Comment> comments = collect.stream().flatMap(Collection::stream).collect(Collectors.toList());
              comments.addAll(parents);
              promise.complete(comments);
            });
          } else if (event.succeeded() && event.result().rowCount() == 0) {
            promise.complete(List.of());
          } else if (event.failed()) {
            promise.fail(event.cause());
          }
        });

    return promise.future();
  }

  private Comment commentFromRow(final Row row) {
    final LocalDateTime createdAt = row.getLocalDateTime(CREATED_AT_COLUMN);
    final LocalDateTime updatedAt = row.getLocalDateTime(UPDATED_AT_COLUMN);
    return new Comment(
        row.getLong(ID_COLUMN),
        row.getString(USER_ID_COLUMN),
        row.getString(ITEM_ID_COLUMN),
        ItemType.valueOf(row.getString(ITEM_TYPE_COLUMN)),
        row.getString(CONTENT_COLUMN),
        Timestamp.newBuilder().setNanos(createdAt.toInstant(ZoneOffset.UTC).getNano()).setSeconds(createdAt.toInstant(ZoneOffset.UTC).getEpochSecond()).build(),
        Timestamp.newBuilder().setNanos(updatedAt.toInstant(ZoneOffset.UTC).getNano()).setSeconds(updatedAt.toInstant(ZoneOffset.UTC).getEpochSecond()).build());
  }
}
