package io.firedancer.contrib.gerrit.plugins.gha;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import io.firedancer.contrib.gerrit.plugins.gha.repos.RepoManager;

public class Module extends AbstractModule {
  @Inject
  Module() {}

  @Override
  protected void configure() {
    bind(RepoManager.class).in(Scopes.SINGLETON);
  }
}
