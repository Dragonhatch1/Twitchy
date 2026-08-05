package com.twitchy.client;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import com.twitchy.Twitchy;

/**
 * Flips the camera upside-down for a set duration using EntityRenderer's own (private) camRoll
 * field - the same mechanism vanilla uses internally for camera-tilt effects. Purely a rendering
 * trick: the player's actual look direction/yaw/pitch never change, so movement and aim are
 * completely unaffected - only what's displayed on screen flips.
 */
public final class CameraFlipEffect {

    /**
     * Degrees the roll moves per client tick. At 20 ticks/sec, 18F means a full 180-degree spin
     * takes 10 ticks (0.5 seconds). Lower = slower/smoother, higher = faster/snappier.
     */
    private static final float SPIN_SPEED_DEG_PER_TICK = 18.0F;

    private static Field camRollField;
    private static Field prevCamRollField;
    private static volatile long revertAtMillis = 0;
    private static volatile int pendingActivateSeconds = 0;
    private static volatile float targetRoll = 0.0F;

    private CameraFlipEffect() {}

    static {
        camRollField = findField("camRoll", "field_78495_O");
        prevCamRollField = findField("prevCamRoll", "field_78505_P");
        if (camRollField == null || prevCamRollField == null) {
            Twitchy.LOG.error(
                "Could not access EntityRenderer.camRoll (tried both MCP and SRG names) - camera flip effect will be disabled.");
        }
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

    public static void requestActivate(int durationSeconds) {
        pendingActivateSeconds = Math.max(1, durationSeconds);
    }

    public static void activate(int durationSeconds) {
        targetRoll = 180.0F;
        revertAtMillis = System.currentTimeMillis() + Math.max(1, durationSeconds) * 1000L;
    }

    /** Call once per client tick - cheap no-op when no flip is currently active. */
    public static void tick() {
        if (pendingActivateSeconds > 0) {
            int seconds = pendingActivateSeconds;
            pendingActivateSeconds = 0;
            activate(seconds);
        }
        if (revertAtMillis != 0 && System.currentTimeMillis() >= revertAtMillis) {
            targetRoll = 0.0F; // going back to normal
            revertAtMillis = 0;
        }
        stepTowardTarget();
    }

    private static void stepTowardTarget() {
        if (camRollField == null || prevCamRollField == null) return;
        try {
            EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
            float current = camRollField.getFloat(renderer);
            if (current == targetRoll) return;

            float next = targetRoll > current ? Math.min(targetRoll, current + SPIN_SPEED_DEG_PER_TICK)
                : Math.max(targetRoll, current - SPIN_SPEED_DEG_PER_TICK);
            camRollField.setFloat(renderer, next);
        } catch (IllegalAccessException e) {
            Twitchy.LOG.error("Failed to update camera roll", e);
        }
    }
}
