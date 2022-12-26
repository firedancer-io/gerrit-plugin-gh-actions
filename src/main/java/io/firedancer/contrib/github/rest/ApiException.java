package io.firedancer.contrib.github.rest;

public class ApiException extends Exception {
  private final ApiError error;

  public ApiException(ApiError error) {
    super(error.message);
    this.error = error;
  }

  public ApiError getError() {
    return error;
  }
}
