package io.appwish.commentservice.eventbus;

/**
 * Represents addresses available on the event bus
 */
public enum Address {
  FIND_ALL_COMMENTS,
  CREATE_ONE_COMMENT,
  UPDATE_ONE_COMMENT,
  DELETE_ONE_COMMENT;

  public String get() {
    return name();
  }
}
