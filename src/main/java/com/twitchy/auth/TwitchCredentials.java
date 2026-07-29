package com.twitchy.auth;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;

/**
 * Holds the OAuth user access token obtained from the implicit grant flow, plus the resolved
 * broadcaster identity. Stored on its own in config/twitchy/credentials.json (NOT in the normal
 * Forge .cfg) so people don't accidentally share/commit their token alongside ordinary settings.
 *
 * This is a client-side secret. Per the mod's trust model, it never leaves the client and the
 * dedicated server never sees or needs it.
 */
public class TwitchCredentials {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();

    public String accessToken;
    public String[] scopes;
    public String userId;
    public String userLogin;

    public boolean isPresent() {
        return accessToken != null && !accessToken.isEmpty();
    }

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "credentials.json");
    }

    public static TwitchCredentials load() {
        File f = file();
        if (!f.exists()) {
            return new TwitchCredentials();
        }
        try (FileReader reader = new FileReader(f)) {
            TwitchCredentials loaded = GSON.fromJson(reader, TwitchCredentials.class);
            return loaded != null ? loaded : new TwitchCredentials();
        } catch (IOException e) {
            Twitchy.LOG.warn("Failed to load Twitch credentials, starting fresh.", e);
            return new TwitchCredentials();
        }
    }

    public void save() {
        File f = file();
        try (FileWriter writer = new FileWriter(f)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save Twitch credentials.", e);
        }
        try {
            // Best-effort: restrict permissions since this file contains a live token.
            Files.setPosixFilePermissions(
                f.toPath(),
                java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        } catch (Exception ignored) {
            // Not all filesystems (e.g. Windows) support POSIX permissions; that's fine.
        }
    }

    public void clear() {
        accessToken = null;
        scopes = null;
        userId = null;
        userLogin = null;
        File f = file();
        if (f.exists()) {
            f.delete();
        }
    }
}
