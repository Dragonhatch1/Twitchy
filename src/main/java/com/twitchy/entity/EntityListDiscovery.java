package com.twitchy.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;

/** One-time discovery dump: on first server start, scans every entity registered across vanilla
 *  and every loaded mod (via EntityList's own public registry, confirmed real and enumerable) and
 *  writes out every EntityMob and EntityAnimal subclass found, as a starting reference for
 *  building MobSpawning.json's actual pools. Never overwrites an existing file. */
public final class EntityListDiscovery {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private EntityListDiscovery() {}

    private static File file() {
        File dir = new File(Loader.instance().getConfigDir(), "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "EntityList.json");
    }

    public static void generateIfMissing() {
        File f = file();
        if (f.exists()) return;

        List<String> animals = new ArrayList<>();
        List<String> mobs = new ArrayList<>();

        for (var entry : new TreeMap<>(EntityList.stringToClassMapping).entrySet()) {
            String name = entry.getKey();
            Class<? extends Entity> clazz = entry.getValue();
            if (EntityMob.class.isAssignableFrom(clazz)) {
                mobs.add(name);
            } else if (EntityAnimal.class.isAssignableFrom(clazz)) {
                animals.add(name);
            }
        }

        Discovery discovery = new Discovery();

        // ===================== EXPLANATION TEXT GOES HERE =====================
        discovery._comment = "This is all the Animal and Mobs available for spawn in the game. Take this and put them in MobSpawning.json in either the Regular or Boss category depending on how you want it to spawn.";
        // ========================================================================

        discovery.animals = animals;
        discovery.mobs = mobs;

        try (FileWriter writer = new FileWriter(f)) {
            GSON.toJson(discovery, writer);
            Twitchy.LOG.info(
                "Generated EntityList.json with {} animal(s) and {} mob(s).",
                animals.size(),
                mobs.size());
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to write EntityList.json", e);
        }
    }

    private static class Discovery {

        String _comment;
        List<String> animals;
        List<String> mobs;
    }
}
