package io.appwish.commentservice.verticle;

import io.appwish.commentservice.interceptor.ExceptionDetailsInterceptor;
import io.appwish.commentservice.interceptor.UserContextInterceptor;
import io.appwish.commentservice.service.GrpcServiceImpl;
import io.grpc.BindableService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

/**
 * Verticle responsible for spinning up the gRPC server.
 */
public class GrpcVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcVerticle.class.getName());
  private static final String APP_PORT = "appPort";
  private static final String APP_HOST = "appHost";

  private final JsonObject config;

  public GrpcVerticle(final JsonObject config) {
    this.config = config;
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final BindableService grpcCommentService = new GrpcServiceImpl(vertx.eventBus());
    final String appHost = config.getString(APP_HOST);
    final Integer appPort = config.getInteger(APP_PORT);

    final VertxServer server = VertxServerBuilder
        .forAddress(vertx, appHost, appPort)
        .intercept(new UserContextInterceptor())
        .addService(grpcCommentService)
        .intercept(new ExceptionDetailsInterceptor())
        .build();

    server.start(asyncResult -> {
      if (asyncResult.succeeded()) {
        LOG.info("GrpcServiceImpl gRPC server started on host=" + appHost + " and port=" + appPort);
        startPromise.complete();
      } else {
        LOG.error(
          "Could not start GrpcServiceImpl gRPC server: " + asyncResult.cause().getMessage());
        startPromise.fail(asyncResult.cause());
      }
    });
  }
}
