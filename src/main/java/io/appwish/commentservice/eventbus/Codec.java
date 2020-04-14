package io.appwish.commentservice.eventbus;

import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.model.query.CommentSelector;
import io.appwish.commentservice.model.reply.AllCommentReply;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

/**
 * These codecs can be used to enable passing custom Java objects on the local event bus.
 *
 * To enable T type to be passed via the event bus, just create a new {@link LocalReferenceCodec}.
 *
 * It's not enough to add the codec here - you need to register them on the event bus using {@link
 * EventBus#registerCodec(MessageCodec)}.
 */
public enum Codec {
  UPDATE_COMMENT_INPUT(new LocalReferenceCodec<>(UpdateCommentInput.class)),
  COMMENT(new LocalReferenceCodec<>(Comment.class)),
  ALL_COMMENT_REPLY(new LocalReferenceCodec<>(AllCommentReply.class)),
  ALL_COMMENT_QUERY(new LocalReferenceCodec<>(CommentSelector.class)),
  COMMENT_QUERY(new LocalReferenceCodec<>(CommentQuery.class)),
  COMMENT_INPUT(new LocalReferenceCodec<>(CommentInput.class));

  private final LocalReferenceCodec codec;

  Codec(final LocalReferenceCodec codec) {
    this.codec = codec;
  }

  public <T> LocalReferenceCodec<T> getCodec() {
    return codec;
  }

  public String getCodecName() {
    return codec.name();
  }
}
