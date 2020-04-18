package io.appwish.commentservice.verticle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.grpc.CommentSelectorProto;
import io.appwish.grpc.CommentServiceGrpc;
import io.appwish.commentservice.TestData;
import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.eventbus.Codec;
import io.appwish.commentservice.eventbus.EventBusConfigurer;
import io.grpc.ManagedChannel;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.badata.protobuf.converter.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class GrpcVerticleTest {

  @Test
  void should_expose_grpc_server(final Vertx vertx, final VertxTestContext context) {
    // given
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    final ManagedChannel channel = VertxChannelBuilder.forAddress(vertx, TestData.APP_HOST, TestData.APP_PORT).usePlaintext(true).build();
    final CommentServiceGrpc.CommentServiceVertxStub serviceStub = new CommentServiceGrpc.CommentServiceVertxStub(channel);
    vertx.deployVerticle(new GrpcVerticle(new JsonObject().put("appHost", "0.0.0.0").put("appPort", 8080)), new DeploymentOptions(), context.completing());
    vertx.eventBus().consumer(Address.FIND_ALL_COMMENTS.get(), event -> event.reply(TestData.COMMENTS, new DeliveryOptions().setCodecName(Codec.COMMENT.getCodecName())));

    util.registerCodecs();

    // when
    serviceStub.getAllComment(Converter.create().toProtobuf(CommentSelectorProto.class, TestData.COMMENT_SELECTOR), event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }
}
