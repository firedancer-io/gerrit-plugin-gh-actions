package io.firedancer.contrib.github.rest;

import com.google.gson.annotations.SerializedName;

public class InstallationTokenOptions {
  @SerializedName("repository_ids")
  public long[] repositoryIds;

  @SerializedName("repositories")
  public String[] repositories;

  @SerializedName("permissions")
  public InstallationPermissions permissions;
}
