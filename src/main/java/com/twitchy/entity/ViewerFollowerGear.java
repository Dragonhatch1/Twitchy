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

public final class ViewerFollowerGear {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static Map<String, ChatterRecord> chatters = new HashMap<>();

    private ViewerFollowerGear() {}

    public static class ChatterRecord {

        public List<GearPiece> gear = new ArrayList<>();
        public int lastHits = 0;
        public int bossLastHits = 0;
        public String minecraftUsername = "";
    }

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "ChatterGear.json");
    }

    public static void load() {
        File f = file();
        if (!f.exists()) return;
        try (FileReader reader = new FileReader(f)) {
            java.lang.reflect.Type type = new TypeToken<Map<String, ChatterRecord>>() {}.getType();
            Map<String, ChatterRecord> loaded = GSON.fromJson(reader, type);
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
            Twitchy.LOG.error("Failed to save ChatterGear.json", e);
        }
    }

    private static ChatterRecord recordFor(String userId) {
        return chatters.computeIfAbsent(userId, id -> new ChatterRecord());
    }

    // ===================== Gear =====================

    public static List<GearPiece> getGear(String userId) {
        ChatterRecord record = chatters.get(userId);
        return record != null ? record.gear : Collections.emptyList();
    }

    public static boolean meetsRequirement(String userId, List<GearPiece> required) {
        if (required == null || required.isEmpty()) return true;
        List<GearPiece> current = getGear(userId);
        for (GearPiece req : required) {
            boolean found = current.stream()
                .anyMatch(g -> g.item.equals(req.item) && g.metadata == req.metadata);
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static void applyUpgrade(String userId, List<GearPiece> newPieces) {
        ChatterRecord record = recordFor(userId);
        for (GearPiece piece : newPieces) {
            record.gear.removeIf(g -> g.slot == piece.slot);
            record.gear.add(piece);
        }
        save();
    }

    // ===================== Kill tracking =====================

    public static int getLastHits(String userId) {
        ChatterRecord record = chatters.get(userId);
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
        ChatterRecord record = recordFor(userId);
        record.lastHits = Math.max(0, record.lastHits - amount);
        save();
    }

    public static int getBossLastHits(String userId) {
        ChatterRecord record = chatters.get(userId);
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
        ChatterRecord record = recordFor(userId);
        record.bossLastHits = Math.max(0, record.bossLastHits - amount);
        save();
    }

    // ===================== Skins =====================

    public static void setMinecraftUsername(String userId, String minecraftUsername) {
        recordFor(userId).minecraftUsername = minecraftUsername;
        save();
    }

    public static String getMinecraftUsername(String userId) {
        ChatterRecord record = chatters.get(userId);
        return record != null ? record.minecraftUsername : "";
    }
}
