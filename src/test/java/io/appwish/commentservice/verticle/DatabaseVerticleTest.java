package io.appwish.commentservice.verticle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appwish.commentservice.TestData;
import io.appwish.commentservice.eventbus.Address;
import io.appwish.commentservice.eventbus.EventBusConfigurer;
import io.appwish.commentservice.repository.CommentRepository;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseVerticleTest {

  @Test
  void should_expose_database_service(final Vertx vertx, final VertxTestContext context) {
    // given
    final CommentRepository repository = mock(CommentRepository.class);
    final DatabaseVerticle verticle = new DatabaseVerticle(repository);
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    when(repository.findAll(TestData.COMMENT_SELECTOR)).thenReturn(Future.succeededFuture(TestData.COMMENTS));

    util.registerCodecs();

    // when
    vertx.deployVerticle(verticle, new DeploymentOptions(), context.succeeding());

    vertx.eventBus().request(Address.FIND_ALL_COMMENTS.get(), TestData.COMMENT_SELECTOR, event -> {
      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertEquals(TestData.COMMENTS, event.result().body());
        context.completeNow();
      });

    });
  }
}
