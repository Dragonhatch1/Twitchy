package com.twitchy.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.twitchy.entity.MobSpawningConfig;
import com.twitchy.network.PacketHandler;
import com.twitchy.network.RequestMobSpawnPacket;

public class GuiDirectionalChallenge extends GuiScreen {

    private final String[] sequence;
    private final long totalMillis;

    private int progressIndex = 0;
    private long startMillis;
    private long flashUntilMillis = 0;
    private boolean resolved = false;

    private static final int SLOT_SIZE_CURRENT = 44;
    private static final int SLOT_SIZE_NEXT = 30;
    private static final int SLOT_GAP = 10;

    private static final int VISIBLE_WINDOW = 6; // how many slots shown at once (current + upcoming)
    private static final long SLIDE_ANIM_MS = 150;

    private float slideOffset = 0.0F; // 1.0 = just shifted, decays to 0.0 = settled
    private long lastRenderNanos = 0;

    private final String title;
    private final String subtitle;
    private final int regularSpawnCount;
    private final int bossSpawnCount;

    public GuiDirectionalChallenge(String[] sequence, int seconds, String title, String subtitle, int regularSpawnCount,
        int bossSpawnCount) {
        this.sequence = sequence;
        this.totalMillis = seconds * 1000L;
        this.title = title;
        this.subtitle = subtitle;
        this.regularSpawnCount = regularSpawnCount;
        this.bossSpawnCount = bossSpawnCount;
    }

    @Override
    public void initGui() {
        this.startMillis = System.currentTimeMillis();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // deliberate: the world keeps moving while this is open
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (resolved) return;

        if (keyCode == Keyboard.KEY_ESCAPE) {
            resolve(false);
            return;
        }

        Character pressed = keyCodeToWasd(keyCode);
        if (pressed == null) return; // not a WASD key, ignore

        char expected = sequence[progressIndex].charAt(0);
        if (Character.toUpperCase(pressed) == Character.toUpperCase(expected)) {
            progressIndex++;
            slideOffset = 1.0F; // kick off the slide-left animation
            if (progressIndex >= sequence.length) {
                resolve(true);
            }
        } else {
            progressIndex = 0;
            flashUntilMillis = System.currentTimeMillis() + 180;
        }
    }

    private Character keyCodeToWasd(int keyCode) {
        if (keyCode == Keyboard.KEY_W) return 'W';
        if (keyCode == Keyboard.KEY_A) return 'A';
        if (keyCode == Keyboard.KEY_S) return 'S';
        if (keyCode == Keyboard.KEY_D) return 'D';
        return null;
    }

    @Override
    public void updateScreen() {
        if (resolved) return;
        if (System.currentTimeMillis() - startMillis >= totalMillis) {
            resolve(false);
        }
    }

    private void resolve(boolean success) {
        if (resolved) return;
        resolved = true;
        KeySequenceChallengeManager.notifyResolved();

        if (!success) {
            if (regularSpawnCount > 0) {
                java.util.List<String> regularPicks = MobSpawningConfig.pickRandom(regularSpawnCount, false);
                PacketHandler.sendToServer(new RequestMobSpawnPacket(regularPicks, false));
            }
            if (bossSpawnCount > 0) {
                java.util.List<String> bossPicks = MobSpawningConfig.pickRandom(bossSpawnCount, true);
                PacketHandler.sendToServer(new RequestMobSpawnPacket(bossPicks, true));
            }
        }

        this.mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Dim (not black out) the world behind, so it's still visibly happening
        drawRect(0, 0, screenWidth, screenHeight, 0x99000000);

        // A couple of faint horizontal static lines for the "corrupted" texture
        long now = System.currentTimeMillis();
        int staticY1 = (int) ((now / 3) % screenHeight);
        int staticY2 = (int) ((now / 5 + 400) % screenHeight);
        drawRect(0, staticY1, screenWidth, staticY1 + 1, 0x22FFFFFF);
        drawRect(0, staticY2, screenWidth, staticY2 + 1, 0x18FFFFFF);

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // Decay the slide animation based on real elapsed time since the last frame.
        long nowNanos = System.nanoTime();
        float deltaMs = lastRenderNanos == 0 ? 0 : (nowNanos - lastRenderNanos) / 1_000_000.0F;
        lastRenderNanos = nowNanos;
        slideOffset = Math.max(0.0F, slideOffset - deltaMs / SLIDE_ANIM_MS);

        boolean flashing = now < flashUntilMillis;

        int windowEnd = Math.min(sequence.length, progressIndex + VISIBLE_WINDOW);
        int visibleCount = windowEnd - progressIndex;

        int slotSpacing = SLOT_SIZE_NEXT + SLOT_GAP;
        int rowWidth = SLOT_SIZE_CURRENT + Math.max(0, visibleCount - 1) * slotSpacing;
        // The extra slideOffset*slotSpacing shifts the whole row right, then animates back down to 0 -
        // visually reads as the row sliding left into its settled position right after a hit.
        int x = centerX - rowWidth / 2 + (int) (slideOffset * slotSpacing);

        if (title != null && !title.isEmpty()) {
            int titleY = centerY - 60; // above the slot row
            int titleW = this.fontRendererObj.getStringWidth(title);
            this.fontRendererObj.drawStringWithShadow(title, centerX - titleW / 2, titleY, 0xFFFF2E6C);
        }

        for (int k = 0; k < visibleCount; k++) {
            int i = progressIndex + k;
            boolean isCurrent = k == 0;
            int size = isCurrent ? SLOT_SIZE_CURRENT : SLOT_SIZE_NEXT;
            int slotTop = centerY - size / 2;

            int mainColor = isCurrent ? (flashing ? 0xFFFFFFFF : 0xFFFF2E6C) : 0xFFFF2E6C;
            int ghostColor = 0x992EE6FF;

            drawBoxOutline(x - 2, slotTop - 2, x + size - 2, slotTop + size - 2, 2, ghostColor);
            drawBoxOutline(x, slotTop, x + size, slotTop + size, 2, mainColor);
            drawWasdLetter(sequence[i].charAt(0), x + size / 2, slotTop + size / 2, isCurrent ? 2.2F : 1.4F, mainColor);

            x += size + SLOT_GAP;
        }

        // Countdown bar
        long elapsed = now - startMillis;
        float remainingFrac = Math.max(0.0F, 1.0F - (elapsed / (float) totalMillis));
        int barWidth = 160;
        int barLeft = centerX - barWidth / 2;
        int barTop = centerY + 50;
        drawRect(barLeft, barTop, barLeft + barWidth, barTop + 6, 0xAA330000);
        drawRect(barLeft, barTop, barLeft + (int) (barWidth * remainingFrac), barTop + 6, 0xFFFF2E6C);

        if (subtitle != null && !subtitle.isEmpty()) {
            int subtitleY = barTop + 14; // below the countdown bar
            int subtitleW = this.fontRendererObj.getStringWidth(subtitle);
            this.fontRendererObj.drawStringWithShadow(subtitle, centerX - subtitleW / 2, subtitleY, 0x99FF2E6C);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void drawWasdLetter(char letter, int centerX, int centerY, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(centerX, centerY, 0);
        GL11.glScalef(scale, scale, 1.0F);
        String s = String.valueOf(letter);
        int w = this.fontRendererObj.getStringWidth(s);
        this.fontRendererObj.drawString(s, -w / 2, -4, color);
        GL11.glPopMatrix();
    }

    private void drawBoxOutline(int left, int top, int right, int bottom, int thickness, int color) {
        drawRect(left, top, right, top + thickness, color);
        drawRect(left, bottom - thickness, right, bottom, color);
        drawRect(left, top, left + thickness, bottom, color);
        drawRect(right - thickness, top, right, bottom, color);
    }
}
