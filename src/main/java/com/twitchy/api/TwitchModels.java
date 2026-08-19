package com.twitchy.api;

import java.util.List;

import com.google.gson.JsonObject;

/** Plain data holders matching Twitch's JSON shapes. Field names intentionally match Twitch's API (snake_case). */
public final class TwitchModels {

    private TwitchModels() {}

    public static class HelixUser {

        public String id;
        public String login;
        public String display_name;
    }

    public static class HelixUsersResponse {

        public List<HelixUser> data;
    }

    public static class EventSubSession {

        public String id;
        public String status;
        public int keepalive_timeout_seconds;
        public String reconnect_url;
    }

    public static class EventSubEnvelope {

        public EventSubMetadata metadata;
        public EventSubNotificationPayload payload;
    }

    public static class EventSubMetadata {

        public String message_id;
        public String message_type; // session_welcome, session_keepalive, notification, session_reconnect, revocation
        public String subscription_type;
    }

    public static class EventSubNotificationPayload {

        public EventSubSubscription subscription;
        public JsonObject event;
        public EventSubSession session; // present on session_reconnect
    }

    public static class EventSubSubscription {

        public String id;
        public String type;
        public String status;
    }

    /** channel.channel_points_custom_reward_redemption.add event body. */
    public static class RewardRedemptionEvent {

        public String id; // redemption id
        public String broadcaster_user_id;
        public String broadcaster_user_login;
        public String user_id;
        public String user_login;
        public String user_name;
        public String user_input;
        public String status;
        public Reward reward;
        public String redeemed_at;
    }

    public static class Reward {

        public String id;
        public String title;
        public int cost;
        public String prompt;
    }

    public static class CreateSubscriptionRequest {

        public String type;
        public String version;
        public Condition condition;
        public Transport transport;

        public static class Condition {

            public String broadcaster_user_id;
            public String moderator_user_id;
            public String user_id;
            public String to_broadcaster_user_id;
        }

        public static class Transport {

            public String method = "websocket";
            public String session_id;
        }
    }

    public static class SendChatMessageRequest {

        public String broadcaster_id;
        public String sender_id;
        public String message;
    }

    public static class CustomRewards {

        public String id;
        public String broadcaster_id;
        public String title;
        public int cost;
        public String prompt;
        public boolean is_enabled = true;
    }

    public static class CustomRewardsResponse {

        public List<CustomRewards> data;
    }

    public static class CreateCustomRewardRequest {

        public String title;
        public int cost;
        public String prompt;
        public boolean is_enabled = true;
        public boolean is_user_input_required;
    }

    public static class UpdateCustomRewardRequest {

        public Boolean is_enabled;
        public Boolean is_user_input_required;
    }

    public static class UpdateRedemptionStatusRequest {

        public String status;
    }

    public static class ChatMessageEvent {

        public String broadcaster_user_id;
        public String broadcaster_user_login;
        public String chatter_user_id;
        public String chatter_user_login;
        public String chatter_user_name;
        public String message_id;
        public ChatMessage message;
        public String message_type;
    }

    public static class ChatMessage {

        public String text;
    }

    public static class ChattersResponse {

        public List<Chatter> data;
        public Pagination pagination;
        public int total;
    }

    public static class Chatter {

        public String user_id;
        public String user_login;
        public String user_name;
    }

    public static class Pagination {

        public String cursor;
    }

    public static class RaidEvent {

        public String from_broadcaster_user_id;
        public String from_broadcaster_user_login;
        public String from_broadcaster_user_name;
        public int viewer_count;
    }
}
