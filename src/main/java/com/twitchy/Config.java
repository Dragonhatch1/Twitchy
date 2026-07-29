package com.twitchy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Non-secret, shareable settings. The OAuth token itself is NOT stored here -
 * see {@link com.twitchy.auth.TwitchCredentials}, which lives in its own file so people don't
 * accidentally commit/share their token along with a Forge config.
 */
public class Config {

    /** Your Twitch application's Client ID (from the Twitch Developer Console). Not secret, safe in this file. */
    public static String clientId = "cb4l2upufqqckuf2um852gc5rr5spg";

    /** Twitch login name (all lowercase) of the channel/broadcaster this instance manages. */
    public static String channelLogin = "";

    /**
     * Local port used for the OAuth redirect callback. Must match the "OAuth Redirect URL" registered on the
     * Twitch application as http://localhost:<port>/twitchy-callback
     */
    public static int callbackPort = 53134;

    /** If true, logs verbose Twitch API/EventSub traffic to the log file. */
    public static boolean debugLogging = false;

    /** If true, Twitchy attempts to reconnect the EventSub session automatically on disconnect. */
    public static boolean autoReconnect = true;

    /** In-game username of the broadcaster; the default target for player-affecting reward actions. */
    public static String broadcasterMinecraftUsername = "";

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        clientId = configuration.getString(
            "clientId",
            Configuration.CATEGORY_GENERAL,
            clientId,
            "Twitch application Client ID. Don't touch if you don't know what your doing.");
        channelLogin = configuration.getString(
            "channelLogin",
            Configuration.CATEGORY_GENERAL,
            channelLogin,
            "Twitch channel/broadcaster login name (lowercase) that Twitchy manages.");
        callbackPort = configuration.getInt(
            "callbackPort",
            Configuration.CATEGORY_GENERAL,
            callbackPort,
            1024,
            65535,
            "Local port for the OAuth redirect callback. Must match the redirect URL registered on the Twitch app which is 53134.");
        debugLogging = configuration.getBoolean(
            "debugLogging",
            Configuration.CATEGORY_GENERAL,
            debugLogging,
            "Verbose logging of Twitch API/EventSub traffic.");
        autoReconnect = configuration.getBoolean(
            "autoReconnect",
            Configuration.CATEGORY_GENERAL,
            autoReconnect,
            "Automatically reconnect the EventSub WebSocket session if it drops.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
