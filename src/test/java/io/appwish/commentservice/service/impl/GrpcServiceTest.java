package io.appwish.commentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.commentservice.TestData;
import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.eventbus.Codec;
import io.appwish.commentservice.eventbus.EventBusConfigurer;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.reply.CommentDeleteReply;
import io.appwish.commentservice.service.GrpcServiceImpl;
import io.appwish.grpc.AllCommentQueryProto;
import io.appwish.grpc.AllCommentReplyProto;
import io.appwish.grpc.CommentDeleteReplyProto;
import io.appwish.grpc.CommentInputProto;
import io.appwish.grpc.CommentQueryProto;
import io.appwish.grpc.CommentReplyProto;
import io.appwish.grpc.ParentTypeProto;
import io.appwish.grpc.UpdateCommentInputProto;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Optional;
import java.util.stream.Collectors;
import net.badata.protobuf.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class GrpcServiceTest {

  private GrpcServiceImpl grpcService;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) {
    final EventBusConfigurer eventBusConfigurer = new EventBusConfigurer(vertx.eventBus());
    grpcService = new GrpcServiceImpl(vertx.eventBus());
    eventBusConfigurer.registerCodecs();
    context.completeNow();
  }

  @Test
  void should_return_all_comments(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<AllCommentReplyProto> promise = Promise.promise();
    final AllCommentQueryProto query = AllCommentQueryProto.newBuilder().build();
    vertx.eventBus().consumer(Address.FIND_ALL_COMMENTS.get(), event -> {
      event.reply(TestData.COMMENTS, new DeliveryOptions().setCodecName(Codec.COMMENT.getCodecName()));
    });

    // when
    grpcService.getAllComment(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.COMMENTS, promise.future().result().getCommentsList().stream()
          .map(it -> Converter.create().toDomain(Comment.class, it)).collect(Collectors.toList()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_when_exception_occured_while_getting_all_comments(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<AllCommentReplyProto> promise = Promise.promise();
    final AllCommentQueryProto query = AllCommentQueryProto.newBuilder().build();
    vertx.eventBus().consumer(Address.FIND_ALL_COMMENTS.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.getAllComment(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_add_and_return_back_comment(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentReplyProto> promise = Promise.promise();
    final CommentInputProto inputProto = CommentInputProto.newBuilder()
        .setContent(TestData.SOME_COMMENT_CONTENT)
        .setParentId(TestData.SOME_PARENT_ID)
        .setParentType(ParentTypeProto.WISH)
        .build();
    vertx.eventBus().consumer(Address.CREATE_ONE_COMMENT.get(), event -> {
      event.reply(TestData.COMMENT_1);
    });

    // when
    grpcService.createComment(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.COMMENT_1,
          Converter.create().toDomain(Comment.class, promise.future().result().getComment()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_report_error_while_error_creating_comment(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentReplyProto> promise = Promise.promise();
    final CommentInputProto inputProto = CommentInputProto.newBuilder()
        .setContent(TestData.SOME_COMMENT_CONTENT)
        .setParentId(TestData.SOME_PARENT_ID)
        .setParentType(ParentTypeProto.WISH)
        .build();
    vertx.eventBus().consumer(Address.CREATE_ONE_COMMENT.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.createComment(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_update_and_return_updated_comment(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentReplyProto> promise = Promise.promise();
    final UpdateCommentInputProto updateCommentInputProto = UpdateCommentInputProto.newBuilder()
        .setContent(TestData.COMMENT_3.getContent())
        .setId(TestData.COMMENT_3.getId())
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_COMMENT.get(), event -> {
      event.reply(Optional.of(TestData.COMMENT_3),
        new DeliveryOptions().setCodecName(Codec.COMMENT.getCodecName()));
    });

    // when
    grpcService.updateComment(updateCommentInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(Converter.create().toDomain(Comment.class, promise.future().result().getComment()),
          TestData.COMMENT_3);
        context.completeNow();
      });

    });
  }

  @Test
  void should_not_update_and_return_empty_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentReplyProto> promise = Promise.promise();
    final UpdateCommentInputProto updateCommentInputProto = UpdateCommentInputProto.newBuilder()
        .setContent(TestData.COMMENT_3.getContent())
        .setId(TestData.COMMENT_3.getId())
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_COMMENT.get(), event -> {
      event.reply(Optional.empty(), new DeliveryOptions().setCodecName(Codec.COMMENT.getCodecName()));
    });

    // when
    grpcService.updateComment(updateCommentInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_updating_comment(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentReplyProto> promise = Promise.promise();
    final UpdateCommentInputProto updateCommentInputProto = UpdateCommentInputProto.newBuilder()
      .setId(TestData.COMMENT_3.getId())
      .setContent(TestData.COMMENT_3.getContent())
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_COMMENT.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.updateComment(updateCommentInputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });

    });
  }

  @Test
  void should_delete_comment_and_return_true(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentDeleteReplyProto> promise = Promise.promise();
    final CommentQueryProto query = CommentQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.DELETE_ONE_COMMENT.get(), event -> {
      event.reply(true);
    });

    // when
    grpcService.deleteComment(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertTrue(promise.future().result().getDeleted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_not_delete_and_return_false_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentDeleteReplyProto> promise = Promise.promise();
    final CommentQueryProto query = CommentQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.DELETE_ONE_COMMENT.get(), event -> {
      event.reply(false);
    });

    // when
    grpcService.deleteComment(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertFalse(promise.future().result().getDeleted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_deleting_comment(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<CommentDeleteReplyProto> promise = Promise.promise();
    final CommentQueryProto query = CommentQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ALL_COMMENTS.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.deleteComment(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }
}
