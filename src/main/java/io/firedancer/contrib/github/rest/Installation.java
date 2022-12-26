package io.firedancer.contrib.github.rest;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class Installation {
  @SerializedName("id")
  public long id;

  @SerializedName("node_id")
  public String nodeId;

  @SerializedName("app_id")
  public long appId;

  @SerializedName("app_slug")
  public String appSlug;

  @SerializedName("target_id")
  public long targetId;

  @SerializedName("account")
  public User account;

  @SerializedName("access_tokens_url")
  public String accessTokensUrl;

  @SerializedName("repositories_url")
  public String repositoriesUrl;

  @SerializedName("html_url")
  public String htmlUrl;

  @SerializedName("target_type")
  public String targetType;

  @SerializedName("single_file_name")
  public String singleFileName;

  @SerializedName("repository_selection")
  public String repositorySelection;

  @SerializedName("events")
  public String[] events;

  @SerializedName("single_file_paths")
  public String[] singleFilePaths;

  @SerializedName("permissions")
  public InstallationPermissions permissions;

  @SerializedName("created_at")
  public Timestamp createdAt;

  @SerializedName("updated_at")
  public Timestamp updatedAt;

  @SerializedName("has_multiple_single_files")
  public Boolean hasMultipleSingleFiles;

  @SerializedName("suspended_by")
  public User suspendedBy;

  @SerializedName("suspended_at")
  public Timestamp suspendedAt;
}
