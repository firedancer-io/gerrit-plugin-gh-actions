package io.firedancer.contrib.gerrit.plugins.gha;

import com.google.gerrit.extensions.events.RevisionCreatedListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.events.EventTypes;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import io.firedancer.contrib.gerrit.plugins.gha.repos.RepoManager;
import io.firedancer.contrib.gerrit.plugins.gha.webhooks.events.WebhookEvent;
import io.firedancer.contrib.gerrit.plugins.gha.workflows.WorkflowDispatcher;

public class Module extends AbstractModule {
  @Inject
  Module() {}

  @Override
  protected void configure() {
    // TODO: These should be scoped to the plugin.
    bind(RepoManager.class).in(Scopes.SINGLETON);

    DynamicSet.bind(binder(), RevisionCreatedListener.class).to(WorkflowDispatcher.class);

    EventTypes.register(WebhookEvent.TYPE, WebhookEvent.class);
  }
}
