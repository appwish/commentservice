package io.appwish.commentservice.model.query;

import io.appwish.commentservice.model.input.ParentType;
import io.appwish.commentservice.model.input.ParentTypeConverter;
import io.appwish.grpc.AllCommentQueryProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used to query multiple objects from the database. Right now it's empty but in
 * the future it may contain information about pagination, sorting etc.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(AllCommentQueryProto.class)
public class AllCommentQuery {

  @ProtoField
  private long parentId;

  @ProtoField(converter = ParentTypeConverter.class)
  private ParentType parentType;

  public AllCommentQuery(final long parentId, final ParentType parentType) {
    this.parentId = parentId;
    this.parentType = parentType;
  }

  public AllCommentQuery() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AllCommentQuery that = (AllCommentQuery) o;
    return parentId == that.parentId &&
        parentType == that.parentType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, parentType);
  }

  @Override
  public String toString() {
    return "AllCommentQuery{" +
        "parentId=" + parentId +
        ", parentType=" + parentType +
        '}';
  }
}
