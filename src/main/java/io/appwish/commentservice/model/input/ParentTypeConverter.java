package io.appwish.commentservice.model.input;

import io.appwish.grpc.ParentTypeProto;
import net.badata.protobuf.converter.type.TypeConverter;

public class ParentTypeConverter implements TypeConverter<ParentType, ParentTypeProto> {

  @Override
  public ParentType toDomainValue(final Object instance) {
    if (instance == ParentTypeProto.WISH) {
      return ParentType.WISH;
    }

    if (instance == ParentTypeProto.COMMENT) {
      return ParentType.COMMENT;
    }

    throw new IllegalArgumentException("Instance is none of valid values of VoteTypeProto");
  }

  @Override
  public ParentTypeProto toProtobufValue(final Object instance) {
    if (instance == ParentType.WISH) {
      return ParentTypeProto.WISH;
    }

    if (instance == ParentType.COMMENT) {
      return ParentTypeProto.COMMENT;
    }

    throw new IllegalArgumentException("Instance is none of valid values of VoteType");
  }

}
