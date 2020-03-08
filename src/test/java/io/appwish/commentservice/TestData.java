package io.appwish.commentservice;

import io.appwish.commentservice.model.Comment;
import io.appwish.commentservice.model.input.CommentInput;
import io.appwish.commentservice.model.input.ParentType;
import io.appwish.commentservice.model.input.UpdateCommentInput;
import io.appwish.commentservice.model.query.AllCommentQuery;
import io.appwish.commentservice.model.query.CommentQuery;
import java.time.LocalDateTime;
import java.util.List;

import com.google.protobuf.Timestamp;

/**
 * Class for constant test data/values to be used in test classes to avoid duplication /
 * boilerplate
 */
public final class TestData {

  /**
   * Represents app address that should be used during the tests
   */
  public static final String APP_HOST = "localhost";

  /**
   * Represents app port that should be used during the tests to avoid ports conflicts
   */
  public static final int APP_PORT = 8281;

  /**
   * Some random values to be used to fill comment fields in tests
   * */
  public static final long SOME_ID = 1;
  public static final int SOME_USER_ID = 12345;
  public static final int SOME_PARENT_ID = 123456;
  public static final ParentType SOME_PARENT_TYPE = ParentType.WISH;
  public static final String SOME_COMMENT_CONTENT = "Some comment content";

  /**
   * Some random error message
   */
  public static final String ERROR_MESSAGE = "Something went wrong";

  /**
   * Use this in test for IDs that you assume do not exist in database
   */
  public static final long NON_EXISTING_ID = 1411223L;

  /**
   * Sample timestamp
   */
  public static final Timestamp SOME_TIMESTAMP = Timestamp.newBuilder().setNanos(LocalDateTime.of(2020, 2, 1, 9, 0).getNano()).build();

  /**
   * Comments to be reused in tests
   */
  public static final Comment COMMENT_1 = new Comment(SOME_ID, SOME_USER_ID, SOME_PARENT_ID, SOME_PARENT_TYPE, SOME_COMMENT_CONTENT, SOME_TIMESTAMP, SOME_TIMESTAMP);
  public static final Comment COMMENT_2 = new Comment(12, 22, 32, ParentType.WISH, "Content 2", SOME_TIMESTAMP, SOME_TIMESTAMP);
  public static final Comment COMMENT_3 = new Comment(13, 23, 33, ParentType.WISH, "Content 3", SOME_TIMESTAMP, SOME_TIMESTAMP);
  public static final Comment COMMENT_4 = new Comment(14, 24, 34, ParentType.COMMENT, "Content 4", SOME_TIMESTAMP, SOME_TIMESTAMP);

  /**
   * List of random comments to be used in tests
   */
  public static final List<Comment> COMMENTS = List.of(COMMENT_1, COMMENT_2, COMMENT_3, COMMENT_4);

  /**
   * All comments query to be used in tests
   */
  public static final AllCommentQuery ALL_COMMENT_QUERY = new AllCommentQuery();

  /**
   * Some random inputs to be used in tests
   */
  public static final CommentInput COMMENT_INPUT_1 = new CommentInput(
    TestData.COMMENT_1.getParentId(),
    TestData.COMMENT_1.getParentType(),
    TestData.COMMENT_1.getContent());
  public static final CommentInput COMMENT_INPUT_2 = new CommentInput(
    TestData.COMMENT_2.getParentId(),
    TestData.COMMENT_2.getParentType(),
    TestData.COMMENT_2.getContent());
  public static final CommentInput COMMENT_INPUT_3 = new CommentInput(
    TestData.COMMENT_3.getParentId(),
    TestData.COMMENT_3.getParentType(),
    TestData.COMMENT_3.getContent());
  public static final CommentInput COMMENT_INPUT_4 = new CommentInput(
    TestData.COMMENT_4.getParentId(),
    TestData.COMMENT_4.getParentType(),
    TestData.COMMENT_4.getContent());

  /**
   * Some random data for update queries in tests
   */
  public static final UpdateCommentInput UPDATE_COMMENT_INPUT = new UpdateCommentInput(
    COMMENT_4.getId(),
    COMMENT_4.getContent());

  /**
   * Some data for comment queries in tests
   */
  public static final CommentQuery COMMENT_QUERY = new CommentQuery(TestData.SOME_ID);
}
