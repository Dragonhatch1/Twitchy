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
import com.twitchy.api.TwitchModels.ChatMessageEvent;
import com.twitchy.api.TwitchModels.EventSubEnvelope;
import com.twitchy.api.TwitchModels.RewardRedemptionEvent;
import com.twitchy.auth.TwitchCredentials;

/**
 * Maintains the EventSub WebSocket session used to receive live channel point redemption events.
 * Only ever runs on the client that owns the Twitch session (the broadcaster's game instance).
 */
public class TwitchEventSubClient implements WebSocket.Listener {

    private static final String EVENTSUB_WS_URL = "wss://eventsub.wss.twitch.tv/ws";
    private static final Gson GSON = new Gson();

    private final TwitchCredentials credentials;
    private final Consumer<RewardRedemptionEvent> onRedemption;
    private final Consumer<ChatMessageEvent> onChatMessage;
    private final Runnable onSessionEstablished;
    private final Consumer<Throwable> onError;

    private WebSocket webSocket;
    private volatile WebSocket currentSocket;
    private final StringBuilder messageBuffer = new StringBuilder();
    private volatile boolean intentionallyClosed = false;

    public TwitchEventSubClient(TwitchCredentials credentials, Consumer<RewardRedemptionEvent> onRedemption,
        Consumer<ChatMessageEvent> onChatMessage, Runnable onSessionEstablished, Consumer<Throwable> onError) {
        this.credentials = credentials;
        this.onRedemption = onRedemption;
        this.onChatMessage = onChatMessage;
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
            .thenAccept(ws -> {
                this.webSocket = ws;
                this.currentSocket = ws;
            });
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
        this.currentSocket = webSocket;
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
        Twitchy.LOG.info("[DEBUG] onClose fired. intentionallyClosed={} webSocket==currentSocket={}",
            intentionallyClosed, webSocket == currentSocket);
        Twitchy.LOG.info("EventSub WebSocket closed: {} {}", statusCode, reason);
        if (webSocket != currentSocket) {
            return null;
        }
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
                    CompletableFuture<Void> redemptionSub = TwitchApiClient
                        .subscribeToRedemptions(credentials, sessionId);
                    CompletableFuture<Void> chatSub = TwitchApiClient.subscribeToChatMessages(credentials, sessionId)
                        .exceptionally(ex -> {
                            // Not fatal - redemptions still work fine without chat commands.
                            Twitchy.LOG.warn(
                                "Failed to subscribe to chat messages, chat commands won't work: {}",
                                ex.getMessage());
                            return null;
                        });
                    java.util.concurrent.CompletableFuture.allOf(redemptionSub, chatSub)
                        .thenRun(() -> { if (onSessionEstablished != null) onSessionEstablished.run(); })
                        .exceptionally(ex -> {
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
                WebSocket old = webSocket;
                currentSocket = null;

                if (old != null) {
                    old.abort();
                }
                connect(newUrl != null ? newUrl : EVENTSUB_WS_URL);
            }
            case "session_keepalive" -> {
                if (Config.debugLogging) Twitchy.LOG.info("EventSub keepalive received.");
            }
            case "notification" -> {
                if (envelope.payload != null && envelope.payload.event != null) {
                    String subType = envelope.metadata.subscription_type;
                    if ("channel.channel_points_custom_reward_redemption.add".equals(subType)) {
                        onRedemption
                            .accept(GSON.fromJson(envelope.payload.event, TwitchModels.RewardRedemptionEvent.class));
                    } else if ("channel.chat.message".equals(subType)) {
                        onChatMessage
                            .accept(GSON.fromJson(envelope.payload.event, TwitchModels.ChatMessageEvent.class));
                    }
                }
            }
            case "revocation" -> Twitchy.LOG.warn(
                "EventSub subscription revoked: {}",
                envelope.payload != null && envelope.payload.subscription != null ? envelope.payload.subscription.status
                    : "unknown");
            default -> {
                if (Config.debugLogging)
                    Twitchy.LOG.info("Unhandled EventSub message type: {}", envelope.metadata.message_type);
            }
        }
    }
}
