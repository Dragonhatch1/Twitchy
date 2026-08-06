package com.twitchy.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Big, center-screen title/subtitle splash - the "Cinematic Title" toast style, matching
 * design concept #2. Purely a rendering effect, drawn as a HUD overlay so it works regardless
 * of what triggered it (item redemptions, chat commands, anything else later).
 *
 * Thread safety: requestShow() is safe to call from any thread (EventSub messages arrive on a
 * background thread). The actual state transition and all rendering happen in onRenderOverlay,
 * which only ever runs on the render thread.
 */
public class ToastEffect {

    private static final long FADE_IN_MS = 300;
    private static final long HOLD_MS = 2200;
    private static final long FADE_OUT_MS = 500;
    private static final long TOTAL_MS = FADE_IN_MS + HOLD_MS + FADE_OUT_MS;

    private static volatile PendingToast pending;
    private static ActiveToast active;

    private static class PendingToast {

        final String title;
        final String subtitle;

        PendingToast(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    private static class ActiveToast {

        final String title;
        final String subtitle;
        final long startMillis;

        ActiveToast(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
            this.startMillis = System.currentTimeMillis();
        }
    }

    /** Safe to call from ANY thread - just queues the toast to actually start on the render thread. */
    public static void requestShow(String title, String subtitle) {
        pending = new PendingToast(title, subtitle == null ? "" : subtitle);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return; // once per frame, after everything else

        PendingToast p = pending;
        if (p != null) {
            pending = null;
            active = new ActiveToast(p.title, p.subtitle);
        }

        if (active == null) return;

        long elapsed = System.currentTimeMillis() - active.startMillis;
        if (elapsed >= TOTAL_MS) {
            active = null;
            return;
        }

        float alpha;
        float scale;
        if (elapsed < FADE_IN_MS) {
            float t = elapsed / (float) FADE_IN_MS;
            alpha = t;
            scale = 0.85F + 0.15F * t;
        } else if (elapsed < FADE_IN_MS + HOLD_MS) {
            alpha = 1.0F;
            scale = 1.0F;
        } else {
            float t = (elapsed - FADE_IN_MS - HOLD_MS) / (float) FADE_OUT_MS;
            alpha = 1.0F - t;
            scale = 1.0F;
        }

        render(active.title, active.subtitle, alpha, scale);
    }

    private void render(String title, String subtitle, float alpha, float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        int a = (int) (alpha * 255) << 24;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // --- Main title: big, centered, ember-colored outline behind white fill ---
        float titleScale = 2.5F * scale;
        GL11.glPushMatrix();
        GL11.glTranslatef(screenWidth / 2.0F, screenHeight / 2.0F - 10, 0);
        GL11.glScalef(titleScale, titleScale, 1.0F);
        int titleWidth = font.getStringWidth(title);
        drawOutlined(font, title, -titleWidth / 2, -4, 0xFFFFFF | a, 0xC73E1D | a);
        GL11.glPopMatrix();

        // --- Subtitle: smaller, gold, below the title ---
        if (subtitle != null && !subtitle.isEmpty()) {
            float subScale = 1.2F * scale;
            GL11.glPushMatrix();
            GL11.glTranslatef(screenWidth / 2.0F, screenHeight / 2.0F + 18, 0);
            GL11.glScalef(subScale, subScale, 1.0F);
            int subWidth = font.getStringWidth(subtitle);
            font.drawStringWithShadow(subtitle, -subWidth / 2, -4, 0xFFD873 | a);
            GL11.glPopMatrix();
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void drawOutlined(FontRenderer font, String text, int x, int y, int mainColor, int outlineColor) {
        font.drawString(text, x - 2, y - 2, outlineColor);
        font.drawString(text, x + 2, y - 2, outlineColor);
        font.drawString(text, x - 2, y + 2, outlineColor);
        font.drawString(text, x + 2, y + 2, outlineColor);
        font.drawString(text, x, y, mainColor);
    }
}
