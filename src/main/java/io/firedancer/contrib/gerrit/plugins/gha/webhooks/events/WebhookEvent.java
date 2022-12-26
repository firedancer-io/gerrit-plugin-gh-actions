package io.firedancer.contrib.gerrit.plugins.gha.webhooks.events;

import com.google.gerrit.server.events.Event;
import com.google.gson.JsonObject;

/** Interface to be extended by events delivered via GitHub webhooks. */
public class WebhookEvent extends Event {
  public static final String TYPE = "github-webhook";

  private final String eventName;
  private final JsonObject payload;

  public WebhookEvent(String eventName, JsonObject payload) {
    super(TYPE);
    this.eventName = eventName;
    this.payload = payload;
  }

  public String getEventName() {
    return this.eventName;
  }

  public JsonObject getPayload() {
    return this.payload;
  }
}
