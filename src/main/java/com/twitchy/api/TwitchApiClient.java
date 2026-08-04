package com.twitchy.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.twitchy.Config;
import com.twitchy.Twitchy;
import com.twitchy.api.TwitchModels.*;
import com.twitchy.auth.TwitchCredentials;

/** Thin wrapper around the bits of Twitch's Helix REST API this mod needs. Client-side only. */
public final class TwitchApiClient {

    private static final String HELIX_BASE = "https://api.twitch.tv/helix";
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    private TwitchApiClient() {}

    public static CompletableFuture<HelixUser> getSelfUser(String token) {
        HttpRequest request = baseRequest(HELIX_BASE + "/users", token).GET()
            .build();
        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(resp -> {
                requireOk(resp, "Get Users");
                HelixUsersResponse parsed = GSON.fromJson(resp.body(), HelixUsersResponse.class);
                if (parsed == null || parsed.data == null || parsed.data.isEmpty()) {
                    throw new RuntimeException("Twitch returned no user for this token.");
                }
                return parsed.data.get(0);
            });
    }

    public static CompletableFuture<Void> sendChatMessage(TwitchCredentials creds, String message) {
        SendChatMessageRequest body = new SendChatMessageRequest();
        body.broadcaster_id = creds.userId;
        body.sender_id = creds.userId;
        body.message = message;

        HttpRequest request = baseRequest(HELIX_BASE + "/chat/messages", creds.accessToken)
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .header("Content-Type", "application/json")
            .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(resp -> {
                requireOk(resp, "Send Chat Message");
                if (Config.debugLogging) {
                    Twitchy.LOG.info("Sent Twitch chat message: {}", message);
                }
            });
    }

    /** Subscribes an already-connected EventSub WebSocket session to channel point redemption events. */
    public static CompletableFuture<Void> subscribeToRedemptions(TwitchCredentials creds, String sessionId) {
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        req.type = "channel.channel_points_custom_reward_redemption.add";
        req.version = "1";
        req.condition = new CreateSubscriptionRequest.Condition();
        req.condition.broadcaster_user_id = creds.userId;
        req.transport = new CreateSubscriptionRequest.Transport();
        req.transport.session_id = sessionId;

        HttpRequest request = baseRequest(HELIX_BASE + "/eventsub/subscriptions", creds.accessToken)
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(req)))
            .header("Content-Type", "application/json")
            .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(resp -> {
                if (resp.statusCode() != 202 && resp.statusCode() != 200) {
                    throw new RuntimeException(
                        "Failed to subscribe to channel point redemptions (HTTP " + resp.statusCode()
                            + "): "
                            + resp.body()
                            + "\nCheck that channel:read:redemptions was granted and that you are the broadcaster.");
                }
                Twitchy.LOG.info("Subscribed to channel point redemptions for {}", creds.userLogin);
            });
    }

    public static CompletableFuture<List<CustomRewards>> listCustomRewards(TwitchCredentials creds) {
        HttpRequest request = baseRequest(
            HELIX_BASE + "/channel_points/custom_rewards?broadcaster_id="
                + creds.userId
                + "&only_manageable_rewards=true",
            creds.accessToken).GET()
                .build();
        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(resp -> {
                requireOk(resp, "Get Custom Rewards");
                CustomRewardsResponse parsed = GSON.fromJson(resp.body(), CustomRewardsResponse.class);
                return parsed != null && parsed.data != null ? parsed.data : List.<CustomRewards>of();
            });
    }

    public static CompletableFuture<CustomRewards> createCustomReward(TwitchCredentials creds, String title, int cost,
        String prompt) {
        CreateCustomRewardRequest body = new CreateCustomRewardRequest();
        body.title = title;
        body.cost = cost;
        body.prompt = prompt;

        HttpRequest request = baseRequest(
            HELIX_BASE + "/channel_points/custom_rewards?broadcaster_id=" + creds.userId,
            creds.accessToken).POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
                .header("Content-Type", "application/json")
                .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(resp -> {
                if (resp.statusCode() != 200) {
                    throw new RuntimeException(
                        "Failed to create reward '" + title
                            + "' (HTTP "
                            + resp.statusCode()
                            + "): "
                            + resp.body()
                            + "\nCheck that channel:manage:redemptions was granted.");
                }
                CustomRewardsResponse parsed = GSON.fromJson(resp.body(), CustomRewardsResponse.class);
                return parsed.data.get(0);
            });
    }

    private static HttpRequest.Builder baseRequest(String url, String token) {
        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(15))
            .header("Authorization", "Bearer " + token)
            .header("Client-Id", Config.clientId);
    }

    private static void requireOk(HttpResponse<String> resp, String action) {
        if (resp.statusCode() / 100 != 2) {
            throw new RuntimeException(
                "Twitch API call '" + action + "' failed (HTTP " + resp.statusCode() + "): " + resp.body());
        }
    }

    public static CompletableFuture<Void> updateCustomReward(TwitchCredentials creds, String twitchRewardId,
        boolean enabled) {
        UpdateCustomRewardRequest body = new UpdateCustomRewardRequest();
        body.is_enabled = enabled;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(
                URI.create(
                    HELIX_BASE + "/channel_points/custom_rewards?broadcaster_id="
                        + creds.userId
                        + "&id="
                        + twitchRewardId))
            .timeout(Duration.ofSeconds(15))
            .header("Authorization", "Bearer " + creds.accessToken)
            .header("Client-Id", Config.clientId)
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(resp -> {
                if (resp.statusCode() != 200) {
                    throw new RuntimeException(
                        "Failed to update reward " + twitchRewardId
                            + " (HTTP "
                            + resp.statusCode()
                            + "): "
                            + resp.body());
                }
            });
    }

    public static CompletableFuture<Void> updateRedemptionStatus(TwitchCredentials creds, String twitchRewardId,
        String redemptionId, boolean fulfilled) {
        UpdateRedemptionStatusRequest body = new UpdateRedemptionStatusRequest();
        body.status = fulfilled ? "FULFILLED" : "CANCELED";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(
                URI.create(
                    HELIX_BASE + "/channel_points/custom_rewards/redemptions?broadcaster_id="
                        + creds.userId
                        + "&reward_id="
                        + twitchRewardId
                        + "&id="
                        + redemptionId))
            .timeout(Duration.ofSeconds(15))
            .header("Authorization", "Bearer " + creds.accessToken)
            .header("Client-Id", Config.clientId)
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(resp -> {
                if (resp.statusCode() != 200) {
                    throw new RuntimeException(
                        "Failed to update redemption status (HTTP " + resp.statusCode() + "): " + resp.body());
                }
                if (Config.debugLogging) {
                    Twitchy.LOG.info("Redemption {} marked {}", redemptionId, body.status);
                }
            });
    }

    public static CompletableFuture<Void> subscribeToChatMessages(TwitchCredentials creds, String sessionId) {
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        req.type = "channel.chat.message";
        req.version = "1";
        req.condition = new CreateSubscriptionRequest.Condition();
        req.condition.broadcaster_user_id = creds.userId;
        req.condition.user_id = creds.userId; // reading chat as ourselves, using our own user token
        req.transport = new CreateSubscriptionRequest.Transport();
        req.transport.session_id = sessionId;

        HttpRequest request = baseRequest(HELIX_BASE + "/eventsub/subscriptions", creds.accessToken)
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(req)))
            .header("Content-Type", "application/json")
            .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
            if (resp.statusCode() != 202 && resp.statusCode() != 200) {
                throw new RuntimeException(
                    "Failed to subscribe to chat messages (HTTP " + resp.statusCode() + "): " + resp.body()
                        + "\nCheck that user:read:chat was granted.");
            }
            Twitchy.LOG.info("Subscribed to chat messages for {}", creds.userLogin);
        });
    }
}
