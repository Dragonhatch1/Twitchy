package com.twitchy.entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;

/** Regular/boss mob pools for random spawning, loaded from config/twitchy/MobSpawning.json.
 *  Both client and server maintain their own copy of this same file - same reasoning as
 *  RewardConfig: the client picks from its own local pool when requesting a spawn, and the
 *  server independently validates every request against its own copy before spawning anything,
 *  never trusting the client's selection blindly. In singleplayer, both sides share the same
 *  integrated-server config directory, so they trivially read the identical file. */
public final class MobSpawningConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Random RANDOM = new Random();
    private static Pools pools = new Pools();

    private MobSpawningConfig() {}

    public static class Pools {

        public List<String> regular = new ArrayList<>();
        public List<String> boss = new ArrayList<>();
    }

    private static File file() {
        File dir = new File(Loader.instance().getConfigDir(), "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "MobSpawning.json");
    }

    public static void load() {
        File f = file();
        if (!f.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            Pools loaded = GSON.fromJson(reader, Pools.class);
            pools = loaded != null ? loaded : new Pools();
            Twitchy.LOG.info(
                "Loaded MobSpawning.json: {} regular, {} boss.",
                pools.regular.size(),
                pools.boss.size());
        } catch (Exception e) {
            Twitchy.LOG.error("Failed to load MobSpawning.json, using empty pools.", e);
            pools = new Pools();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(pools, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save MobSpawning.json", e);
        }
    }

    /** Server-side: is this entity name allowed to spawn at all, checked against both pools
     *  combined. The server doesn't care whether something is "regular" or "boss" - that
     *  categorization is purely a client-side concern (which pool to draw from, which kill counter
     *  to credit). This is just a flat safety allow-list. */
    public static boolean isAllowed(String entityName) {
        return pools.regular.contains(entityName) || pools.boss.contains(entityName);
    }

    /** Client-side: pick `count` random names from this streamer's own local pool, to send as a
     *  spawn request. The server-side isAllowed check still has final say once the request arrives. */
    public static List<String> pickRandom(int count, boolean boss) {
        List<String> source = boss ? pools.boss : pools.regular;
        List<String> picks = new ArrayList<>();
        if (source.isEmpty()) return picks;
        for (int i = 0; i < count; i++) {
            picks.add(source.get(RANDOM.nextInt(source.size())));
        }
        return picks;
    }
}
