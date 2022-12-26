package io.firedancer.contrib.github.rest;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface AppsService {
  /**
   * See <a
   * href="https://docs.github.com/en/rest/reference/apps/apps#create-an-installation-access-token-for-an-app">
   * Create an installation access token for an app</a>
   *
   * @param installationId The ID of the installation.
   * @param opts Installation token options.
   */
  HttpResponse<InstallationToken> createInstallationAccessToken(
      long installationId, InstallationTokenOptions opts)
      throws IOException, InterruptedException, ApiException;

  /**
   * See <a
   * href="https://docs.github.com/en/rest/apps/apps?apiVersion=2022-11-28#get-a-repository-installation-for-the-authenticated-app">
   * Get a repository installation for the authenticated app</a>
   *
   * @param owner The owner of the repository
   * @param repo The name of the repository
   */
  HttpResponse<Installation> findRepositoryInstallation(String owner, String repo)
      throws IOException, InterruptedException, ApiException;
}
