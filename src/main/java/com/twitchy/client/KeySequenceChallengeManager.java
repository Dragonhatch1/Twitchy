package com.twitchy.client;

import net.minecraft.client.Minecraft;

/**
 * Client-side dispatcher for the WASD key-sequence challenge GUI. requestStart() is safe to call
 * from any thread (redemptions arrive on the EventSub background thread); the actual GUI only
 * ever opens from tick(), which must only ever run on the client thread.
 */
public final class KeySequenceChallengeManager {

    private static volatile PendingChallenge pending;

    private KeySequenceChallengeManager() {}

    private static class PendingChallenge {

        final String[] sequence;
        final int seconds;
        final String title;
        final String subtitle;

        PendingChallenge(String[] sequence, int seconds, String title, String subtitle) {
            this.sequence = sequence;
            this.seconds = seconds;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    public static void requestStart(String[] sequence, int seconds, String title, String subtitle) {
        pending = new PendingChallenge(sequence, seconds, title, subtitle);
    }

    /** Call once per client tick, on the client thread. */
    public static void tick() {
        PendingChallenge p = pending;
        if (p == null) return;
        pending = null;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return; // not actually in a world right now
        mc.displayGuiScreen(new GuiDirectionalChallenge(p.sequence, p.seconds, p.title, p.subtitle));
    }
}
