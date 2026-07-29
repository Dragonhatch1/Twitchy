package com.twitchy.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.twitchy.Config;
import com.twitchy.Twitchy;
import com.twitchy.api.TwitchModels.EventSubEnvelope;
import com.twitchy.auth.TwitchCredentials;

/**
 * Maintains the EventSub WebSocket session used to receive live channel point redemption events.
 * Only ever runs on the client that owns the Twitch session (the broadcaster's game instance).
 */
public class TwitchEventSubClient implements WebSocket.Listener {

    private static final String EVENTSUB_WS_URL = "wss://eventsub.wss.twitch.tv/ws";
    private static final Gson GSON = new Gson();

    private final TwitchCredentials credentials;
    private final Consumer<TwitchModels.RewardRedemptionEvent> onRedemption;
    private final Runnable onSessionEstablished;
    private final Consumer<Throwable> onError;

    private WebSocket webSocket;
    private final StringBuilder messageBuffer = new StringBuilder();
    private volatile boolean intentionallyClosed = false;

    public TwitchEventSubClient(TwitchCredentials credentials, Consumer<TwitchModels.RewardRedemptionEvent> onRedemption,
        Runnable onSessionEstablished, Consumer<Throwable> onError) {
        this.credentials = credentials;
        this.onRedemption = onRedemption;
        this.onSessionEstablished = onSessionEstablished;
        this.onError = onError;
    }

    public CompletableFuture<Void> connect() {
        return connect(EVENTSUB_WS_URL);
    }

    private CompletableFuture<Void> connect(String url) {
        intentionallyClosed = false;
        HttpClient client = HttpClient.newHttpClient();
        return client.newWebSocketBuilder()
            .buildAsync(URI.create(url), this)
            .thenAccept(ws -> this.webSocket = ws);
    }

    public void disconnect() {
        intentionallyClosed = true;
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        this.webSocket = webSocket;
        Twitchy.LOG.info("EventSub WebSocket connected, awaiting welcome message...");
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        messageBuffer.append(data);
        if (last) {
            String full = messageBuffer.toString();
            messageBuffer.setLength(0);
            handleMessage(full);
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        Twitchy.LOG.info("EventSub WebSocket closed: {} {}", statusCode, reason);
        if (!intentionallyClosed && Config.autoReconnect) {
            Twitchy.LOG.info("Attempting to reconnect EventSub session...");
            connect().exceptionally(ex -> {
                onError.accept(ex);
                return null;
            });
        }
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        Twitchy.LOG.error("EventSub WebSocket error", error);
        onError.accept(error);
    }

    private void handleMessage(String json) {
        if (Config.debugLogging) {
            Twitchy.LOG.info("EventSub <- {}", json);
        }
        EventSubEnvelope envelope;
        try {
            envelope = GSON.fromJson(json, EventSubEnvelope.class);
        } catch (Exception e) {
            Twitchy.LOG.warn("Failed to parse EventSub message", e);
            return;
        }
        if (envelope == null || envelope.metadata == null) return;

        switch (envelope.metadata.message_type) {
            case "session_welcome" -> {
                String sessionId = envelope.payload != null && envelope.payload.session != null
                    ? envelope.payload.session.id
                    : null;
                if (sessionId != null) {
                    Twitchy.LOG.info("EventSub session established: {}", sessionId);
                    TwitchApiClient.subscribeToRedemptions(credentials, sessionId).thenRun(() -> {
                        if (onSessionEstablished != null) onSessionEstablished.run();
                    }).exceptionally(ex -> {
                        onError.accept(ex);
                        return null;
                    });
                }
            }
            case "session_reconnect" -> {
                String newUrl = envelope.payload != null && envelope.payload.session != null
                    ? envelope.payload.session.reconnect_url
                    : null;
                Twitchy.LOG.info("EventSub requested reconnect to a new session...");
                intentionallyClosed = true;
                if (webSocket != null) {
                    webSocket.abort();
                }
                connect(newUrl != null ? newUrl : EVENTSUB_WS_URL);
            }
            case "session_keepalive" -> {
                if (Config.debugLogging) Twitchy.LOG.info("EventSub keepalive received.");
            }
            case "notification" -> {
                if (envelope.payload != null && envelope.payload.event != null
                    && "channel.channel_points_custom_reward_redemption.add".equals(envelope.metadata.subscription_type)) {
                    onRedemption.accept(envelope.payload.event);
                }
            }
            case "revocation" -> Twitchy.LOG.warn(
                "EventSub subscription revoked: {}",
                envelope.payload != null && envelope.payload.subscription != null ? envelope.payload.subscription.status : "unknown");
            default -> {
                if (Config.debugLogging) Twitchy.LOG.info("Unhandled EventSub message type: {}", envelope.metadata.message_type);
            }
        }
    }
}
