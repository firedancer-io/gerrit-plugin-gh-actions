package io.firedancer.contrib.gerrit.plugins.gha;

import com.google.inject.servlet.ServletModule;
import io.firedancer.contrib.gerrit.plugins.gha.webhooks.WebhookServlet;

/** Serves an endpoint that GitHub can deliver webhooks to. */
public class HttpModule extends ServletModule {
  private static final String PATH_WEBHOOK = "/webhook";

  @Override
  protected void configureServlets() {
    serve(PATH_WEBHOOK).with(WebhookServlet.class);
  }
}
