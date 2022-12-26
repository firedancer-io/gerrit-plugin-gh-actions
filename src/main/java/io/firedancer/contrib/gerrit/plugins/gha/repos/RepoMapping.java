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

import com.google.gerrit.common.Nullable;
import com.google.gerrit.entities.Project;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.firedancer.contrib.gerrit.plugins.gha.config.GhaProjectConfig;
import java.util.HashMap;
import java.util.HashSet;

/** Tracks relation of Gerrit projects and GitHub repos. */
@Singleton
public class RepoMapping {
  private final PluginConfigFactory cfg;
  private final String pluginName;

  /** Track known projects */
  private final HashMap<Project.NameKey, GhaProjectConfig> projects = new HashMap<>();

  /** Track projects which are known to lack GitHub Actions integrations */
  private final HashSet<Project.NameKey> skipProjects = new HashSet<>();

  @Inject
  public RepoMapping(PluginConfigFactory cfg, @PluginName String pluginName) {
    this.cfg = cfg;
    this.pluginName = pluginName;
  }

  /**
   * Associates a Gerrit project by name with a GitHub repository URL.
   *
   * @param projectName Name of project
   * @param config Config object. If null, acts like {@link RepoMapping::remove} instead
   */
  public void put(Project.NameKey projectName, @Nullable GhaProjectConfig config) {
    if (config != null) {
      projects.put(projectName, config);
      skipProjects.remove(projectName);
    } else {
      remove(projectName);
    }
  }

  /** Returns whether given project is in cache. */
  public boolean isCached(Project.NameKey projectName) {
    return projects.containsKey(projectName) || skipProjects.contains(projectName);
  }

  /** Looks up the GitHub config for a given Gerrit project. */
  @Nullable
  public GhaProjectConfig get(Project.NameKey projectName) {
    if (projects.containsKey(projectName)) {
      return projects.get(projectName);
    } else if (skipProjects.contains(projectName)) {
      return null; // We know this project doesn't have a config
    }

    try {
      PluginConfig pluginConfig = cfg.getFromProjectConfig(projectName, pluginName);
      GhaProjectConfig config = GhaProjectConfig.fromPluginConfig(projectName, pluginConfig);
      put(projectName, config);
      return config;
    } catch (NoSuchProjectException e) {
      purge(projectName);
      return null;
    }
  }

  /** Removes GitHub Actions config from a project and cache the fact that it is missing. */
  public void remove(Project.NameKey projectName) {
    projects.remove(projectName);
    skipProjects.add(projectName);
  }

  /** Removes a project from cache. */
  public void purge(Project.NameKey projectName) {
    projects.remove(projectName);
    skipProjects.remove(projectName);
  }
}
