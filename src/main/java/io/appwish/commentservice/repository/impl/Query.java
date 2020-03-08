package io.appwish.commentservice.repository.impl;

/**
 * Contains queries to execute on Postgres.
 *
 * I'm not sure what's the best practice for storing String SQLs, so for now it'll stay here.
 */
public enum Query {
  FIND_ALL_COMMENTS_BY_ITEM("SELECT * FROM comments "
      + "WHERE parent_id=$1 "
      + "AND parent_type=$2"),
  DELETE_COMMENT_QUERY("DELETE FROM comments WHERE id=$1"),
  INSERT_COMMENT_QUERY(
    "INSERT INTO comments ("
      + "user_id, "
      + "parent_id, "
      + "parent_type, "
      + "content, "
      + "created_at, "
      + "updated_at) "
      + "VALUES ($1, $2, $3, $4, $5, $6) "
      + "RETURNING *"),
  UPDATE_COMMENT_QUERY(
    "UPDATE comments SET "
      + "content=$2 "
      + "WHERE id=$1 RETURNING *"),
  CREATE_COMMENTS_TABLE(
    "CREATE TABLE IF NOT EXISTS comments("
      + "id serial PRIMARY KEY, "
      + "user_id serial, "
      + "parent_id serial, "
      + "parent_type VARCHAR(255), "
      + "content VARCHAR(255), "
      + "created_at timestamp, "
      + "updated_at timestamp);");

  private final String sql;

  Query(final String sql) {
    this.sql = sql;
  }

  public String sql() {
    return sql;
  }
}
