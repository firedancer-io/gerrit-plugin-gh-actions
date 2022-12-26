package io.firedancer.contrib.github.rest;

import com.google.gerrit.common.Nullable;

public class Result<T> {
  private Result(T result, ApiError error) {
    this.result = result;
    this.error = error;
  }

  public static <T> Result<T> fromError(ApiError error) {
    return new Result<>(null, error);
  }

  public static <T> Result<T> fromResult(T result) {
    return new Result<>(result, null);
  }

  @Nullable public T result;
  @Nullable public ApiError error;
}
