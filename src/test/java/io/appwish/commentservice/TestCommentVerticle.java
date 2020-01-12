package io.appwish.commentservice;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class TestCommentVerticle {

  @BeforeEach
  void verticle_deployed_and_grpc_server_started(final Vertx vertx, final VertxTestContext testContext) {
    vertx.deployVerticle(new CommentVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
