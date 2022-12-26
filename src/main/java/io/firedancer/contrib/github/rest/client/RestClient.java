/*
 * Copyright (C) 2022 Jump Crypto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.firedancer.contrib.github.rest.client;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.gerrit.common.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.firedancer.contrib.github.rest.ApiException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Base client implementing an authenticated REST transport for GitHub APIs. */
public abstract class RestClient {
  /** GitHub API version target */
  public static final String API_VERSION = "2022-11-28";
  /** Expected MIME type of GitHub API responses */
  public static final String API_MIME_TYPE = "application/vnd.github+json";
  /** User agent string sent to GitHub API */
  public static final String USER_AGENT = "Gerrit-plugin-github-actions Java-http-client";

  /** Identifies a repo at this GitHub API. */
  public static class RepoKey {
    public RepoKey(String owner, String repo) {
      this.owner = owner;
      this.repo = repo;
    }

    protected String owner;
    protected String repo;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RepoKey repoKey = (RepoKey) o;
      return Objects.equal(owner, repoKey.owner) && Objects.equal(repo, repoKey.repo);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(owner, repo);
    }

    @Override
    public String toString() {
      return owner + "/" + repo;
    }
  }

  /** HTTP client used to send requests. */
  protected final HttpClient client;

  /** GitHub API base URL. */
  protected final String baseUrl;

  /** GitHub App identifier */
  protected final int appId;

  /** Cache for last signed JWT */
  @Nullable protected IssuedToken jwt;

  protected final Gson gson;

  protected RestClient(HttpClient client, String baseUrl, int appId) {
    this.client = client;
    this.baseUrl = baseUrl;
    this.appId = appId;
    this.gson =
        new GsonBuilder().setDateFormat("yyyyMMdd'T'HHmmss'Z'").create(); // RFC 3339 time format
  }

  /** Returns the auth token to be sent in requests. */
  protected abstract String getAuthToken();

  /**
   * Creates a new authenticated request.
   *
   * <p>Available permissions depend on the implementation of {@link #getAuthToken()}
   *
   * @param path The request path, rooted in {@link #baseUrl}
   * @return HTTP request builder
   */
  protected HttpRequest.Builder newRequest(String path) {
    return HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/" + CharMatcher.is('/').trimLeadingFrom(path)))
        .header("accept", API_MIME_TYPE)
        .header("authorization", "Bearer " + getAuthToken())
        .header("user-agent", USER_AGENT)
        .header("x-github-api-version", API_VERSION);
  }

  /** Creates a new GET request. */
  protected HttpRequest.Builder newGetRequest(String path) {
    return newRequest(path).GET().header("accept", API_MIME_TYPE);
  }

  /** Sends a GET request to retrieve a JSON object. */
  protected <T> HttpResponse<T> getForJson(String path, Class<T> tClass)
      throws IOException, InterruptedException, ApiException {
    return WrapHttpResponse.unwrapResult(
        client.send(newGetRequest(path).build(), new ResponseHandler<>(tClass)));
  }

  /** Creates a new POST request with a JSON object. */
  protected HttpRequest.Builder newPostJsonRequest(String path, Object data) {
    String jsonData = gson.toJson(data);
    return newRequest(path)
        .POST(HttpRequest.BodyPublishers.ofString(jsonData, Charsets.UTF_8))
        .header("content-type", "application/json");
  }

  /** Sends a POST request with a JSON object to retrieve a JSON object. */
  protected <T> HttpResponse<T> postJsonForJson(String path, Object data, Class<T> tClass)
      throws IOException, InterruptedException, ApiException {
    return WrapHttpResponse.unwrapResult(
        client.send(newPostJsonRequest(path, data).build(), new ResponseHandler<>(tClass)));
  }
}
