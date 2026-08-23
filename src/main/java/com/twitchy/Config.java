package com.twitchy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Non-secret, shareable settings. The OAuth token itself is NOT stored here -
 * see {@link com.twitchy.auth.TwitchCredentials}, which lives in its own file so people don't
 * accidentally commit/share their token along with a Forge config.
 */
public class Config {

    private static Configuration configuration;

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

    /** If true, automatically spawns random mobs near the streamer when a raid comes in. */
    public static boolean mobSpawnOnRaid = false;

    /** If true, automatically sends a Twitch Shoutout to the raiding broadcaster on an incoming raid. */
    public static boolean autoShoutout = false;

    /**
     * WebSocket URL for the EventSub connection. Only change this for local testing against the
     * Twitch CLI's mock EventSub server - leave as default for real streaming use.
     */
    public static String eventSubWsUrl = "wss://eventsub.wss.twitch.tv/ws";

    /**
     * Base URL for Helix API calls. Only change this for local testing against the Twitch CLI's
     * mock server - leave as default for real streaming use.
     */
    public static String helixApiBaseUrl = "https://api.twitch.tv/helix";

    /** If true, the broadcaster's own account also gets a viewer-follower entity spawned for them,
     *  same as any other chatter. Off by default. */
    public static boolean spawnBroadcasterFollower = false;

    // Channel-point deposit container location, set/moved via /twitchy setstorage.
    public static int storageX = 0;
    public static int storageY = 0;
    public static int storageZ = 0;
    public static int storageDimension = 0;
    public static boolean storageTargetSet = false;

    public static boolean autoConnectOnJoin = false;

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);

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
        autoConnectOnJoin = configuration.getBoolean(
            "autoConnectOnJoin",
            Configuration.CATEGORY_GENERAL,
            autoConnectOnJoin,
            "Automatically reconnect to Twitch when joining a world, if a token is already saved from a previous /twitchy connect.");
        spawnBroadcasterFollower = configuration.getBoolean(
            "SpawnBroadcasterFollower",
            Configuration.CATEGORY_GENERAL,
            spawnBroadcasterFollower,
            "Spawn a follower for the broadcaster as well. Primarily for Debugging purposes, testing redeems, etc.");

        storageX = configuration.getInt(
            "storageX",
            "storage",
            storageX,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "X coordinate of the DEPOSIT_ITEM target container.");
        storageY = configuration
            .getInt("storageY", "storage", storageY, 0, 255, "Y coordinate of the DEPOSIT_ITEM target container.");
        storageZ = configuration.getInt(
            "storageZ",
            "storage",
            storageZ,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Z coordinate of the DEPOSIT_ITEM target container.");
        storageDimension = configuration.getInt(
            "storageDimension",
            "storage",
            storageDimension,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Dimension ID of the DEPOSIT_ITEM target container (0 = Overworld, -1 = Nether, 1 = End).");
        storageTargetSet = configuration.getBoolean(
            "storageTargetSet",
            "storage",
            storageTargetSet,
            "Whether a deposit location has actually been set via /twitchy setstorage yet.");

        mobSpawnOnRaid = configuration.getBoolean(
            "MobSpawnOnRaid",
            "Raids",
            mobSpawnOnRaid,
            "Automatically spawn random mobs near the streamer on an incoming raid.");
        autoShoutout = configuration.getBoolean(
            "AutoShoutout",
            "Raids",
            autoShoutout,
            "Automatically send a Twitch Shoutout to the broadcaster that raided you.");

        eventSubWsUrl = configuration.getString(
            "eventSubWsUrl",
            "Networking",
            eventSubWsUrl,
            "EventSub WebSocket URL. Only change for local dev testing against the Twitch CLI mock server.");
        helixApiBaseUrl = configuration.getString(
            "helixApiBaseUrl",
            "Networking",
            helixApiBaseUrl,
            "Helix API base URL. Only change for local dev testing against the Twitch CLI mock server.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    /** Called by /twitchy setstorage (server-side) to move the deposit target and persist it immediately. */
    public static synchronized void setStorageTarget(int x, int y, int z, int dimension) {
        storageX = x;
        storageY = y;
        storageZ = z;
        storageDimension = dimension;
        storageTargetSet = true;

        if (configuration != null) {
            configuration.get("storage", "storageX", 0)
                .set(x);
            configuration.get("storage", "storageY", 0)
                .set(y);
            configuration.get("storage", "storageZ", 0)
                .set(z);
            configuration.get("storage", "storageDimension", 0)
                .set(dimension);
            configuration.get("storage", "storageTargetSet", false)
                .set(true);
            configuration.save();
        }
    }
}
