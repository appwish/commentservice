package io.appwish.commentservice;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

/**
 * Class: CommentVerticle
 */
public class CommentVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(CommentVerticle.class);

  private static final String APP_PORT = "appPort";
  private static final String APP_HOST = "appHost";

  @Override
  public void start(Promise<Void> startPromise) {

    final ConfigRetriever retriever = ConfigRetriever.create(vertx);

    retriever.getConfig(json -> {
      final JsonObject config = json.result();
      final String appHost = config.getString(APP_HOST);
      final Integer appPort = config.getInteger(APP_PORT);

      final VertxServer server = VertxServerBuilder
              .forAddress(vertx, appHost, appPort)
              .build();

      server.start(ar -> {
        if(ar.succeeded()) {
          LOG.info("CommentService gRPC server started on port: " + appPort);
          startPromise.complete();
        } else {
          LOG.error("Could not start CommentService gRPC server: " + ar.cause().getMessage());
          startPromise.fail(ar.cause());
        }
      });
    });
  }
}
