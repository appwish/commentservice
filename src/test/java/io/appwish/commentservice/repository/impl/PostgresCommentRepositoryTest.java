package io.appwish.commentservice.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.commentservice.TestData;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.ItemType;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.model.query.CommentSelector;
import io.appwish.commentservice.repository.CommentRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class PostgresCommentRepositoryTest {

  private static final String DATABASE_HOST = "localhost";
  private static final String DEFAULT_POSTGRES = "postgres";

  private EmbeddedPostgres postgres;
  private CommentRepository repository;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) throws Exception {
    postgres = EmbeddedPostgres.start();

    final PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(postgres.getPort())
      .setHost(DATABASE_HOST)
      .setDatabase(DEFAULT_POSTGRES)
      .setUser(DEFAULT_POSTGRES)
      .setPassword(DEFAULT_POSTGRES);
    final PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    final PgPool client = PgPool.pool(connectOptions, poolOptions);

    client.query(Query.CREATE_COMMENTS_TABLE.sql(), context.completing());

    repository = new PostgresCommentRepository(client);
  }

  @AfterEach
  void tearDown() throws Exception {
    postgres.close();
  }

  @Test
  void should_be_able_to_store_comment(final Vertx vertx, final VertxTestContext context) {
    // given
    final CommentInput commentInput = new CommentInput(
      Long.parseLong(TestData.SOME_ITEM_ID),
      TestData.SOME_PARENT_TYPE,
      TestData.SOME_COMMENT_CONTENT);

    // when
    repository.addOne(commentInput, TestData.COMMENT_1.getUserId())
      .setHandler(event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertEquals(TestData.SOME_ITEM_ID, event.result().getItemId());
          assertEquals(TestData.SOME_PARENT_TYPE, event.result().getItemType());
          assertEquals(TestData.SOME_COMMENT_CONTENT, event.result().getContent());
          context.completeNow();
        });
      });
  }

  @Test
  void should_be_able_to_read_multiple_comments(final Vertx vertx, final VertxTestContext context) {
    // given
    final Future<Comment> addComment1a = repository.addOne(TestData.COMMENT_INPUT_1, TestData.COMMENT_1.getUserId());
    final Future<Comment> addComment1b = repository.addOne(TestData.COMMENT_INPUT_1, TestData.COMMENT_1.getUserId());
    final Future<Comment> addComment1c = repository.addOne(TestData.COMMENT_INPUT_1, TestData.COMMENT_1.getUserId());
    final Future<Comment> addComment2 = repository.addOne(TestData.COMMENT_INPUT_2, TestData.COMMENT_2.getUserId());
    final Future<Comment> addComment3 = repository.addOne(TestData.COMMENT_INPUT_3, TestData.COMMENT_3.getUserId());
    final Future<Comment> addComment4 = repository.addOne(TestData.COMMENT_INPUT_4, TestData.COMMENT_4.getUserId());

    context.assertComplete(CompositeFuture.all(List.of(addComment1a, addComment1b, addComment1c, addComment2, addComment3, addComment4)))
      .setHandler(event -> {

        // when
        repository.findAll(new CommentSelector(
            TestData.COMMENT_1.getItemId(),
            TestData.COMMENT_1.getItemType()
        )).setHandler(query -> context.verify(() -> {

          // then
          assertTrue(query.succeeded());
          assertEquals(3, query.result().size());
          query.result().forEach(comment -> assertTrue(isInList(comment, TestData.COMMENTS)));
          context.completeNow();
        }));
      });
  }

  @Test
  void should_not_delete_non_existent_comment(final Vertx vertx, final VertxTestContext context) {
    // when
    repository.deleteOne(new CommentQuery(TestData.NON_EXISTING_ID))
      .setHandler(event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertFalse(event.result());
          context.completeNow();
        });
      });
  }

  @Test
  void should_be_able_to_delete_existing_comment(final Vertx vertx, final VertxTestContext context) {
    // given
    context.assertComplete(repository.addOne(TestData.COMMENT_INPUT_1, TestData.COMMENT_1.getUserId())).setHandler(event -> {
      final long id = event.result().getId();

      // when
      repository.deleteOne(new CommentQuery(id)).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result());
          context.completeNow();
        });
      });
    });
  }

  @Test
  void should_not_update_non_existent_comment(final Vertx vertx, final VertxTestContext context) {
    // given
    final UpdateCommentInput updated = new UpdateCommentInput(
      TestData.NON_EXISTING_ID,
      TestData.COMMENT_2.getContent());

    // when
    repository.updateOne(updated).setHandler(query -> {

      // then
      context.verify(() -> {
        assertTrue(query.succeeded());
        assertTrue(query.result().isEmpty());
        context.completeNow();
      });
    });
  }

  @Test
  void  should_update_existing_comment(final Vertx vertx, final VertxTestContext context)
    throws Exception {
    // given
    context.assertComplete(repository.addOne(TestData.COMMENT_INPUT_1, TestData.COMMENT_1.getUserId())).setHandler(event -> {
      final long id = event.result().getId();
      final UpdateCommentInput updated = new UpdateCommentInput(id, TestData.COMMENT_2.getContent());

      // when
      repository.updateOne(updated).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result().isPresent());
          assertEquals(TestData.COMMENT_2.getContent(), query.result().get().getContent());
          assertEquals(TestData.COMMENT_1.getItemId(), query.result().get().getItemId());
          assertEquals(TestData.COMMENT_1.getItemType(), query.result().get().getItemType());
          assertEquals(TestData.COMMENT_1.getUserId(), query.result().get().getUserId());
          assertEquals(id, query.result().get().getId());
          context.completeNow();
        });
      });
    });
  }

  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
  void should_fail_fast_on_postgres_connection_error(final Vertx vertx,
    final VertxTestContext context) throws Exception {
    // given
    final UpdateCommentInput updateCommentInput = new UpdateCommentInput(
        TestData.NON_EXISTING_ID,
        TestData.COMMENT_2.getContent());

    // database down
    postgres.close();

    // when
    final Future<Comment> addComment = repository.addOne(TestData.COMMENT_INPUT_1, TestData.COMMENT_1.getUserId());
    final Future<List<Comment>> findAllComments = repository.findAll(new CommentSelector("123", ItemType.COMMENT));
    final Future<Optional<Comment>> updateComment = repository.updateOne(updateCommentInput);

    // then
    CompositeFuture.any(addComment, findAllComments, updateComment).setHandler(event -> {
      if (event.succeeded()) {
        context.failNow(new AssertionError("All queries should fail!"));
      } else {
        context.completeNow();
      }
    });
  }

  private static boolean isInList(final Comment comment, final List<Comment> list) {
    return list.stream().anyMatch(commentFromList ->
        // ignores timestamps and id
        comment.getContent().equals(commentFromList.getContent()) &&
        comment.getItemId().equals(commentFromList.getItemId()) &&
        comment.getItemType().equals(commentFromList.getItemType()));
  }
}
