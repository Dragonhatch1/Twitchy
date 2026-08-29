package com.twitchy.entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.twitchy.Twitchy;
import com.twitchy.rewards.RewardAction.GearPiece;

import cpw.mods.fml.common.Loader;

public final class GearSets {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static Map<String, GearSetEntry> sets = new LinkedHashMap<>();

    private GearSets() {}

    public static class GearSetEntry {

        public String displayName;
        public List<GearPiece> pieces;
    }

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "GearSets.json");
    }

    public static void load() {
        File f = file();
        if (!f.exists()) {
            sets = defaultSets();
            save();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            java.lang.reflect.Type type = new TypeToken<Map<String, GearSetEntry>>() {}.getType();
            Map<String, GearSetEntry> loaded = GSON.fromJson(reader, type);
            sets = loaded != null ? loaded : new LinkedHashMap<>();
            Twitchy.LOG.info("Loaded GearSets.json: {} set(s).", sets.size());
        } catch (Exception e) {
            Twitchy.LOG.error("Failed to load GearSets.json, using empty sets.", e);
            sets = new LinkedHashMap<>();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(sets, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save GearSets.json", e);
        }
    }

    public static List<GearPiece> getSet(String key) {
        GearSetEntry entry = sets.get(key);
        return entry != null && entry.pieces != null ? entry.pieces : Collections.emptyList();
    }

    public static String getDisplayName(String key) {
        if (key == null) return "";
        GearSetEntry entry = sets.get(key);
        return entry != null && entry.displayName != null && !entry.displayName.isBlank() ? entry.displayName : key;
    }

    public static boolean exists(String key) {
        return sets.containsKey(key);
    }

    public static Iterable<String> allKeys() {
        return sets.keySet();
    }

    public static GearPiece findPieceInSets(List<String> unlockedKeys, int slot, String itemNameQuery) {
        String query = itemNameQuery.toLowerCase();
        for (String key : unlockedKeys) {
            for (GearPiece piece : getSet(key)) {
                if (piece.slot != slot) continue;
                String shortName = piece.item.contains(":") ? piece.item.substring(piece.item.indexOf(':') + 1)
                    : piece.item;
                if (shortName.equalsIgnoreCase(query)) return piece;
            }
        }
        return null;
    }

    public static List<GearPiece> findAllPiecesForSlot(List<String> unlockedKeys, int slot) {
        List<GearPiece> matches = new java.util.ArrayList<>();
        for (String key : unlockedKeys) {
            for (GearPiece piece : getSet(key)) {
                if (piece.slot == slot) matches.add(piece);
            }
        }
        return matches;
    }

    private static Map<String, GearSetEntry> defaultSets() {
        Map<String, GearSetEntry> defaults = new LinkedHashMap<>();

        defaults.put(
            "leatherarmour",
            entry(
                "Leather Armour",
                piece("minecraft:leather_helmet", 0, 4),
                piece("minecraft:leather_chestplate", 0, 3),
                piece("minecraft:leather_leggings", 0, 2),
                piece("minecraft:leather_boots", 0, 1)));

        defaults.put(
            "goldarmour",
            entry(
                "Gold Armour",
                piece("minecraft:golden_helmet", 0, 4),
                piece("minecraft:golden_chestplate", 0, 3),
                piece("minecraft:golden_leggings", 0, 2),
                piece("minecraft:golden_boots", 0, 1)));

        defaults.put(
            "chainarmour",
            entry(
                "Chain Armour",
                piece("minecraft:chainmail_helmet", 0, 4),
                piece("minecraft:chainmail_chestplate", 0, 3),
                piece("minecraft:chainmail_leggings", 0, 2),
                piece("minecraft:chainmail_boots", 0, 1)));

        defaults.put(
            "ironarmour",
            entry(
                "Iron Armour",
                piece("minecraft:iron_helmet", 0, 4),
                piece("minecraft:iron_chestplate", 0, 3),
                piece("minecraft:iron_leggings", 0, 2),
                piece("minecraft:iron_boots", 0, 1)));

        defaults.put(
            "diamondarmour",
            entry(
                "Diamond Armour",
                piece("minecraft:diamond_helmet", 0, 4),
                piece("minecraft:diamond_chestplate", 0, 3),
                piece("minecraft:diamond_leggings", 0, 2),
                piece("minecraft:diamond_boots", 0, 1)));

        defaults.put("woodensword", entry("Wooden Sword", piece("minecraft:wooden_sword", 0, 0)));
        defaults.put("stonesword", entry("Stone Sword", piece("minecraft:stone_sword", 0, 0)));
        defaults.put("ironsword", entry("Iron Sword", piece("minecraft:iron_sword", 0, 0)));
        defaults.put("diamondsword", entry("Diamond Sword", piece("minecraft:diamond_sword", 0, 0)));

        return defaults;
    }

    private static GearSetEntry entry(String displayName, GearPiece... pieces) {
        GearSetEntry e = new GearSetEntry();
        e.displayName = displayName;
        e.pieces = List.of(pieces);
        return e;
    }

    private static GearPiece piece(String item, int metadata, int slot) {
        GearPiece p = new GearPiece();
        p.item = item;
        p.metadata = metadata;
        p.slot = slot;
        return p;
    }

    public static void putSet(String key, String displayName, List<GearPiece> pieces) {
        GearSetEntry entry = new GearSetEntry();
        entry.displayName = displayName;
        entry.pieces = pieces;
        sets.put(key, entry);
        save();
    }

    public static void deleteSet(String key) {
        sets.remove(key);
        save();
    }
}
