package com.twitchy.client;

import java.util.ArrayList;
import java.util.List;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchModels;
import com.twitchy.network.PacketHandler;
import com.twitchy.network.SyncViewerListPacket;

public class ViewerFollowerClientPoller {

    private static final long POLL_INTERVAL_MS = 60 * 1000L;
    private long lastPollMillis = 0;
    private volatile boolean pollInFlight = false;

    /** Call once per client tick, on the client thread. */
    public void tick() {
        if (pollInFlight || !TwitchSessionManager.INSTANCE.isEventSubReady()) return;

        long now = System.currentTimeMillis();
        if (now - lastPollMillis < POLL_INTERVAL_MS) return;
        lastPollMillis = now;

        pollInFlight = true;
        TwitchApiClient.getChatters(TwitchSessionManager.INSTANCE.credentials())
            .thenAccept(response -> {
                String selfId = TwitchSessionManager.INSTANCE.credentials().userId;
                List<TwitchModels.Chatter> filtered = new ArrayList<>();
                for (TwitchModels.Chatter c : response.data) {
                    if (!c.user_id.equals(selfId)) filtered.add(c);
                }
                PacketHandler.sendToServer(new SyncViewerListPacket(filtered));
            })
            .exceptionally(ex -> {
                Twitchy.LOG.error("Failed to poll chatters for viewer followers", ex);
                return null;
            })
            .whenComplete((r, e) -> pollInFlight = false);
    }

    /**
     * Defensive reset so a poll that was mid-flight when the world unloaded (and whose
     * whenComplete callback may never fire, since the future keeps chasing an HTTP call from a
     * session that no longer exists) can never permanently wedge future polling. Call this
     * whenever the Twitch session tears down.
     */
    public void reset() {
        pollInFlight = false;
        lastPollMillis = 0;
    }
}
