package io.firedancer.contrib.github.rest.client;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import io.firedancer.contrib.github.rest.ApiError;
import io.firedancer.contrib.github.rest.Result;
import java.net.http.HttpResponse;

public class ResponseHandler<T> implements HttpResponse.BodyHandler<Result<T>> {
  private static final Gson gson = new Gson();

  private final Class<T> _class;

  public ResponseHandler(Class<T> _class) {
    this._class = _class;
  }

  @Override
  public HttpResponse.BodySubscriber<Result<T>> apply(HttpResponse.ResponseInfo resp) {
    switch (resp.statusCode()) {
      case 200:
      case 201:
        if (_class == Void.class) {
          return HttpResponse.BodySubscribers.mapping(
              HttpResponse.BodySubscribers.discarding(), v -> Result.fromResult((T) v));
        }
        return asResult(this._class);
      default:
        return asError();
    }
  }

  private static <T> HttpResponse.BodySubscriber<Result<T>> asError() {
    return HttpResponse.BodySubscribers.mapping(
        HttpResponse.BodySubscribers.ofString(Charsets.UTF_8),
        body -> Result.fromError(gson.fromJson(body, ApiError.class)));
  }

  private static <T> HttpResponse.BodySubscriber<Result<T>> asResult(Class<T> _class) {
    return HttpResponse.BodySubscribers.mapping(
        HttpResponse.BodySubscribers.ofString(Charsets.UTF_8),
        s -> Result.fromResult(gson.fromJson(s, _class)));
  }
}
