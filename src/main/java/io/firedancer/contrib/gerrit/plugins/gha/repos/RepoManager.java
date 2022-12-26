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

package io.firedancer.contrib.gerrit.plugins.gha.repos;

import com.google.common.flogger.FluentLogger;
import com.google.gerrit.entities.Project;
import com.google.gerrit.entities.RefNames;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.extensions.events.NewProjectCreatedListener;
import com.google.gerrit.extensions.events.ProjectDeletedListener;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.firedancer.contrib.gerrit.plugins.gha.config.GhaProjectConfig;
import org.eclipse.jgit.errors.ConfigInvalidException;

/** Keeps a mapping from GitHub repos to Gerrit projects. */
@Singleton
public class RepoManager
    implements NewProjectCreatedListener, ProjectDeletedListener, GitReferenceUpdatedListener {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();

  private final PluginConfigFactory cfg;
  private final RepoMapping mapping;
  private final String pluginName;

  @Inject
  RepoManager(PluginConfigFactory cfg, RepoMapping mapping, @PluginName String pluginName) {
    this.cfg = cfg;
    this.mapping = mapping;
    this.pluginName = pluginName;
  }

  /** Reacts to the creation of a new project to update mapping. */
  @Override
  public void onNewProjectCreated(NewProjectCreatedListener.Event event) {
    tryRefreshConfig(Project.nameKey(event.getProjectName()));
  }

  /** Reacts to deletion of a project to update mapping. */
  @Override
  public void onProjectDeleted(ProjectDeletedListener.Event event) {
    mapping.remove(Project.nameKey(event.getProjectName()));
  }

  /** Reacts to changes in the project's GitHub mapping config. */
  @Override
  public void onGitReferenceUpdated(GitReferenceUpdatedListener.Event event) {
    if (event.getRefName().equals(RefNames.REFS_CONFIG)) {
      tryRefreshConfig(Project.nameKey(event.getProjectName()));
    }
  }

  private void tryRefreshConfig(Project.NameKey projectName) {
    try {
      refreshConfig(projectName);
    } catch (NoSuchProjectException e) {
      log.atWarning().log(
          "Tried to update config of project %s, but project does not exist", projectName.get());
    } catch (ConfigInvalidException e) {
      log.atWarning().log("Invalid GitHub config in project %s: %s", projectName, e.getMessage());
    }
  }

  private void refreshConfig(Project.NameKey projectName)
      throws NoSuchProjectException, ConfigInvalidException {
    // Get config file from meta
    PluginConfig pluginConfig = cfg.getFromProjectConfig(projectName, pluginName);
    GhaProjectConfig projectConfig = GhaProjectConfig.fromPluginConfig(projectName, pluginConfig);

    // Bail if config is incomplete
    if (projectConfig == null) {
      mapping.remove(projectName);
      log.atWarning().log("Failed to construct GitHub Actions config for project %s", projectName);
      return;
    }

    // Write config to cache
    mapping.put(projectName, projectConfig);
  }
}
