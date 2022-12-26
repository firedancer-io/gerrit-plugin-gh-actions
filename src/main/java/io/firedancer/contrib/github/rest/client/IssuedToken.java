package io.firedancer.contrib.github.rest.client;

import java.time.Instant;

/** An auth token with a pre-determined expiration time. */
public class IssuedToken {
  IssuedToken(String token, Instant expiresAt) {
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public String token;
  public Instant expiresAt;

  /** Clock drift tolerance to factor into expiry date calculation. */
  private static final int CLOCK_DRIFT_SECS = 10;

  /** Returns whether a token has expired. */
  public boolean isExpired() {
    return Instant.now().isAfter(this.expiresAt.minusSeconds(CLOCK_DRIFT_SECS));
  }
}
