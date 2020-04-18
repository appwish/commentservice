package io.appwish.commentservice.service;

import static java.util.Objects.isNull;

import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.eventbus.Codec;
import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.model.query.CommentSelector;
import io.appwish.commentservice.repository.CommentRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import java.util.List;
import java.util.Optional;

/**
 * Exposes the comment repository on the event bus. Takes data from the comment repository and replies to requests on the event bus.
 */
public class DatabaseService {

  private static final String USER_ID = "userId";

  private final EventBus eventBus;
  private final CommentRepository commentRepository;

  public DatabaseService(final EventBus eventBus, final CommentRepository commentRepository) {
    this.eventBus = eventBus;
    this.commentRepository = commentRepository;
  }

  public void registerEventBusEventHandlers() {
    eventBus.<CommentSelector>consumer(Address.FIND_ALL_COMMENTS.get())
        .handler(event -> commentRepository.findAll(event.body()).setHandler(findAllHandler(event)));

    eventBus.<CommentInput>consumer(Address.CREATE_ONE_COMMENT.get())
        .handler(event -> {
          final String userId = event.headers().get(USER_ID);

          if (isNull(userId)) {
            event.fail(1, "Only authenticated users can add comments.");
            return;
          }

          commentRepository.addOne(event.body(), userId).setHandler(addOneHandler(event));
        });

    eventBus.<UpdateCommentInput>consumer(Address.UPDATE_ONE_COMMENT.get())
        .handler(event -> {
          final String userId = event.headers().get(USER_ID);

          if (isNull(userId)) {
            event.fail(1, "Only authenticated users can update comments.");
            return;
          }

          commentRepository.isAuthor(new CommentQuery(event.body().getId()), userId)
              .onSuccess(isAuthor -> {
                if (isAuthor) {
                  commentRepository.updateOne(event.body()).setHandler(updateOneHandler(event));
                }
              })
              .onFailure(failure -> event.fail(1, failure.getMessage()));
        });

    eventBus.<CommentQuery>consumer(Address.DELETE_ONE_COMMENT.get())
        .handler(event -> commentRepository.isAuthor(event.body(), event.headers().get("userId")).onComplete(deleted -> {
          if (deleted.result()) {
            commentRepository.deleteOne(event.body()).setHandler(deleteOneHandler(event));
          } else {
            event.fail(1, "User not an author of comment");
          }
        }));
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

  private Handler<AsyncResult<List<Comment>>> findAllHandler(final Message<CommentSelector> event) {
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
