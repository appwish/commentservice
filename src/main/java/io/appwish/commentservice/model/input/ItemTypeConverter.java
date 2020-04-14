package io.appwish.commentservice.model.input;

import io.appwish.grpc.ItemTypeProto;
import net.badata.protobuf.converter.type.TypeConverter;

public class ItemTypeConverter implements TypeConverter<ItemType, ItemTypeProto> {

  @Override
  public ItemType toDomainValue(final Object instance) {
    if (instance == ItemTypeProto.WISH) {
      return ItemType.WISH;
    }

    if (instance == ItemTypeProto.COMMENT) {
      return ItemType.COMMENT;
    }

    throw new IllegalArgumentException("Instance is none of valid values of VoteTypeProto");
  }

  @Override
  public ItemTypeProto toProtobufValue(final Object instance) {
    if (instance == ItemType.WISH) {
      return ItemTypeProto.WISH;
    }

    if (instance == ItemType.COMMENT) {
      return ItemTypeProto.COMMENT;
    }

    throw new IllegalArgumentException("Instance is none of valid values of VoteType");
  }

}
