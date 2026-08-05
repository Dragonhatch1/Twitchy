package com.twitchy.client;

import java.lang.reflect.Field;

import com.twitchy.Twitchy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

/**
 * Flips the camera upside-down for a set duration using EntityRenderer's own (private) camRoll
 * field - the same mechanism vanilla uses internally for camera-tilt effects. Purely a rendering
 * trick: the player's actual look direction/yaw/pitch never change, so movement and aim are
 * completely unaffected - only what's displayed on screen flips.
 */
public final class CameraFlipEffect {

    private static Field camRollField;
    private static Field prevCamRollField;
    private static volatile long revertAtMillis = 0;

    private CameraFlipEffect() {}

    static {
        try {
            camRollField = EntityRenderer.class.getDeclaredField("camRoll");
            camRollField.setAccessible(true);
            prevCamRollField = EntityRenderer.class.getDeclaredField("prevCamRoll");
            prevCamRollField.setAccessible(true);
        } catch (Exception e) {
            Twitchy.LOG.error("Could not access EntityRenderer.camRoll - camera flip effect will be disabled.", e);
        }
    }

    public static void activate(int durationSeconds) {
        setRoll(180.0F);
        revertAtMillis = System.currentTimeMillis() + Math.max(1, durationSeconds) * 1000L;
    }

    /** Call once per client tick - cheap no-op when no flip is currently active. */
    public static void tick() {
        if (revertAtMillis != 0 && System.currentTimeMillis() >= revertAtMillis) {
            setRoll(0.0F);
            revertAtMillis = 0;
        }
    }

    private static void setRoll(float roll) {
        if (camRollField == null || prevCamRollField == null) return;
        try {
            EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
            camRollField.setFloat(renderer, roll);
            prevCamRollField.setFloat(renderer, roll); // set both so it snaps instantly, no visible spin-in
        } catch (IllegalAccessException e) {
            Twitchy.LOG.error("Failed to set camera roll", e);
        }
    }
}
