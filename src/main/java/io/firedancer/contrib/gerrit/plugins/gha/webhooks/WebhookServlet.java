package io.firedancer.contrib.gerrit.plugins.gha.webhooks;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.server.events.EventDispatcher;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.firedancer.contrib.gerrit.plugins.gha.config.Credentials;
import io.firedancer.contrib.gerrit.plugins.gha.webhooks.events.WebhookEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WebhookServlet extends HttpServlet {
  private static final long MAX_REQUEST_BODY_SIZE = 131072;
  private static final String HUB_SIGNATURE_256_PREFIX = "sha256=";

  private static final Logger logger = LoggerFactory.getLogger(WebhookServlet.class);

  private final Credentials creds;
  private final DynamicItem<EventDispatcher> eventDispatcher;
  private final Gson gson;

  @Inject
  WebhookServlet(Credentials creds, DynamicItem<EventDispatcher> eventDispatcher, Gson gson) {
    this.creds = creds;
    this.eventDispatcher = eventDispatcher;
    this.gson = gson;

    if (Strings.isNullOrEmpty(this.creds.getWebhookSecret())) {
      logger.error("webhook-secret not configured");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // If webhook secret is not available, skip.
    if (Strings.isNullOrEmpty(creds.getWebhookSecret())) {
      logger.debug("webhook-secret not configured");
      resp.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Misconfigured GitHub webhook server");
      return;
    }

    // If auth header is missing, skip.
    String signature = req.getHeader("x-hub-signature-256");
    if (Strings.isNullOrEmpty(signature)) {
      logger.debug("request missing signature header");
      resp.sendError(SC_UNAUTHORIZED, "Missing GitHub request signature");
      return;
    }

    // Limit max request body size to prevent spam.
    if (req.getContentLengthLong() > MAX_REQUEST_BODY_SIZE) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Oversize request body");
      return;
    }

    // Read request body.
    String body;
    try (BufferedReader reader = req.getReader()) {
      body = reader.lines().collect(Collectors.joining());
    }

    // Check request signature.
    boolean signatureValid;
    try {
      signatureValid = validateSignature256(signature, body, req.getCharacterEncoding());
    } catch (NoSuchAlgorithmException e) {
      throw new ServletException(e);
    }

    // If request signature invalid, skip.
    if (!signatureValid) {
      logger.debug("Invalid webhook signature");
      resp.sendError(SC_UNAUTHORIZED);
      return;
    }

    // Find event type.
    String eventName = req.getHeader("x-github-event");
    if (eventName == null) {
      logger.error("Received webhook without x-github-event header");
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing event name header");
      return;
    }

    // Parse request body.
    JsonObject obj;
    try {
      obj = gson.fromJson(body, JsonObject.class);
    } catch (JsonSyntaxException e) {
      logger.error("Received invalid JSON (authenticated)");
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
      return;
    }

    // Send event to stream.
    WebhookEvent event = new WebhookEvent(eventName, obj);
    try {
      eventDispatcher.get().postEvent(event);
    } catch (PermissionBackendException e) {
      throw new ServletException(e);
    }
  }

  /**
   * validates callback HMAC-SHA256 signature sent from GitHub
   *
   * @param signatureHeader signature HTTP request header of a GitHub webhook
   * @param body HTTP request body
   * @return true if webhook secret is not configured or signatureHeader is valid against payload
   *     and the secret, false if otherwise.
   */
  private boolean validateSignature256(String signatureHeader, String body, String encoding)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    byte[] payload = body.getBytes(encoding == null ? "UTF-8" : encoding);

    if (!StringUtils.startsWith(signatureHeader, HUB_SIGNATURE_256_PREFIX)) {
      logger.warn("Unsupported webhook signature type: {}", signatureHeader);
      return false;
    }
    byte[] signature;
    try {
      signature =
          Hex.decodeHex(signatureHeader.substring(HUB_SIGNATURE_256_PREFIX.length()).toCharArray());
    } catch (DecoderException e) {
      logger.error("Invalid signature: {}", signatureHeader);
      return false;
    }
    return MessageDigest.isEqual(signature, getExpectedSignature256(payload));
  }

  /**
   * Calculates the expected HMAC-SHA256 signature of the payload
   *
   * @param payload payload to calculate a signature for
   * @return signature of the payload
   * @see <a href=
   *     "https://developer.github.com/webhooks/securing/#validating-payloads-from-github">
   *     Validating payloads from GitHub</a>
   */
  private byte[] getExpectedSignature256(byte[] payload) throws NoSuchAlgorithmException {
    SecretKeySpec key = new SecretKeySpec(creds.getWebhookSecret().getBytes(), "HmacSHA256");
    Mac hmac;
    try {
      hmac = Mac.getInstance("HmacSHA256");
      hmac.init(key);
    } catch (InvalidKeyException e) {
      throw new IllegalStateException("WTF: HmacSHA256 key incompatible with HmacSHA256 hasher", e);
    }
    return hmac.doFinal(payload);
  }
}
