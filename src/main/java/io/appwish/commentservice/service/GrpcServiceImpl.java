package io.appwish.commentservice.service;

import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.AllCommentQuery;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.model.reply.CommentDeleteReply;
import io.appwish.commentservice.model.reply.CommentReply;
import io.appwish.grpc.AllCommentQueryProto;
import io.appwish.grpc.AllCommentReplyProto;
import io.appwish.grpc.CommentDeleteReplyProto;
import io.appwish.grpc.CommentInputProto;
import io.appwish.grpc.CommentProto;
import io.appwish.grpc.CommentQueryProto;
import io.appwish.grpc.CommentReplyProto;
import io.appwish.grpc.CommentServiceGrpc;
import io.appwish.grpc.UpdateCommentInputProto;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.badata.protobuf.converter.Converter;

/**
 * Handles gRPC server request calls. Sends request on event bus to interact with comment data in the
 * database.
 */
public class GrpcServiceImpl extends CommentServiceGrpc.CommentServiceVertxImplBase {

  private final EventBus eventBus;
  private final Converter converter;

  public GrpcServiceImpl(final EventBus eventBus) {
    this.eventBus = eventBus;
    this.converter = Converter.create();
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.getAllComment(...)
   */
  @Override
  public void getAllComment(final AllCommentQueryProto request, final Promise<AllCommentReplyProto> response) {

    eventBus.<List<Comment>>request(
        Address.FIND_ALL_COMMENTS.get(), converter.toDomain(AllCommentQuery.class, request),
        event -> {
          if (event.succeeded()) {
            final List<CommentProto> collect = event.result().body().stream()
                .map(it -> converter.toProtobuf(CommentProto.class, it))
                .collect(Collectors.toList());
            response.complete(AllCommentReplyProto.newBuilder().addAllComments(collect).build());
          } else {
            response.fail(event.cause());
          }
        });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.addComment(...)
   */
  @Override
  public void createComment(final CommentInputProto request, final Promise<CommentReplyProto> response) {

    eventBus.<Comment>request(
        Address.CREATE_ONE_COMMENT.get(), converter.toDomain(CommentInput.class, request),
        event -> {
          if (event.succeeded()) {
            response.complete(converter
                .toProtobuf(CommentReplyProto.class, new CommentReply(event.result().body())));
          } else {
            response.fail(event.cause());
          }
        });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.updateComment(...)
   */
  @Override
  public void updateComment(final UpdateCommentInputProto request, final Promise<CommentReplyProto> response) {

    eventBus.<Optional<Comment>>request(
        Address.UPDATE_ONE_COMMENT.get(), converter.toDomain(UpdateCommentInput.class, request),
        event -> {
          if (event.succeeded() && event.result().body().isPresent()) {
            response.complete(converter.toProtobuf(CommentReplyProto.class, new CommentReply(event.result().body().get())));
          } else if (event.succeeded()) {
            response.complete();
          } else {
            response.fail(event.cause());
          }
        });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.deleteComment(...)
   */
  @Override
  public void deleteComment(final CommentQueryProto request,
      final Promise<CommentDeleteReplyProto> response) {

    eventBus.<Boolean>request(
        Address.DELETE_ONE_COMMENT.get(), converter.toDomain(CommentQuery.class, request),
        event -> {
          if (event.succeeded()) {
            response.complete(converter.toProtobuf(CommentDeleteReplyProto.class,
                new CommentDeleteReply(event.result().body())));
          } else {
            response.fail(event.cause());
          }
        });
  }
}
