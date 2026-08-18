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

/**
 * Persists each chatter's current gear AND accumulated last-hit kill count, keyed by Twitch
 * user_id (never username). Same Gson-file pattern as RewardConfig/ChatCommandConfig.
 */
public final class ViewerFollowerGear {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static Map<String, ChatterRecord> chatters = new HashMap<>();

    private ViewerFollowerGear() {}

    public static class ChatterRecord {

        public List<GearPiece> gear = new ArrayList<>();
        public int lastHits = 0;
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
        Twitchy.LOG
            .info("[DEBUG] ViewerFollowerGear.load() called. file={} exists={}", f.getAbsolutePath(), f.exists());
        if (!f.exists()) return;
        try (FileReader reader = new FileReader(f)) {
            java.lang.reflect.Type type = new TypeToken<Map<String, ChatterRecord>>() {}.getType();
            Map<String, ChatterRecord> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                chatters = loaded;
                Twitchy.LOG.info("[DEBUG] loaded {} chatter record(s), keys={}", chatters.size(), chatters.keySet());
            } else {
                Twitchy.LOG.warn("[DEBUG] fromJson returned null despite file existing");
            }
        } catch (Exception e) {
            Twitchy.LOG.error("[DEBUG] Failed to load ChatterGear.json", e);
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
        Twitchy.LOG.info(
            "[DEBUG] meetsRequirement called for userId='{}', required={}, currentChattersKeys={}",
            userId,
            required,
            chatters.keySet());
        if (required == null || required.isEmpty()) return true;
        List<GearPiece> current = getGear(userId);
        Twitchy.LOG.info("[DEBUG] current gear for '{}': {}", userId, current);
        for (GearPiece req : required) {
            boolean found = current.stream()
                .anyMatch(g -> g.item.equals(req.item) && g.metadata == req.metadata);
            if (!found) {
                Twitchy.LOG.info("[DEBUG] missing required piece: {}", req.item);
                return false;
            }
        }
        return true;
    }

    public static void applyUpgrade(String userId, List<GearPiece> newPieces) {
        Twitchy.LOG.info("[DEBUG] applyUpgrade called for userId='{}', pieces={}", userId, newPieces);
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
        Twitchy.LOG.info("[DEBUG] hasEnoughKills for '{}': have={}, required={}", userId, have, required);
        if (required <= 0) return true;
        return have >= required;
    }

    /**
     * Deducts the spent amount - called only after a gated redemption has already passed its
     * check, so this should never actually go negative in practice.
     */
    public static void spendKills(String userId, int amount) {
        if (amount <= 0) return;
        ChatterRecord record = recordFor(userId);
        record.lastHits = Math.max(0, record.lastHits - amount);
        save();
    }
}
