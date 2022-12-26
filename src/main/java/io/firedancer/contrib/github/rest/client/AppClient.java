package io.firedancer.contrib.github.rest.client;

import com.google.common.base.Charsets;
import com.google.common.flogger.FluentLogger;
import io.firedancer.contrib.github.rest.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

/**
 * GitHub REST API client with application-level authorization.
 *
 * <p>Mainly used to manage the application itself and obtain installation auth tokens.
 */
public class AppClient extends RestClient implements AppsService {

  private static final FluentLogger log = FluentLogger.forEnclosingClass();

  /** Local signer to create JWTs */
  protected final JwtTokenIssuer issuer;

  /** Cache for installation access tokens */
  protected final HashMap<RepoKey, IssuedToken> repoInstallationTokens;

  public AppClient(HttpClient client, String baseUrl, int appId, byte[] privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    super(client, baseUrl, appId);
    this.issuer = JwtTokenIssuer.fromPrivateKey(appId, privateKey);
    this.repoInstallationTokens = new HashMap<>();
  }

  public InstallationClient getInstallationClient(RepoKey key)
      throws IOException, InterruptedException, ApiException {
    if (repoInstallationTokens.containsKey(key)) {
      IssuedToken token = repoInstallationTokens.get(key);
      if (token.isExpired()) {
        log.atFiner().log("Access token for repo %s expired, refreshing", key);
      } else {
        log.atFiner().log("Using cached access token for repo %s", key);
        return new InstallationClient(client, baseUrl, appId, token.token);
      }
    } else {
      log.atFiner().log("No installation access token found for repo %s, creating new one", key);
    }

    Installation installation;
    try {
      installation = findRepositoryInstallation(key.owner, key.repo).body();
    } catch (ApiException e) {
      log.atWarning().log("Missing app installation for repo %s: %s", key, e);
      return null;
    }

    long installationId = installation.id;
    log.atFiner().log("Found installation ID %s for repo %s", installationId, key);

    InstallationTokenOptions opts = new InstallationTokenOptions();
    // TODO restrict to only the repo we need

    InstallationToken token;
    try {
      token = createInstallationAccessToken(installationId, opts).body();
    } catch (ApiException e) {
      log.atWarning().log(
          "Failed to create installation access token for repo %s installation %d: %s",
          key, installationId, e);
      return null;
    }
    log.atFiner().log("Created installation access token for repo %s", key);

    repoInstallationTokens.put(key, new IssuedToken(token.token, token.expiresAt.toInstant()));
    return new InstallationClient(client, baseUrl, appId, token.token);
  }

  /**
   * Gets the latest cached JWT.
   *
   * <p>If none is cached or the current JWT has expired, creates a new one.
   *
   * <p>JWTs are only useful for management requests, such as issuing installation tokens that
   * provide actual access to the GitHub API.
   */
  @Override
  protected String getAuthToken() {
    if (jwt == null || jwt.isExpired()) {
      jwt = issuer.issueJwt();
    }
    return jwt.token;
  }

  @Override
  public HttpResponse<InstallationToken> createInstallationAccessToken(
      long installationId, InstallationTokenOptions opts)
      throws IOException, InterruptedException, ApiException {
    return postJsonForJson(
        "/app/installations/" + installationId + "/access_tokens", opts, InstallationToken.class);
  }

  @Override
  public HttpResponse<Installation> findRepositoryInstallation(String owner, String repo)
      throws IOException, InterruptedException, ApiException {
    return getForJson(
        "/repos/"
            + URLEncoder.encode(owner, Charsets.UTF_8)
            + "/"
            + URLEncoder.encode(repo, Charsets.UTF_8)
            + "/installation",
        Installation.class);
  }
}
