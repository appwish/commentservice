package io.appwish.commentservice.repository;

import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.CommentQuery;
import io.appwish.commentservice.model.query.CommentSelector;
import io.vertx.core.Future;
import java.util.List;
import java.util.Optional;

/**
 * Interface for interaction with comment persistence layer
 */
public interface CommentRepository {

  Future<List<Comment>> findAll(final CommentSelector query);

  Future<Comment> addOne(final CommentInput input, final String userId);

  Future<Boolean> deleteOne(final CommentQuery query);

  Future<Optional<Comment>> updateOne(final UpdateCommentInput input);

  Future<Boolean> isAuthor(final CommentQuery query, final String userId);
}
