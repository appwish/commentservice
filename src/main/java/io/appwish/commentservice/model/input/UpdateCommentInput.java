package io.appwish.commentservice.model.input;

import io.appwish.grpc.UpdateCommentInputProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;


/**
 * This type should be used as input for updates of comments in the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(UpdateCommentInputProto.class)
public class UpdateCommentInput {

  @ProtoField
  private long id;

  @ProtoField
  private String content;

  public UpdateCommentInput(final long id, final String content) {
    this.id = id;
    this.content = content;
  }

  public UpdateCommentInput() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateCommentInput that = (UpdateCommentInput) o;
    return id == that.id &&
        content.equals(that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, content);
  }

  @Override
  public String toString() {
    return "UpdateCommentInput{" +
        "id=" + id +
        ", content='" + content + '\'' +
        '}';
  }
}
