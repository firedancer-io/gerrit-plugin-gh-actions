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
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhaServerConfig {
  /**
   * Top-level section of plugin-specific server config. Subsection is set to plugin name.
   *
   * <p>e.g. <code>[plugin "github-actions"]</code>
   */
  public static final String SECTION_PLUGIN = "plugin";

  /**
   * URL origin of GitHub instance.
   *
   * <p>Defaults to <code>github.com</code>
   *
   * <p>For GitHub Enterprise, this is a custom URL.
   */
  public static final String KEY_GITHUB_ORIGIN = "github-origin";

  /** Default value of {@link #KEY_GITHUB_ORIGIN} setting. */
  public static final String DEFAULT_GITHUB_ORIGIN = "github.com";

  /** URL of GitHub API. */
  public static final String KEY_API_URL = "github-api-url";

  /**
   * HMAC secret for authenticating GitHub webhook deliveries. Part of secret config.
   *
   * <p>See <a
   * href="https://docs.github.com/en/developers/webhooks-and-events/webhooks/securing-your-webhooks">Securing
   * your webhooks</a>
   */
  public static final String KEY_WEBHOOK_SECRET = "webhook-secret";

  /** GitHub App identifier. Integer shown in the app settings page in developer settings. */
  public static final String KEY_APP_ID = "app-id";

  /**
   * PEM private key used to sign JSON Web Tokens.
   *
   * <p>See <a
   * href="https://docs.github.com/en/developers/apps/building-github-apps/authenticating-with-github-apps#authenticating-as-a-github-app">Authenticating
   * as a GitHub App</a>
   */
  public static final String KEY_PRIVATE_KEY_FILE = "private-key-file";

  private static final Logger log = LoggerFactory.getLogger(GhaServerConfig.class);

  @Inject
  private GhaServerConfig(@GerritServerConfig Config config, @PluginName String pluginName) {
    this.githubOrigin = config.getString(SECTION_PLUGIN, pluginName, KEY_GITHUB_ORIGIN);
    if (Strings.isNullOrEmpty(this.githubOrigin)) {
      this.githubOrigin = DEFAULT_GITHUB_ORIGIN;
    }

    this.githubApiUrl = config.getString(SECTION_PLUGIN, pluginName, KEY_API_URL);
    if (Strings.isNullOrEmpty(this.githubApiUrl)) {
      this.githubApiUrl = "https://api." + this.githubOrigin;
    }

    if (!Strings.isNullOrEmpty(config.getString(SECTION_PLUGIN, pluginName, KEY_APP_ID))) {
      this.appId = config.getInt(SECTION_PLUGIN, pluginName, KEY_APP_ID, -1);
    }

    String privateKeyPath = config.getString(SECTION_PLUGIN, pluginName, KEY_PRIVATE_KEY_FILE);
    if (!Strings.isNullOrEmpty(privateKeyPath)) {
      try {
        this.privateKey = Files.readAllBytes(Path.of(privateKeyPath));
      } catch (IOException e) {
        log.error("Failed to read private key of GitHub Actions app at {}", privateKeyPath);
      }
    }
  }

  /** Value of {@link #KEY_GITHUB_ORIGIN} */
  public String githubOrigin;

  /** Value of {@link #KEY_API_URL} */
  public String githubApiUrl;

  /** Value of {@link #KEY_APP_ID} */
  public Integer appId;

  /** File content behind {@link #KEY_PRIVATE_KEY_FILE} */
  public byte[] privateKey;
}
