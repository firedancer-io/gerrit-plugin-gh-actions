package io.firedancer.contrib.github.rest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;

public interface ActionsService {
  /**
   * See <a
   * href="https://docs.github.com/en/rest/actions/workflows?apiVersion=2022-11-28#create-a-workflow-dispatch-event">Create
   * a workflow dispatch event</a>
   */
  HttpResponse<Void> createWorkflowDispatchEvent(
      String owner, String repo, String workflowId, CreateWorkflowDispatchEventRequest event)
      throws IOException, InterruptedException, ApiException;

  class CreateWorkflowDispatchEventRequest {
    public CreateWorkflowDispatchEventRequest(String ref) {
      this.ref = ref;
      this.inputs = new HashMap<>();
    }

    public String ref;
    public HashMap<String, String> inputs;
  }
}
