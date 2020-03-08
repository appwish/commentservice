package io.appwish.commentservice.verticle;

import io.appwish.commentservice.repository.CommentRepository;
import io.appwish.commentservice.service.DatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * Verticle responsible for database access. Registers DatabaseService to expose the database on the
 * event bus.
 */
public class DatabaseVerticle extends AbstractVerticle {

  private final CommentRepository commentRepository;

  public DatabaseVerticle(final CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final DatabaseService databaseService = new DatabaseService(vertx.eventBus(), commentRepository);
    databaseService.registerEventBusEventHandlers();
    startPromise.complete();
  }
}
