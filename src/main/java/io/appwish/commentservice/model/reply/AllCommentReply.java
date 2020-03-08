package io.appwish.commentservice.model.reply;

import io.appwish.grpc.AllCommentReplyProto;
import io.appwish.commentservice.model.Comment;
import java.util.List;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * Represents data to return for multiple comments query. Right now it contains just a list of comments,
 * but later we'll add pagination etc.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(AllCommentReplyProto.class)
public class AllCommentReply {

  @ProtoField
  private List<Comment> comments;

  public AllCommentReply(final List<Comment> comments) {
    this.comments = comments;
  }

  public AllCommentReply() {
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(final List<Comment> comments) {
    this.comments = comments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AllCommentReply that = (AllCommentReply) o;
    return comments.equals(that.comments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(comments);
  }

  @Override
  public String toString() {
    return "AllCommentReply{" +
      "comments=" + comments +
      '}';
  }
}
