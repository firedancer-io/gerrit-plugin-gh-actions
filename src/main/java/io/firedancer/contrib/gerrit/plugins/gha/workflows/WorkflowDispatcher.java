package io.firedancer.contrib.gerrit.plugins.gha.workflows;

import com.google.common.flogger.FluentLogger;
import com.google.gerrit.entities.Project;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.RevisionInfo;
import com.google.gerrit.extensions.events.RevisionCreatedListener;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.firedancer.contrib.gerrit.plugins.gha.config.GhaProjectConfig;
import io.firedancer.contrib.gerrit.plugins.gha.config.GhaServerConfig;
import io.firedancer.contrib.gerrit.plugins.gha.repos.RepoMapping;
import io.firedancer.contrib.github.rest.ActionsService;
import io.firedancer.contrib.github.rest.client.AppClient;
import io.firedancer.contrib.github.rest.client.InstallationClient;
import io.firedancer.contrib.github.rest.client.RestClient;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Singleton
public class WorkflowDispatcher implements RevisionCreatedListener {

  private static final FluentLogger log = FluentLogger.forEnclosingClass();

  private final AppClient appClient;
  private final GerritApi gerritApi;
  private final RepoMapping mapping;

  @Inject
  WorkflowDispatcher(GhaServerConfig serverConfig, GerritApi gerritApi, RepoMapping mapping)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    HttpClient httpClient = HttpClient.newHttpClient();

    this.appClient =
        new AppClient(
            httpClient, serverConfig.githubApiUrl, serverConfig.appId, serverConfig.privateKey);
    this.gerritApi = gerritApi;
    this.mapping = mapping;
  }

  @Override
  public void onRevisionCreated(Event event) {
    ChangeInfo change = event.getChange();
    RevisionInfo revision = event.getRevision();

    // Lookup project config in cache. If not found, assume GHA is not enabled.
    GhaProjectConfig project = mapping.get(Project.nameKey(change.project));
    if (project == null) {
      log.atFiner().log(
          "Skipping GitHub Actions for change %d/%d in project %s",
          change._number, revision._number, change.project);
      return;
    }

    // Acquire GitHub client with authorization to dispatch workflows.
    RestClient.RepoKey repoKey = new RestClient.RepoKey(project.owner, project.repo);
    InstallationClient client;
    try {
      client = appClient.getInstallationClient(repoKey);
      if (client == null) {
        return;
      }
    } catch (Exception e) {
      log.atWarning().log("Error setting up GitHub client for installation at %s: %s", repoKey, e);
      return;
    }

    // Assemble workflow dispatch request
    ActionsService.CreateWorkflowDispatchEventRequest request =
        new ActionsService.CreateWorkflowDispatchEventRequest(project.workflowRef);
    request.inputs.put("gerrit_project", change.project);

    // Dispatch workflow
    try {
      client.createWorkflowDispatchEvent(project.owner, project.repo, project.workflow, request);
    } catch (Exception e) {
      log.atWarning().log("Error dispatching workflow '%s' for '%s': %s", project.workflow, repoKey, e);
      return;
    }

    // Add comment notifying users of workflow dispatch
    ReviewInput dispatchComment =
        ReviewInput.create().message("Dispatched GitHub Actions workflow " + project.workflow);
    try {
      gerritApi.changes().id(change.id).revision(revision._number).review(dispatchComment);
    } catch (RestApiException e) {
      log.atWarning().log(
          "Error adding comment to %s notifying of workflow dispatch: %s", change.id, e);
    }
  }
}
