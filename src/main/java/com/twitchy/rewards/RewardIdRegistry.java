package com.twitchy.rewards;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;

/**
 * Client-side, per-broadcaster: maps a stable local reward "key" (defined once in rewards.json,
 * shared across every streamer) to the Twitch-assigned reward UUID on THIS streamer's own
 * channel. This is what lets many different streamers on one server use the same rewards.json
 * without needing to know or share each other's actual Twitch reward IDs.
 */
public class RewardIdRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type MAP_TYPE = new TypeToken<HashMap<String, String>>() {}.getType();

    /** key -> twitch reward id, for whichever broadcaster is currently connected. */
    private static volatile Map<String, String> keyToTwitchId = new HashMap<>();

    private RewardIdRegistry() {}

    private static File file(String broadcasterUserId) {
        File dir = new File(Loader.instance().getConfigDir(), "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "reward-ids-" + broadcasterUserId + ".json");
    }

    public static synchronized void load(String broadcasterUserId) {
        File f = file(broadcasterUserId);
        if (!f.exists()) {
            keyToTwitchId = new HashMap<>();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            Map<String, String> loaded = GSON.fromJson(reader, MAP_TYPE);
            keyToTwitchId = loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to load reward id registry", e);
            keyToTwitchId = new HashMap<>();
        }
    }

    public static synchronized void save(String broadcasterUserId) {
        try (FileWriter writer = new FileWriter(file(broadcasterUserId))) {
            GSON.toJson(keyToTwitchId, MAP_TYPE, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save reward id registry", e);
        }
    }

    public static synchronized void put(String broadcasterUserId, String key, String twitchRewardId) {
        keyToTwitchId.put(key, twitchRewardId);
        save(broadcasterUserId);
    }

    public static synchronized Optional<String> twitchIdForKey(String key) {
        return Optional.ofNullable(keyToTwitchId.get(key));
    }

    /** Translates an incoming Twitch reward id back to our stable local key. */
    public static synchronized Optional<String> keyForTwitchId(String twitchRewardId) {
        return keyToTwitchId.entrySet().stream()
            .filter(e -> e.getValue().equals(twitchRewardId))
            .map(Map.Entry::getKey)
            .findFirst();
    }
}
