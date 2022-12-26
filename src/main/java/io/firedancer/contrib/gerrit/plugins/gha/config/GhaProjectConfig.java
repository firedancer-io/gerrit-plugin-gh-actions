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

package io.firedancer.contrib.gerrit.plugins.gha.config;

import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.entities.Project;
import com.google.gerrit.server.config.PluginConfig;

public class GhaProjectConfig {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();

  /** Configures whether GitHub Actions are enabled for the project */
  public static final String KEY_ENABLED = "enabled";

  /** Configures owner of GitHub repo containing workflow config */
  public static final String KEY_OWNER = "owner";

  /** Configures name of GitHub repo containing workflow config */
  public static final String KEY_REPO = "repo";

  /** Configures name or ID of workflow */
  public static final String KEY_WORKFLOW = "workflow";

  /** Configures branch or tag name containing workflow */
  public static final String KEY_WORKFLOW_REF = "workflow-ref";

  /** Returns whether config indicates that */
  public static boolean isEnabled(PluginConfig config) {
    return config.getBoolean(KEY_ENABLED, false);
  }

  /**
   * Parses plugin config from the project's main config file.
   *
   * @return Plugin config, or null if config is incomplete
   */
  @Nullable
  public static GhaProjectConfig fromPluginConfig(
      Project.NameKey projectName, PluginConfig config) {
    GhaProjectConfig c = new GhaProjectConfig();

    c.owner = config.getString(KEY_OWNER);
    if (Strings.isNullOrEmpty(c.owner)) {
      log.atWarning().log("Missing 'owner' config for repo %s", projectName);
      return null;
    }

    c.repo = config.getString(KEY_REPO);
    if (Strings.isNullOrEmpty(c.repo)) {
      log.atWarning().log("Missing 'repo' config for repo %s", projectName);
      return null;
    }

    c.workflow = config.getString(KEY_WORKFLOW);
    if (Strings.isNullOrEmpty(c.workflow)) {
      log.atWarning().log("Missing 'workflow' config for repo %s", projectName);
      return null;
    }

    c.workflowRef = config.getString(KEY_WORKFLOW_REF);
    if (Strings.isNullOrEmpty(c.workflowRef)) {
      log.atWarning().log("Missing 'workflow-ref' config for repo %s", projectName);
      return null;
    }

    log.atConfig().log("Found GitHub actions config for project %s", projectName);

    return c;
  }

  /** Owner of GitHub repo containing workflow config */
  public String owner;

  /** Name of GitHub repo containing workflow config */
  public String repo;

  /** Name or ID of workflow */
  public String workflow;

  /** Branch or tag name containing workflow */
  public String workflowRef;
}
