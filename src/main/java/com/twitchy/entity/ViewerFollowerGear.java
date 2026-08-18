package com.twitchy.entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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

/** Persists each chatter's current gear set, keyed by Twitch user_id (never username - logins can
 *  change, IDs never do). Same Gson-file pattern as RewardConfig/ChatCommandConfig. */
public final class ViewerFollowerGear {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, List<GearPiece>> gear = new HashMap<>();

    private ViewerFollowerGear() {}

    private static File file() {
        File dir = new File(Loader.instance().getConfigDir(), "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "ChatterGear.json");
    }

    public static void load() {
        File f = file();
        if (!f.exists()) return;
        try (FileReader reader = new FileReader(f)) {
            Type type = new TypeToken<Map<String, List<GearPiece>>>() {}.getType();
            Map<String, List<GearPiece>> loaded = GSON.fromJson(reader, type);
            if (loaded != null) gear = loaded;
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to load ChatterGear.json", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(gear, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save ChatterGear.json", e);
        }
    }

    public static List<GearPiece> getGear(String userId) {
        return gear.getOrDefault(userId, Collections.emptyList());
    }

    /** True only if every required piece is present in this chatter's current gear (matched by
     *  item id + metadata - slot is implied by what the piece actually is). */
    public static boolean meetsRequirement(String userId, List<GearPiece> required) {
        if (required == null || required.isEmpty()) return true;
        List<GearPiece> current = getGear(userId);
        for (GearPiece req : required) {
            boolean found = current.stream().anyMatch(g -> g.item.equals(req.item) && g.metadata == req.metadata);
            if (!found) return false;
        }
        return true;
    }

    /** Each new piece overwrites whatever was previously stored for its own slot - matches how
     *  armor slots naturally work (one item per slot at a time). */
    public static void applyUpgrade(String userId, List<GearPiece> newPieces) {
        List<GearPiece> current = new ArrayList<>(getGear(userId));
        for (GearPiece piece : newPieces) {
            current.removeIf(g -> g.slot == piece.slot);
            current.add(piece);
        }
        gear.put(userId, current);
        save();
    }
}
