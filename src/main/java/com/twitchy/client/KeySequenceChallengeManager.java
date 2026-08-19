package com.twitchy.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.client.Minecraft;

public final class KeySequenceChallengeManager {

    private static final Queue<PendingChallenge> queue = new ConcurrentLinkedQueue<>();
    private static volatile boolean challengeActive = false;

    private KeySequenceChallengeManager() {}

    private static class PendingChallenge {

        final String[] sequence;
        final int seconds;
        final String title;
        final String subtitle;
        final int regularSpawnCount;
        final int bossSpawnCount;

        PendingChallenge(String[] sequence, int seconds, String title, String subtitle,
                         int regularSpawnCount, int bossSpawnCount) {
            this.sequence = sequence;
            this.seconds = seconds;
            this.title = title;
            this.subtitle = subtitle;
            this.regularSpawnCount = regularSpawnCount;
            this.bossSpawnCount = bossSpawnCount;
        }
    }

    public static void requestStart(String[] sequence, int seconds, String title, String subtitle,
                                    int regularSpawnCount, int bossSpawnCount) {
        queue.add(new PendingChallenge(sequence, seconds, title, subtitle, regularSpawnCount, bossSpawnCount));
    }

    public static void notifyResolved() {
        challengeActive = false;
    }

    /** Call once per client tick, on the client thread. */
    public static void tick() {
        if (challengeActive) return;

        PendingChallenge p = queue.poll();
        if (p == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        challengeActive = true;
        mc.displayGuiScreen(
            new GuiDirectionalChallenge(p.sequence, p.seconds, p.title, p.subtitle, p.regularSpawnCount, p.bossSpawnCount));
    }
}
