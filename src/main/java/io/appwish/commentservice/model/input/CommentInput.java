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
  private long itemId;

  @ProtoField(converter = ItemTypeConverter.class)
  private ItemType itemType;

  @ProtoField
  private String content;

  public CommentInput(long itemId, ItemType itemType, String content) {
    this.itemId = itemId;
    this.itemType = itemType;
    this.content = content;
  }

  public CommentInput() {
  }

  public long getItemId() {
    return itemId;
  }

  public void setItemId(long itemId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentInput that = (CommentInput) o;
    return itemId == that.itemId &&
        itemType.equals(that.itemType) &&
        content.equals(that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, itemType, content);
  }

  @Override
  public String toString() {
    return "CommentInput{" +
        "itemId=" + itemId +
        ", itemType=" + itemType +
        ", content='" + content + '\'' +
        '}';
  }
}
