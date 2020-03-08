package io.appwish.commentservice.model.reply;

import io.appwish.grpc.CommentReplyProto;
import io.appwish.commentservice.model.Comment;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * Represents data to return for single comment query.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(CommentReplyProto.class)
public class CommentReply {

  @ProtoField
  private Comment comment;

  public CommentReply(final Comment comment) {
    this.comment = comment;
  }

  public CommentReply() {
  }

  public Comment getComment() {
    return comment;
  }

  public void setComment(final Comment comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentReply commentReply = (CommentReply) o;
    return Objects.equals(comment, commentReply.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(comment);
  }

  @Override
  public String toString() {
    return "CommentReply{" +
      "comment=" + comment +
      '}';
  }
}
