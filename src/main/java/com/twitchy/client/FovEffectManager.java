package com.twitchy.client;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;

/**
 * Applies a persistent FOV offset that survives game restarts and resets automatically at a
 * fixed daily time (default 6pm local time). Uses EntityRenderer's debugCamFOV/prevDebugCamFOV
 * fields - normally only used by the F4 debug camera views, otherwise untouched by vanilla,
 * making them a safe additive FOV hook the same way camRoll was for the camera flip.
 */
public final class FovEffectManager {

    /** Hour of day (24h, local time) the effect resets back to 0. */
    private static final int DAILY_RESET_HOUR = 18; // 6pm

    private static Field debugCamFOVField;
    private static Field prevDebugCamFOVField;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static volatile State state = new State();
    private static volatile float pendingDelta = 0.0F;

    /** Vanilla's own FOV options-menu slider range (30-110, unchanged since 1.7.10). */
    private static final float FOV_MIN = 30.0F;
    private static final float FOV_MAX = 110.0F;

    private FovEffectManager() {}

    private static class State {

        float offset = 0.0F;
        String lastResetDate = ""; // yyyy-MM-dd, empty = never reset yet
    }

    static {
        debugCamFOVField = findField("debugCamFOV", "field_78493_M");
        prevDebugCamFOVField = findField("prevDebugCamFOV", "field_78494_N");
        if (debugCamFOVField == null || prevDebugCamFOVField == null) {
            Twitchy.LOG.error(
                "Could not access EntityRenderer.debugCamFOV (tried both MCP and SRG names) - FOV effect will be disabled.");
        }
        load();
    }

    private static Field findField(String mcpName, String srgName) {
        for (String name : new String[] { mcpName, srgName }) {
            try {
                Field f = EntityRenderer.class.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
                // try the next name
            }
        }
        return null;
    }

    /** Safe to call from ANY thread - just queues the request. */
    public static void requestApply(float offsetDelta) {
        pendingDelta += offsetDelta;
    }

    /** Call once per client tick, on the client thread. */
    public static void tick() {
        if (pendingDelta != 0.0F) {
            float delta = pendingDelta;
            pendingDelta = 0.0F;
            state.offset = clampOffset(state.offset + delta);
            state.lastResetDate = LocalDate.now()
                .toString();
            save();
            applyToRenderer(state.offset);
        }

        checkForDailyReset();
    }

    private static void checkForDailyReset() {
        if (state.offset == 0.0F) return; // nothing active, nothing to reset

        LocalDateTime now = LocalDateTime.now();
        String today = now.toLocalDate()
            .toString();
        boolean pastResetTimeToday = now.getHour() >= DAILY_RESET_HOUR;

        // Reset once we're at/after today's reset hour AND haven't already reset today
        // (also covers redeeming after the reset hour on a day we haven't reset yet, and
        // simply having the game closed across the reset time - it catches up on next tick).
        if (pastResetTimeToday && !today.equals(state.lastResetDate)) {
            Twitchy.LOG.info("FOV effect reset (daily {}:00 reset reached).", DAILY_RESET_HOUR);
            state.offset = 0.0F;
            state.lastResetDate = today;
            save();
            applyToRenderer(0.0F);
        } else if (!pastResetTimeToday) {
            // A new day started before the reset hour - make sure a stale lastResetDate from
            // yesterday doesn't block today's reset once we do reach the hour.
            if (!state.lastResetDate.isEmpty() && !state.lastResetDate.equals(today)
                && LocalDate.parse(state.lastResetDate)
                    .isBefore(
                        now.toLocalDate()
                            .minusDays(1))) {
                // no-op safeguard slot for future use; current logic already re-checks by date string
            }
        }
    }

    private static void applyToRenderer(float offset) {
        if (debugCamFOVField == null || prevDebugCamFOVField == null) return;
        try {
            EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
            if (renderer == null) return; // not fully initialized yet (e.g. very early client tick)
            debugCamFOVField.setFloat(renderer, offset);
            prevDebugCamFOVField.setFloat(renderer, offset);
        } catch (IllegalAccessException e) {
            Twitchy.LOG.error("Failed to apply FOV offset", e);
        }
    }

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "fov-state.json");
    }

    private static void load() {
        File f = file();
        if (!f.exists()) return;
        try (FileReader reader = new FileReader(f)) {
            State loaded = GSON.fromJson(reader, State.class);
            if (loaded != null) {
                state = loaded;
                // Re-apply whatever offset was active before the last restart, then let
                // checkForDailyReset() decide on the very next tick whether it should already
                // have expired while the game was closed.
                state.offset = clampOffset(state.offset);
                applyToRenderer(state.offset);
            }
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to load fov-state.json", e);
        }
    }

    private static void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(state, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save fov-state.json", e);
        }
    }

    private static float clampOffset(float offset) {
        float baseFov = Minecraft.getMinecraft().gameSettings != null ? Minecraft.getMinecraft().gameSettings.fovSetting
            : 70.0F; // vanilla default, only used if gameSettings somehow isn't ready yet
        float minOffset = FOV_MIN - baseFov;
        float maxOffset = FOV_MAX - baseFov;
        return Math.max(minOffset, Math.min(maxOffset, offset));
    }
}
