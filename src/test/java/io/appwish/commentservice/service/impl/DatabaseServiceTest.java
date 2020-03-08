package io.appwish.commentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appwish.commentservice.TestData;
import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.eventbus.EventBusConfigurer;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.AllCommentQuery;
import io.appwish.commentservice.repository.CommentRepository;
import io.appwish.commentservice.service.DatabaseService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseServiceTest {

  private DatabaseService databaseService;
  private CommentRepository commentRepository;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) {
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    commentRepository = mock(CommentRepository.class);
    databaseService = new DatabaseService(vertx.eventBus(), commentRepository);
    databaseService.registerEventBusEventHandlers();
    util.registerCodecs();
    context.completeNow();
  }

  @Test
  void should_reply_all_comments(final Vertx vertx, final VertxTestContext context) {
    // given
    when(commentRepository.findAll(TestData.ALL_COMMENT_QUERY))
      .thenReturn(Future.succeededFuture(TestData.COMMENTS));

    // when
    vertx.eventBus().<List<Comment>>request(Address.FIND_ALL_COMMENTS.get(), TestData.ALL_COMMENT_QUERY,
      event -> {

        // then
        context.verify(() -> {
          assertEquals(TestData.COMMENTS, event.result().body());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_getting_all_comments(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(commentRepository.findAll(TestData.ALL_COMMENT_QUERY))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<AllCommentQuery>request(Address.FIND_ALL_COMMENTS.get(), TestData.ALL_COMMENT_QUERY,
      event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_reply_added_comment(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(commentRepository.addOne(TestData.COMMENT_INPUT_1))
      .thenReturn(Future.succeededFuture(TestData.COMMENT_4));

    // when
    vertx.eventBus().<Comment>request(Address.CREATE_ONE_COMMENT.get(), TestData.COMMENT_INPUT_1, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.COMMENT_4, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_adding_comment(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(commentRepository.addOne(TestData.COMMENT_INPUT_1))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Comment>request(Address.CREATE_ONE_COMMENT.get(), TestData.COMMENT_INPUT_1, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_true_if_deleted_comment(final Vertx vertx, final VertxTestContext context) {
    // given
    when(commentRepository.deleteOne(TestData.COMMENT_QUERY))
      .thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_COMMENT.get(), TestData.COMMENT_QUERY, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertEquals(true, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_false_if_not_deleted_comment(final Vertx vertx, final VertxTestContext context) {
    // given
    when(commentRepository.deleteOne(TestData.COMMENT_QUERY))
      .thenReturn(Future.succeededFuture(false));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_COMMENT.get(),
      TestData.COMMENT_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertEquals(false, event.result().body());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_deleting_comment(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(commentRepository.deleteOne(TestData.COMMENT_QUERY))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_COMMENT.get(),
      TestData.COMMENT_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_updated_comment(final Vertx vertx, final VertxTestContext context) {
    // given
    final UpdateCommentInput input = TestData.UPDATE_COMMENT_INPUT;
    when(commentRepository.updateOne(input))
      .thenReturn(Future.succeededFuture(Optional.of(TestData.COMMENT_4)));

    // when
    vertx.eventBus().<Optional<Comment>>request(Address.UPDATE_ONE_COMMENT.get(), input, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.COMMENT_4, event.result().body().get());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_empty_if_not_updated(final Vertx vertx, final VertxTestContext context) {
    // given
    when(commentRepository.updateOne(TestData.UPDATE_COMMENT_INPUT)).thenReturn(Future.succeededFuture(Optional.empty()));

    // when
    vertx.eventBus().<Optional<Comment>>request(Address.UPDATE_ONE_COMMENT.get(), TestData.UPDATE_COMMENT_INPUT, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertTrue(event.result().body().isEmpty());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_updating_comment(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(commentRepository.updateOne(TestData.UPDATE_COMMENT_INPUT)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Optional<Comment>>request(Address.UPDATE_ONE_COMMENT.get(), TestData.UPDATE_COMMENT_INPUT, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }
}
