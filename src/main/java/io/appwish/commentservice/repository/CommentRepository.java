package io.appwish.commentservice.repository;

import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.AllCommentQuery;
import io.appwish.commentservice.model.query.CommentQuery;
import io.vertx.core.Future;
import java.util.List;
import java.util.Optional;

/**
 * Interface for interaction with comment persistence layer
 */
public interface CommentRepository {

  Future<List<Comment>> findAll(final AllCommentQuery query);

  Future<Comment> addOne(final CommentInput input);

  Future<Boolean> deleteOne(final CommentQuery query);

  Future<Optional<Comment>> updateOne(final UpdateCommentInput input);
}
