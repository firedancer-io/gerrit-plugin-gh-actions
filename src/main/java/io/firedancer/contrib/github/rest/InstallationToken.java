package io.firedancer.contrib.github.rest;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class InstallationToken {
  @SerializedName("token")
  public String token;

  @SerializedName("expires_at")
  public Timestamp expiresAt;

  @SerializedName("permissions")
  public InstallationPermissions permissions;

  @SerializedName("repositories")
  public String[] repositories;
}
