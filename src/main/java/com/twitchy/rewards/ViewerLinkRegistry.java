package com.twitchy.rewards;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;

/**
 * Optional mapping of Twitch login name -> Minecraft username, for servers where viewers actually
 * play on the same world as the broadcaster. Not required for a single-player "chaos mod" style setup.
 */
public class ViewerLinkRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static final Type MAP_TYPE = new TypeToken<HashMap<String, String>>() {}.getType();

    private static volatile Map<String, String> links = new HashMap<>();

    private ViewerLinkRegistry() {}

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "viewer-links.json");
    }

    public static synchronized void load() {
        File f = file();
        if (!f.exists()) {
            links = new HashMap<>();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            Map<String, String> loaded = GSON.fromJson(reader, MAP_TYPE);
            links = loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to load viewer-links.json", e);
            links = new HashMap<>();
        }
    }

    public static synchronized void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(links, MAP_TYPE, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save viewer-links.json", e);
        }
    }

    public static synchronized void link(String twitchLogin, String minecraftUsername) {
        links.put(twitchLogin.toLowerCase(), minecraftUsername);
        save();
    }

    public static synchronized void unlink(String twitchLogin) {
        links.remove(twitchLogin.toLowerCase());
        save();
    }

    public static synchronized String resolve(String twitchLogin) {
        return links.get(twitchLogin.toLowerCase());
    }
}
