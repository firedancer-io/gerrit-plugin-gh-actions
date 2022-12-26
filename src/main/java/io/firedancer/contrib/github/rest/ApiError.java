package io.firedancer.contrib.github.rest;

public class ApiError {
  public static class SubError {
    public String resource;
    public String field;
    public String code;
  }

  public String message;

  public String documentation_url;

  public SubError[] errors;

  @Override
  public String toString() {
    return "ApiError{\"" + String.valueOf(message) + "\"}";
  }
}
