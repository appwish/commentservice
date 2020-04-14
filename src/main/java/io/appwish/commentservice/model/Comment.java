package io.appwish.commentservice.model;

import com.google.protobuf.Timestamp;
import io.appwish.commentservice.model.input.ItemType;
import io.appwish.commentservice.model.input.ItemTypeConverter;
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
  private String userId;

  @ProtoField
  private String itemId;

  @ProtoField(converter = ItemTypeConverter.class)
  private ItemType itemType;

  @ProtoField
  private String content;

  @ProtoField
  private Timestamp createdAt;

  @ProtoField
  private Timestamp updatedAt;

  public Comment(long id, String userId, String itemId,
      ItemType itemType, String content, Timestamp createdAt,
      Timestamp updatedAt) {
    this.id = id;
    this.userId = userId;
    this.itemId = itemId;
    this.itemType = itemType;
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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
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
        itemId == comment.itemId &&
        itemType == comment.itemType &&
        content.equals(comment.content) &&
        createdAt.equals(comment.createdAt) &&
        updatedAt.equals(comment.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, itemId, itemType, content, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "Comment{" +
        "id=" + id +
        ", userId=" + userId +
        ", parentId=" + itemId +
        ", parentType=" + itemType +
        ", content='" + content + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}
