package io.appwish.commentservice.model.reply;

import io.appwish.grpc.CommentDeleteReplyProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * Represents data to return for delete wish query.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(CommentDeleteReplyProto.class)
public class CommentDeleteReply {

  @ProtoField
  private boolean deleted;

  public CommentDeleteReply(boolean deleted) {
    this.deleted = deleted;
  }

  public CommentDeleteReply() {
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentDeleteReply that = (CommentDeleteReply) o;
    return deleted == that.deleted;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deleted);
  }
}
