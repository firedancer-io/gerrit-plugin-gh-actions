package io.firedancer.contrib.github.rest.client;

import io.firedancer.contrib.github.rest.ApiException;
import io.firedancer.contrib.github.rest.Result;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import javax.net.ssl.SSLSession;

public class WrapHttpResponse<T, W> implements HttpResponse<T> {
  private final HttpResponse<W> response;
  private final T newBody;

  public WrapHttpResponse(HttpResponse<W> response, T body) {
    this.response = response;
    this.newBody = body;
  }

  public static <T> WrapHttpResponse<T, Result<T>> unwrapResult(HttpResponse<Result<T>> response)
      throws ApiException {
    Result<T> res = response.body();
    if (res.error != null) {
      throw new ApiException(res.error);
    }
    return new WrapHttpResponse<>(response, res.result);
  }

  @Override
  public int statusCode() {
    return response.statusCode();
  }

  @Override
  public HttpRequest request() {
    return response.request();
  }

  @Override
  public Optional<HttpResponse<T>> previousResponse() {
    return Optional.empty();
  }

  @Override
  public HttpHeaders headers() {
    return response.headers();
  }

  @Override
  public T body() {
    return newBody;
  }

  @Override
  public Optional<SSLSession> sslSession() {
    return response.sslSession();
  }

  @Override
  public URI uri() {
    return response.uri();
  }

  @Override
  public HttpClient.Version version() {
    return response.version();
  }
}
