package com.twitchy.entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.twitchy.Twitchy;
import com.twitchy.rewards.RewardAction.GearPiece;

import cpw.mods.fml.common.Loader;

public final class ViewerFollowerProfile {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, ViewerRecord> chatters = new HashMap<>();

    private static final float MIN_SCALE = 0.20F;
    private static final float MAX_SCALE = 3.0F;

    private ViewerFollowerProfile() {}

    public static class ViewerRecord {

        public List<String> unlocked = new ArrayList<>();
        public Map<Integer, GearPiece> equipped = new HashMap<>();

        public int lastHits = 0;
        public int bossLastHits = 0;
        public String minecraftUsername = "";
        public float scale = 1.0F;
        public String followerModel = "STEVE";
    }

    private static File file() {
        File dir = new File(Loader.instance().getConfigDir(), "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "ViewerProfile.json");
    }

    public static void load() {
        File f = file();
        if (!f.exists()) return;
        try (FileReader reader = new FileReader(f)) {
            java.lang.reflect.Type type = new TypeToken<Map<String, ViewerRecord>>() {}.getType();
            Map<String, ViewerRecord> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                chatters = loaded;
            }
        } catch (Exception e) {
            chatters = new HashMap<>();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(chatters, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save ViewerProfile.json", e);
        }
    }

    private static ViewerRecord recordFor(String userId) {
        return chatters.computeIfAbsent(userId, id -> new ViewerRecord());
    }

    public static List<String> getUnlocked(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? record.unlocked : Collections.emptyList();
    }

    public static boolean hasUnlocked(String userId, String key) {
        return getUnlocked(userId).contains(key);
    }

    /** Returns false if the viewer already has this key - callers use this to refuse/refund
     *  rather than let someone waste points re-buying something they already own. */
    public static boolean grantUnlock(String userId, String key) {
        ViewerRecord record = recordFor(userId);
        if (record.unlocked.contains(key)) return false;
        record.unlocked.add(key);
        save();
        return true;
    }

    public static List<GearPiece> getEquippedGear(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? new ArrayList<>(record.equipped.values()) : Collections.emptyList();
    }

    /** Equips a specific piece into its slot, only if the viewer has actually unlocked a set
     *  containing it. Returns false (does nothing) if they haven't. */
    public static boolean equipPiece(String userId, int slot, String itemNameQuery) {
        ViewerRecord record = recordFor(userId);
        GearPiece piece = GearSets.findPieceInSets(record.unlocked, slot, itemNameQuery);
        if (piece == null) return false;
        record.equipped.put(slot, piece);
        save();
        return true;
    }

    public static int getLastHits(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? record.lastHits : 0;
    }

    public static void addKill(String userId) {
        recordFor(userId).lastHits++;
        save();
    }

    public static boolean hasEnoughKills(String userId, int required) {
        int have = getLastHits(userId);
        if (required <= 0) return true;
        return have >= required;
    }

    public static void spendKills(String userId, int amount) {
        if (amount <= 0) return;
        ViewerRecord record = recordFor(userId);
        record.lastHits = Math.max(0, record.lastHits - amount);
        save();
    }

    public static int getBossLastHits(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? record.bossLastHits : 0;
    }

    public static void addBossKill(String userId) {
        recordFor(userId).bossLastHits++;
        save();
    }

    public static boolean hasEnoughBossKills(String userId, int required) {
        if (required <= 0) return true;
        return getBossLastHits(userId) >= required;
    }

    public static void spendBossKills(String userId, int amount) {
        if (amount <= 0) return;
        ViewerRecord record = recordFor(userId);
        record.bossLastHits = Math.max(0, record.bossLastHits - amount);
        save();
    }

    public static void setMinecraftUsername(String userId, String minecraftUsername) {
        recordFor(userId).minecraftUsername = minecraftUsername;
        save();
    }

    public static String getMinecraftUsername(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? record.minecraftUsername : "";
    }

    public static float getScale(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? record.scale : 1.0F;
    }

    public static float adjustScale(String userId, float delta) {
        ViewerRecord record = recordFor(userId);
        record.scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, record.scale + delta));
        save();
        return record.scale;
    }

    public static void setFollowerModel(String userId, String model) {
        recordFor(userId).followerModel = model;
        save();
    }

    public static String getFollowerModel(String userId) {
        ViewerRecord record = chatters.get(userId);
        return record != null ? record.followerModel : "STEVE";
    }
}
