package io.firedancer.contrib.gerrit.plugins.gha.repos;

import com.google.common.base.Strings;
import com.google.gerrit.entities.Project;
import com.google.gerrit.entities.RefNames;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.extensions.events.NewProjectCreatedListener;
import com.google.gerrit.extensions.events.ProjectDeletedListener;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Keeps a mapping from GitHub repos to Gerrit projects. */
@Singleton
public class RepoManager
    implements NewProjectCreatedListener, ProjectDeletedListener, GitReferenceUpdatedListener {
  private static final Logger logger = LoggerFactory.getLogger(RepoManager.class);

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
    mapping.removeByProjectName(Project.nameKey(event.getProjectName()));
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
      logger.error(
          "Tried to update config of project {}, but project does not exist", projectName.get());
    } catch (ConfigInvalidException e) {
      logger.error("Invalid GitHub config in project {}: {}", projectName, e.getMessage());
    }
  }

  private void refreshConfig(Project.NameKey projectName)
      throws NoSuchProjectException, ConfigInvalidException {
    // Get config file from meta
    Config config = cfg.getProjectPluginConfig(projectName, pluginName);

    // Get project repo settings.
    String remote = config.getString("repo", null, "github-origin");
    if (Strings.isNullOrEmpty(remote)) {
      remote = "github.com";
    }
    String owner = config.getString("repo", null, "owner");
    if (Strings.isNullOrEmpty(owner)) {
      throw new ConfigInvalidException("Missing repo.owner key");
    }
    String repo = config.getString("repo", null, "repo");
    if (Strings.isNullOrEmpty(repo)) {
      throw new ConfigInvalidException("Missing repo.repo key");
    }

    // Assemble repo URL.
    String repoUrl =
        "https://"
            + remote
            + "/"
            + URLEncoder.encode(owner, StandardCharsets.UTF_8)
            + "/"
            + URLEncoder.encode(repo, StandardCharsets.UTF_8);

    // Update mapping.
    mapping.put(projectName, repoUrl);
  }
}
