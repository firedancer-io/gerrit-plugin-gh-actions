package io.firedancer.contrib.github.rest.client;

import io.firedancer.contrib.github.rest.ActionsService;
import io.firedancer.contrib.github.rest.ApiException;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class InstallationClient extends RestClient implements ActionsService {
  private final String authToken;

  InstallationClient(HttpClient client, String baseUrl, int appId, String authToken) {
    super(client, baseUrl, appId);
    this.authToken = authToken;
  }

  @Override
  protected String getAuthToken() {
    return authToken;
  }

  @Override
  public HttpResponse<Void> createWorkflowDispatchEvent(
      String owner, String repo, String workflowId, CreateWorkflowDispatchEventRequest event)
      throws IOException, InterruptedException, ApiException {
    String path =
        "/repos/"
            + URLEncoder.encode(owner, StandardCharsets.UTF_8)
            + "/"
            + URLEncoder.encode(repo, StandardCharsets.UTF_8)
            + "/actions/workflows/"
            + URLEncoder.encode(workflowId, StandardCharsets.UTF_8)
            + "/dispatches";

    return postJsonForJson(path, event, Void.class);
  }
}
