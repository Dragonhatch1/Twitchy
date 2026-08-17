package com.twitchy.client;

import java.util.ArrayList;
import java.util.List;

import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchModels;
import com.twitchy.network.MessageSyncViewerList;
import com.twitchy.network.PacketHandler;

public class ViewerFollowerClientPoller {

    private static final long POLL_INTERVAL_MS = 60 * 1000L;
    private long lastPollMillis = 0;
    private boolean pollInFlight = false;

    /** Call once per client tick, on the client thread. */
    public void tick() {
        if (pollInFlight || !TwitchSessionManager.INSTANCE.hasStoredToken()) return;

        long now = System.currentTimeMillis();
        if (now - lastPollMillis < POLL_INTERVAL_MS) return;
        lastPollMillis = now;

        pollInFlight = true;
        TwitchApiClient.getChatters(TwitchSessionManager.INSTANCE.credentials())
            .thenAccept(response -> {
                String selfId = TwitchSessionManager.INSTANCE.credentials().userId;
                List<TwitchModels.Chatter> filtered = new ArrayList<>();
                for (TwitchModels.Chatter c : response.data) {
                    if (!c.user_id.equals(selfId)) filtered.add(c); // filter out the broadcaster here, client-side
                }
                PacketHandler.sendToServer(new MessageSyncViewerList(filtered));
            })
            .exceptionally(ex -> {
                Twitchy.LOG.error("Failed to poll chatters for viewer followers", ex);
                return null;
            })
            .whenComplete((r, e) -> pollInFlight = false);
    }
}
