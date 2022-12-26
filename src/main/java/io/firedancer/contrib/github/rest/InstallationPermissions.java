package io.firedancer.contrib.github.rest;

import com.google.gson.annotations.SerializedName;

public class InstallationPermissions {
  @SerializedName("actions")
  public String actions;

  @SerializedName("administration")
  public String administration;

  @SerializedName("checks")
  public String checks;

  @SerializedName("contents")
  public String contents;

  @SerializedName("deployments")
  public String deployments;

  @SerializedName("issues")
  public String issues;

  @SerializedName("members")
  public String members;

  @SerializedName("metadata")
  public String metadata;

  @SerializedName("organization_administration")
  public String organizationAdministration;

  @SerializedName("organization_custom_roles")
  public String organizationCustomRoles;

  @SerializedName("organization_hooks")
  public String organizationHooks;

  @SerializedName("organization_packages")
  public String organizationPackages;

  @SerializedName("organization_plan")
  public String organizationPlan;

  @SerializedName("organization_pre_receive_hooks")
  public String organizationPreReceiveHooks;

  @SerializedName("organization_projects")
  public String organizationProjects;

  @SerializedName("organization_self_hosted_runners")
  public String organizationSelfHostedRunners;

  @SerializedName("organization_user_blocking")
  public String organizationUserBlocking;

  @SerializedName("packages")
  public String packages;

  @SerializedName("pages")
  public String pages;

  @SerializedName("pull_requests")
  public String pull_requests;

  @SerializedName("repository_hooks")
  public String repository_hooks;

  @SerializedName("repository_projects")
  public String repository_projects;

  @SerializedName("repository_pre_receive_hooks")
  public String repository_pre_receive_hooks;

  @SerializedName("secrets")
  public String secrets;

  @SerializedName("secret_scanning_alerts")
  public String secret_scanning_alerts;

  @SerializedName("security_events")
  public String security_events;

  @SerializedName("single_file")
  public String single_file;

  @SerializedName("statuses")
  public String statuses;

  @SerializedName("team_discussions")
  public String team_discussions;

  @SerializedName("vulnerability_alerts")
  public String vulnerability_alerts;

  @SerializedName("workflows")
  public String workflows;
}
