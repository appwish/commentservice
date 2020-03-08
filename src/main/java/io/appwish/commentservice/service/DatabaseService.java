package io.appwish.commentservice.service;

import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.eventbus.Codec;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.query.AllCommentQuery;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.repository.CommentRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import java.util.List;
import java.util.Optional;

/**
 * Exposes the comment repository on the event bus. Takes data from the comment repository and replies to
 * requests on the event bus.
 */
public class DatabaseService {

  private final EventBus eventBus;
  private final CommentRepository commentRepository;

  public DatabaseService(final EventBus eventBus, final CommentRepository commentRepository) {
    this.eventBus = eventBus;
    this.commentRepository = commentRepository;
  }

  public void registerEventBusEventHandlers() {
    eventBus.<AllCommentQuery>consumer(Address.FIND_ALL_COMMENTS.get())
      .handler(event -> commentRepository.findAll(event.body()).setHandler(findAllHandler(event)));

    eventBus.<CommentInput>consumer(Address.CREATE_ONE_COMMENT.get())
      .handler(event -> commentRepository.addOne(event.body()).setHandler(addOneHandler(event)));

    eventBus.<UpdateCommentInput>consumer(Address.UPDATE_ONE_COMMENT.get())
      .handler(event -> commentRepository.updateOne(event.body()).setHandler(updateOneHandler(event)));

    eventBus.<CommentQuery>consumer(Address.DELETE_ONE_COMMENT.get())
      .handler(event -> commentRepository.deleteOne(event.body()).setHandler(deleteOneHandler(event)));
  }

  private Handler<AsyncResult<Boolean>> deleteOneHandler(final Message<CommentQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, query.cause().getMessage());
      }
    };
  }

  private Handler<AsyncResult<Optional<Comment>>> updateOneHandler(
    final Message<UpdateCommentInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(), new DeliveryOptions().setCodecName(Codec.COMMENT.getCodecName()));
      } else {
        event.fail(1, query.cause().getMessage());
      }
    };
  }

  private Handler<AsyncResult<Comment>> addOneHandler(final Message<CommentInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, query.cause().getMessage());
      }
    };
  }

  private Handler<AsyncResult<List<Comment>>> findAllHandler(final Message<AllCommentQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(),
          new DeliveryOptions().setCodecName(Codec.COMMENT.getCodecName()));
      } else {
        event.fail(1, query.cause().getMessage());
      }
    };
  }
}
