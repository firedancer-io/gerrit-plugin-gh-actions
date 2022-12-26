// Copyright (C) 2011 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.firedancer.contrib.gerrit.plugins.gha.config;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;

@Singleton
public class Credentials {
  private final String webhookSecret;

  @Inject
  public Credentials(SitePaths site, @PluginName String pluginName)
      throws ConfigInvalidException, IOException {
    Config config = load(site);
    this.webhookSecret = config.getString("plugin", pluginName, "webhook-secret");
  }

  private static Config load(SitePaths site) throws ConfigInvalidException, IOException {
    FileBasedConfig cfg = new FileBasedConfig(site.secure_config.toFile(), FS.DETECTED);
    if (cfg.getFile().exists() && cfg.getFile().length() > 0) {
      try {
        cfg.load();
      } catch (ConfigInvalidException e) {
        throw new ConfigInvalidException(
            String.format("Config file %s is invalid: %s", cfg.getFile(), e.getMessage()), e);
      } catch (IOException e) {
        throw new IOException(
            String.format("Cannot read %s: %s", cfg.getFile(), e.getMessage()), e);
      }
    }
    return cfg;
  }

  public String getWebhookSecret() {
    return webhookSecret;
  }
}
