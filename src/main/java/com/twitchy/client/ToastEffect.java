package com.twitchy.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Renders one of several toast styles when a redemption requests one. toastType 1 (big center
 * title) and 2 (chat bubble) are one-shot: fade in, hold, fade out, then gone. toastType 3
 * (marquee ticker) is different on purpose - it's persistent and queue-driven, continuously
 * rotating through every marquee-type redemption that's come in, one at a time, at a constant
 * scroll speed regardless of message length.
 */
public class ToastEffect {

    private static final long FADE_IN_MS = 300;
    private static final long HOLD_MS = 5000;
    private static final long FADE_OUT_MS = 500;
    private static final long TOTAL_MS = FADE_IN_MS + HOLD_MS + FADE_OUT_MS;
    /** Marquee scroll speed in pixels/second - constant regardless of message length, so longer
     *  messages take proportionally longer rather than feeling rushed. */
    private static final float MARQUEE_SPEED_PX_PER_SEC = 65.0F;
    private static final float MARQUEE_TEXT_SCALE = 0.8F;

    private static volatile PendingToast pending;
    private static ActiveToast active;


    private static final Queue<MarqueeEntry> marqueeQueue = new ConcurrentLinkedQueue<>();
    private static final StringBuilder marqueeText = new StringBuilder(); // render-thread only, never cleared
    private static float marqueeScrollX;
    private static boolean marqueeStarted = false;
    private static long lastFrameNanos = 0;

    private static final String MARQUEE_SEPARATOR = "     \u2022     "; // bullet, spaced out
    private static final int MARQUEE_MAX_CHARS = 2000; // safety cap over a long stream session

    private static final int CHAT_BUBBLE_MAX_TEXT_WIDTH = 140; // px cap before text wraps to a new line

    private static class PendingToast {

        final String title;
        final String subtitle;
        final int toastType;

        PendingToast(String title, String subtitle, int toastType) {
            this.title = title;
            this.subtitle = subtitle;
            this.toastType = toastType;
        }
    }

    private static class ActiveToast {

        final String title;
        final String subtitle;
        final int toastType;
        final long startMillis;

        ActiveToast(String title, String subtitle, int toastType) {
            this.title = title;
            this.subtitle = subtitle;
            this.toastType = toastType;
            this.startMillis = System.currentTimeMillis();
        }
    }

    private static class MarqueeEntry {

        final String text;

        MarqueeEntry(String text) {
            this.text = text;
        }
    }

    /** Safe to call from ANY thread. Type 3 (marquee) joins the persistent rotation queue; types
     *  1 and 2 replace whatever one-shot toast is currently showing, same as before. */
    public static void requestShow(String title, String subtitle, int toastType) {
        if (toastType == 3) {
            String text = (subtitle != null && !subtitle.isEmpty()) ? title + "   -   " + subtitle : title;
            marqueeQueue.add(new MarqueeEntry(text));
            return;
        }
        pending = new PendingToast(title, subtitle == null ? "" : subtitle, toastType);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        tickAndRenderOneShotToast();
        tickAndRenderMarquee();
    }

    // ===================== One-shot toasts (types 1 and 2) =====================

    private void tickAndRenderOneShotToast() {
        PendingToast p = pending;
        if (p != null) {
            pending = null;
            active = new ActiveToast(p.title, p.subtitle, p.toastType);
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

        if (active.toastType == 2) {
            renderChatBubble(active.title, active.subtitle, alpha);
        } else {
            renderCenterTitle(active.title, active.subtitle, alpha, scale);
        }
    }

    private void renderCenterTitle(String title, String subtitle, float alpha, float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        int a = (int) (alpha * 255) << 24;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        float titleScale = 2.5F * scale;
        GL11.glPushMatrix();
        GL11.glTranslatef(screenWidth / 2.0F, screenHeight / 2.0F - 10, 0);
        GL11.glScalef(titleScale, titleScale, 1.0F);
        int titleWidth = font.getStringWidth(title);
        drawOutlined(font, title, -titleWidth / 2, -4, 0xFFFFFF | a, 0xC73E1D | a);
        GL11.glPopMatrix();

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

    private void renderChatBubble(String title, String subtitle, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        int a = (int) (alpha * 255) << 24;

        boolean hasSub = subtitle != null && !subtitle.isEmpty();

        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.addAll(font.listFormattedStringToWidth(title, CHAT_BUBBLE_MAX_TEXT_WIDTH));
        if (hasSub) {
            lines.addAll(font.listFormattedStringToWidth(subtitle, CHAT_BUBBLE_MAX_TEXT_WIDTH));
        }

        int actualTextWidth = 0;
        for (String line : lines) {
            actualTextWidth = Math.max(actualTextWidth, font.getStringWidth(line));
        }

        int padding = 8;
        int bubbleWidth = actualTextWidth + padding * 2;
        int lineHeight = 11;
        int bubbleHeight = padding + lineHeight * lines.size();

        int marginBottom = 100;
        int marginRight = 14;
        int right = screenWidth - marginRight;
        int left = right - bubbleWidth;
        int bottom = screenHeight - marginBottom;
        int top = bottom - bubbleHeight;

        int borderColor = a;
        int fillColor = a | 0xFFFFFF;
        int textColor = a | 0x1A1A1A;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        float radius = 6.0F;
        drawRoundedRect(left - 2, top - 2, right + 2, bottom + 2, radius, borderColor);
        drawRoundedRect(left, top, right, bottom, radius - 2, fillColor);

        int tailX = left + 20;
        drawTriangle(tailX, bottom, tailX + 16, bottom, tailX + 4, bottom + 12, borderColor);
        drawTriangle(tailX + 2, bottom, tailX + 12, bottom, tailX + 4, bottom + 8, fillColor);

        for (int i = 0; i < lines.size(); i++) {
            font.drawString(lines.get(i), left + padding, top + padding / 2 + lineHeight * i, textColor);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    // ===================== Marquee ticker (type 3) - persistent + queued =====================

    private void tickAndRenderMarquee() {
        MarqueeEntry next;
        while ((next = marqueeQueue.poll()) != null) {
            if (marqueeText.length() > 0) {
                marqueeText.append(MARQUEE_SEPARATOR);
            }
            marqueeText.append(next.text);

            if (!marqueeStarted) {
                marqueeStarted = true;
                Minecraft mc0 = Minecraft.getMinecraft();
                ScaledResolution res0 = new ScaledResolution(mc0, mc0.displayWidth, mc0.displayHeight);
                marqueeScrollX = res0.getScaledWidth();
                lastFrameNanos = System.nanoTime();
            }
        }
        trimMarqueeIfTooLong();

        if (!marqueeStarted || marqueeText.length() == 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenWidth = res.getScaledWidth();

        long now = System.nanoTime();
        float deltaSeconds = Math.min((now - lastFrameNanos) / 1_000_000_000.0F, 0.25F); // clamp against big pauses/lag spikes
        lastFrameNanos = now;

        marqueeScrollX -= MARQUEE_SPEED_PX_PER_SEC * deltaSeconds;

        String text = marqueeText.toString();
        int textWidth = font.getStringWidth(text);

        // Once the whole buffer has scrolled fully off-screen, loop back around instead of stopping -
        // any redemptions that arrived while it was scrolling are already appended into `text`, so
        // the next loop just naturally includes them.
        if (marqueeScrollX + (textWidth * MARQUEE_TEXT_SCALE) < 0) {
            marqueeScrollX = screenWidth;
        }

        int barHeight = 9;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawRect(0, 0, screenWidth, barHeight, 0x990A0A0A);

        GL11.glPushMatrix();
        GL11.glTranslatef(marqueeScrollX, barHeight / 2.0F - 4.5F * MARQUEE_TEXT_SCALE, 0);
        GL11.glScalef(MARQUEE_TEXT_SCALE, MARQUEE_TEXT_SCALE, 1.0F);
        font.drawStringWithShadow(text, 0, 0, 0xFFFFFFFF);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void trimMarqueeIfTooLong() {
        if (marqueeText.length() <= MARQUEE_MAX_CHARS) return;
        int cut = marqueeText.length() / 4; // drop roughly the oldest 25%
        int sepIndex = marqueeText.indexOf(MARQUEE_SEPARATOR, cut);
        if (sepIndex < 0) sepIndex = cut;
        marqueeText.delete(0, sepIndex + MARQUEE_SEPARATOR.length());
    }

    // ===================== Shared drawing helpers =====================

    private void drawRect(int left, int top, int right, int bottom, int color) {
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(left, bottom);
        GL11.glVertex2f(right, bottom);
        GL11.glVertex2f(right, top);
        GL11.glVertex2f(left, top);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x3, y3);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawRoundedRect(float left, float top, float right, float bottom, float radius, int color) {
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);

        // Fill the body with two overlapping strips, leaving just the four corners uncovered.
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(left + radius, bottom);
        GL11.glVertex2f(right - radius, bottom);
        GL11.glVertex2f(right - radius, top);
        GL11.glVertex2f(left + radius, top);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(left, bottom - radius);
        GL11.glVertex2f(right, bottom - radius);
        GL11.glVertex2f(right, top + radius);
        GL11.glVertex2f(left, top + radius);
        GL11.glEnd();

        // Round off each corner with a quarter-circle fan.
        drawCornerArc(left + radius, top + radius, radius, 180, 270);   // top-left
        drawCornerArc(right - radius, top + radius, radius, 270, 360);  // top-right
        drawCornerArc(right - radius, bottom - radius, radius, 0, 90);  // bottom-right
        drawCornerArc(left + radius, bottom - radius, radius, 90, 180); // bottom-left

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawCornerArc(float cx, float cy, float radius, float startDeg, float endDeg) {
        int segments = 8;
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= segments; i++) {
            double angle = Math.toRadians(startDeg + (endDeg - startDeg) * i / (double) segments);
            GL11.glVertex2f((float) (cx + Math.cos(angle) * radius), (float) (cy + Math.sin(angle) * radius));
        }
        GL11.glEnd();
    }
}
