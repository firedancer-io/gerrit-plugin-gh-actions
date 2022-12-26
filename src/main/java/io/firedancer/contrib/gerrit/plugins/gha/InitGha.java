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

package io.firedancer.contrib.gerrit.plugins.gha;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.gerrit.pgm.init.api.InitStep;
import com.google.gerrit.pgm.init.api.Section;
import com.google.inject.Inject;
import io.firedancer.contrib.gerrit.plugins.gha.config.GhaServerConfig;

/** Command-line wizard for initializing this plugin. */
class InitGha implements InitStep {

  private final ConsoleUI ui;
  private final Section sectionPlugin;

  @Inject
  InitGha(ConsoleUI ui, Section.Factory sections, @PluginName String pluginName) {
    this.ui = ui;
    this.sectionPlugin = sections.get(GhaServerConfig.SECTION_PLUGIN, pluginName);
  }

  @Override
  public void run() {
    ui.header("GitHub Actions Plugin");

    sectionPlugin.string("GitHub domain", GhaServerConfig.KEY_GITHUB_ORIGIN, "github.com");
    sectionPlugin.string("Application ID", GhaServerConfig.KEY_APP_ID, "");
    sectionPlugin.string("Path to private key file", GhaServerConfig.KEY_PRIVATE_KEY_FILE, "");
    sectionPlugin.passwordForKey("GitHub webhook secret", GhaServerConfig.KEY_WEBHOOK_SECRET);
  }
}
