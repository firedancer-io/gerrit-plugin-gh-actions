package io.firedancer.contrib.github.rest;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;
import java.util.HashMap;

public class User {
  @SerializedName("login")
  public String login;

  @SerializedName("id")
  public long id;

  @SerializedName("node_id")
  public String nodeId;

  @SerializedName("avatar_url")
  public String avatarUrl;

  @SerializedName("html_url")
  public String htmlUrl;

  @SerializedName("gravatar_id")
  public String gravatarId;

  @SerializedName("name")
  public String name;

  @SerializedName("company")
  public String company;

  @SerializedName("blog")
  public String blog;

  @SerializedName("location")
  public String location;

  @SerializedName("email")
  public String email;

  @SerializedName("hireable")
  public Boolean hireable;

  @SerializedName("bio")
  public String bio;

  @SerializedName("twitter_username")
  public String twitterUsername;

  @SerializedName("public_repos")
  public Integer publicRepos;

  @SerializedName("public_gists")
  public Integer publicGists;

  @SerializedName("followers")
  public Integer followers;

  @SerializedName("following")
  public Integer following;

  @SerializedName("created_at")
  public Timestamp createdAt;

  @SerializedName("updated_at")
  public Timestamp updatedAt;

  @SerializedName("suspended_at")
  public Timestamp suspendedAt;

  @SerializedName("type")
  public String type;

  @SerializedName("site_admin")
  public Boolean siteAdmin;

  @SerializedName("total_private_repos")
  public Integer totalPrivateRepos;

  @SerializedName("owned_private_repos")
  public Integer ownedPrivateRepos;

  @SerializedName("private_gists")
  public Integer privateGists;

  @SerializedName("disk_usage")
  public Integer diskUsage;

  @SerializedName("collaborators")
  public Integer collaborators;

  @SerializedName("two_factor_authentication")
  public Boolean twoFactorAuthentication;

  @SerializedName("plan")
  public Plan plan;

  @SerializedName("ldap_dn")
  public String ldapDn;

  @SerializedName("url")
  public String url;

  @SerializedName("events_url")
  public String eventsUrl;

  @SerializedName("following_url")
  public String followingUrl;

  @SerializedName("followers_url")
  public String followersUrl;

  @SerializedName("gists_url")
  public String gistsUrl;

  @SerializedName("organizations_url")
  public String organizationsUrl;

  @SerializedName("received_events_url")
  public String receivedEventsUrl;

  @SerializedName("repos_url")
  public String reposUrl;

  @SerializedName("starred_url")
  public String starredUrl;

  @SerializedName("subscriptions_url")
  public String subscriptionsUrl;

  @SerializedName("permissions")
  public HashMap<String, Boolean> permissions;

  @SerializedName("role_name")
  public String roleName;
}
