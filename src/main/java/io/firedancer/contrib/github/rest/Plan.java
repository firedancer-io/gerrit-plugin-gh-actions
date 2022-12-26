package io.firedancer.contrib.github.rest;

import com.google.gson.annotations.SerializedName;

public class Plan {
  @SerializedName("name")
  public String name;

  @SerializedName("space")
  public Integer space;

  @SerializedName("collaborators")
  public Integer collaborators;

  @SerializedName("private_repos")
  public Integer privateRepos;

  @SerializedName("filled_seats")
  public Integer filledSeats;

  @SerializedName("seats")
  public Integer seats;
}
