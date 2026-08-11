package com.twitchy.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.client.Minecraft;

/**
 * Client-side dispatcher for the WASD key-sequence challenge GUI. requestStart() is safe to call
 * from any thread (redemptions arrive on the EventSub background thread) and queues challenges
 * rather than overwriting whatever's currently showing - if several redemptions come in close
 * together, they play out one after another instead of the latest one stomping the current one.
 * The actual GUI only ever opens/advances from tick(), which must only ever run on the client thread.
 */
public final class KeySequenceChallengeManager {

    private static final Queue<PendingChallenge> queue = new ConcurrentLinkedQueue<>();
    private static volatile boolean challengeActive = false;

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
        queue.add(new PendingChallenge(sequence, seconds, title, subtitle));
    }

    /**
     * Called by GuiDirectionalChallenge right before it closes, whether won, lost, or escaped -
     * signals the manager it's safe to open the next queued challenge, if any.
     */
    public static void notifyResolved() {
        challengeActive = false;
    }

    /** Call once per client tick, on the client thread. */
    public static void tick() {
        if (challengeActive) return; // a challenge is already showing - let it finish first

        PendingChallenge p = queue.poll();
        if (p == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return; // not actually in a world right now - drop this one silently
        challengeActive = true;
        mc.displayGuiScreen(new GuiDirectionalChallenge(p.sequence, p.seconds, p.title, p.subtitle));
    }
}
