package com.boddi.multidatasource.exception;

/**
 * Created by guoyubo on 2017/6/8.
 */
public class MultiDataSourceException extends RuntimeException {

  private static final long serialVersionUID = -1343739516839252250L;

  public MultiDataSourceException(final String errorMessage, final Object... args) {
    super(String.format(errorMessage, args));
  }

  public MultiDataSourceException(final String message, final Exception cause) {
    super(message, cause);
  }

  public MultiDataSourceException(final Exception cause) {
    super(cause);
  }
}