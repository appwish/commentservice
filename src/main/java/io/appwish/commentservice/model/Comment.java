package io.appwish.commentservice.model;

import com.google.protobuf.Timestamp;
import io.appwish.commentservice.model.input.ParentType;
import io.appwish.commentservice.model.input.ParentTypeConverter;
import io.appwish.grpc.CommentProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(CommentProto.class)
public class Comment {

  @ProtoField
  private long id;

  @ProtoField
  private long userId;

  @ProtoField
  private long parentId;

  @ProtoField(converter = ParentTypeConverter.class)
  private ParentType parentType;

  @ProtoField
  private String content;

  @ProtoField
  private Timestamp createdAt;

  @ProtoField
  private Timestamp updatedAt;

  public Comment(long id, long userId, long parentId,
      ParentType parentType, String content, Timestamp createdAt,
      Timestamp updatedAt) {
    this.id = id;
    this.userId = userId;
    this.parentId = parentId;
    this.parentType = parentType;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Comment() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
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

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Comment comment = (Comment) o;
    return id == comment.id &&
        userId == comment.userId &&
        parentId == comment.parentId &&
        parentType == comment.parentType &&
        content.equals(comment.content) &&
        createdAt.equals(comment.createdAt) &&
        updatedAt.equals(comment.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, parentId, parentType, content, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "Comment{" +
        "id=" + id +
        ", userId=" + userId +
        ", parentId=" + parentId +
        ", parentType=" + parentType +
        ", content='" + content + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}
