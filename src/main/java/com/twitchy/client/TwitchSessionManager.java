package com.twitchy.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchEventSubClient;
import com.twitchy.auth.TwitchAuth;
import com.twitchy.auth.TwitchCredentials;
import com.twitchy.chat.ChatCommandManager;
import com.twitchy.network.DespawnAllViewerFollowersPacket;
import com.twitchy.network.PacketHandler;
import com.twitchy.rewards.RewardManager;

/**
 * Client-side only. Owns the live Twitch session: the OAuth token, the EventSub WebSocket, and
 * outgoing chat messages. There is exactly one of these per running client.
 */
public final class TwitchSessionManager {

    public static final TwitchSessionManager INSTANCE = new TwitchSessionManager();

    private TwitchCredentials credentials;
    private TwitchEventSubClient eventSubClient;
    private volatile boolean eventSubReady = false;
    private final AtomicBoolean connecting = new AtomicBoolean(false);

    private TwitchSessionManager() {
        this.credentials = TwitchCredentials.load();
    }

    public boolean hasStoredToken() {
        return credentials != null && credentials.isPresent();
    }

    public boolean isEventSubReady() {
        return eventSubReady;
    }

    public TwitchCredentials credentials() {
        return credentials;
    }

    /** Connects using a stored token if present, otherwise starts the browser OAuth flow first. */
    public CompletableFuture<Void> connect() {
        if (eventSubReady) {
            return failed("Already connected to Twitch and listening for redemptions.");
        }
        if (!connecting.compareAndSet(false, true)) {
            return failed("A connection attempt is already in progress - please wait.");
        }
        CompletableFuture<Void> result = hasStoredToken() ? syncThenStartEventSub()
            : TwitchAuth.beginAuthFlow()
                .thenCompose(creds -> {
                    this.credentials = creds;
                    return syncThenStartEventSub();
                });
        return result.whenComplete((v, err) -> connecting.set(false));
    }

    /** Forces a fresh browser authorization even if a token is already stored. */
    public CompletableFuture<Void> reauthorize() {
        if (!connecting.compareAndSet(false, true)) {
            return failed("A connection attempt is already in progress - please wait.");
        }
        disconnect();
        CompletableFuture<Void> result = TwitchAuth.beginAuthFlow()
            .thenCompose(creds -> {
                this.credentials = creds;
                return syncThenStartEventSub();
            });
        return result.whenComplete((v, err) -> connecting.set(false));
    }

    private CompletableFuture<Void> startEventSub() {
        eventSubReady = false;
        eventSubClient = new TwitchEventSubClient(
            credentials,
            RewardManager::handleRedemption,
            ChatCommandManager::handleChatMessage,
            () -> {
                eventSubReady = true;
                Twitchy.LOG.info("Twitchy is now listening for channel point redemptions on {}", credentials.userLogin);
            },
            error -> Twitchy.LOG.error("Twitch EventSub error", error));
        return eventSubClient.connect();
    }

    public void disconnect() {
        eventSubReady = false;
        connecting.set(false);
        if (eventSubClient != null) {
            PacketHandler.sendToServer(new DespawnAllViewerFollowersPacket());
            eventSubClient.disconnect();
            eventSubClient = null;
        }
    }

    public void logout() {
        disconnect();
        if (credentials != null) {
            credentials.clear();
        }
        credentials = new TwitchCredentials();
    }

    public CompletableFuture<Void> sendChatMessage(String message) {
        if (!hasStoredToken()) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                new IllegalStateException("Not connected to Twitch. Run /twitchy connect first."));
            return failed;
        }
        return TwitchApiClient.sendChatMessage(credentials, message);
    }

    /** Creates any rewards on Twitch that don't exist yet for this broadcaster, THEN starts listening. */
    private CompletableFuture<Void> syncThenStartEventSub() {
        return RewardManager.syncRewardsToTwitch(credentials)
            .thenCompose(v -> startEventSub());
    }

    private static CompletableFuture<Void> failed(String message) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        f.completeExceptionally(new IllegalStateException(message));
        return f;
    }

}
