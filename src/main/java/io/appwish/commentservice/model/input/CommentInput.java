package io.appwish.commentservice.model.input;

import io.appwish.grpc.CommentInputProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used for inserting new comments to the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(CommentInputProto.class)
public class CommentInput {

  @ProtoField
  private long parentId;

  @ProtoField(converter = ParentTypeConverter.class)
  private ParentType parentType;

  @ProtoField
  private String content;

  public CommentInput(long parentId, ParentType parentType, String content) {
    this.parentId = parentId;
    this.parentType = parentType;
    this.content = content;
  }

  public CommentInput() {
  }

  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  public ParentType getParentType() {
    return parentType;
  }

  public void setParentType(ParentType parentType) {
    this.parentType = parentType;
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
    CommentInput that = (CommentInput) o;
    return parentId == that.parentId &&
        parentType.equals(that.parentType) &&
        content.equals(that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, parentType, content);
  }

  @Override
  public String toString() {
    return "CommentInput{" +
        "parentId=" + parentId +
        ", parentType=" + parentType +
        ", content='" + content + '\'' +
        '}';
  }
}
