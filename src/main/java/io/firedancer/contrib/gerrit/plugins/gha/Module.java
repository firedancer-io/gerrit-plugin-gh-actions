package io.firedancer.contrib.gerrit.plugins.gha;

import com.google.gerrit.server.events.EventTypes;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import io.firedancer.contrib.gerrit.plugins.gha.repos.RepoManager;
import io.firedancer.contrib.gerrit.plugins.gha.webhooks.events.WebhookEvent;

public class Module extends AbstractModule {
  @Inject
  Module() {}

  @Override
  protected void configure() {
    bind(RepoManager.class).in(Scopes.SINGLETON);

    EventTypes.register(WebhookEvent.TYPE, WebhookEvent.class);
  }
}
