package io.firedancer.contrib.gerrit.plugins.gha.repos;

import com.google.common.collect.HashBiMap;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.entities.Project;
import com.google.inject.Singleton;

@Singleton
public class RepoMapping {
  private final HashBiMap<Project.NameKey, String> mapping = HashBiMap.create();

  public void put(Project.NameKey project, String repoUrl) {
    mapping.put(project, repoUrl);
  }

  @Nullable
  public String getRepoUrlByProjectName(Project.NameKey projectName) {
    return mapping.get(projectName);
  }

  @Nullable
  public Project.NameKey getProjectNameByRepoUrl(String repoUrl) {
    return mapping.inverse().get(repoUrl);
  }

  public void removeByProjectName(Project.NameKey projectName) {
    mapping.remove(projectName);
  }

  public void removeByRepoUrl(String repo) {
    mapping.inverse().remove(repo);
  }
}
