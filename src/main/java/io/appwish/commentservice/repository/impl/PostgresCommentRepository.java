package io.appwish.commentservice.repository.impl;

import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.ParentType;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.AllCommentQuery;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.repository.CommentRepository;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.protobuf.Timestamp;

/**
 * Enables storing comments in PostgreSQL
 */
public class PostgresCommentRepository implements CommentRepository {

  private static final String ID_COLUMN = "id";
  private static final String CREATED_AT_COLUMN = "created_at";
  private static final String UPDATED_AT_COLUMN = "updated_at";
  private static final String USER_ID_COLUMN = "user_id";
  private static final String PARENT_ID_COLUMN = "parent_id";
  private static final String PARENT_TYPE_COLUMN = "parent_type";
  private static final String CONTENT_COLUMN = "content";

  private final PgPool client;

  public PostgresCommentRepository(final PgPool client) {
    this.client = client;
  }

  @Override
  public Future<List<Comment>> findAll(final AllCommentQuery query) {
    final Promise<List<Comment>> promise = Promise.promise();

    client.preparedQuery(Query.FIND_ALL_COMMENTS_BY_ITEM.sql(), Tuple.of(query.getParentId(), query.getParentType().toString()), event -> {
      if (event.succeeded()) {
        final List<Comment> comments = StreamSupport
          .stream(event.result().spliterator(), false)
          .map(this::commentFromRow)
          .collect(Collectors.toList());
        promise.complete(comments);
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Comment> addOne(final CommentInput comment) {
    final Promise<Comment> promise = Promise.promise();

    final LocalDateTime now = LocalDateTime.now();
    // TODO remove hardcoded value
    client.preparedQuery(Query.INSERT_COMMENT_QUERY.sql(),
      Tuple.of(123L, comment.getParentId(), comment.getParentType().toString(), comment.getContent(), now, now),
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

  private Comment commentFromRow(final Row row) {
    final LocalDateTime createdAt = row.getLocalDateTime(CREATED_AT_COLUMN);
    final LocalDateTime updatedAt = row.getLocalDateTime(UPDATED_AT_COLUMN);
    return new Comment(
      row.getLong(ID_COLUMN),
      row.getLong(USER_ID_COLUMN),
      row.getLong(PARENT_ID_COLUMN),
      ParentType.valueOf(row.getString(PARENT_TYPE_COLUMN)),
      row.getString(CONTENT_COLUMN),
		  Timestamp.newBuilder().setNanos(createdAt.toInstant(ZoneOffset.UTC).getNano()).setSeconds(createdAt.toInstant(ZoneOffset.UTC).getEpochSecond()).build(),
		  Timestamp.newBuilder().setNanos(updatedAt.toInstant(ZoneOffset.UTC).getNano()).setSeconds(updatedAt.toInstant(ZoneOffset.UTC).getEpochSecond()).build());
  }
}
