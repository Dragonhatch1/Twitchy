package com.twitchy.client;

import java.util.List;

import com.twitchy.Config;
import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;
import com.twitchy.api.TwitchModels.RaidEvent;
import com.twitchy.entity.MobSpawningConfig;
import com.twitchy.network.PacketHandler;
import com.twitchy.network.RequestMobSpawnPacket;

/**
 * Client-side only. On an incoming raid: one regular mob per raider, plus one boss mob for every
 * 8 raiders (integer division - a raid of 24 gets exactly 3 boss, a raid of 30 also gets 3).
 */
public final class RaidManager {

    private static final int RAIDERS_PER_BOSS = 8;

    private RaidManager() {}

    public static void handleRaid(RaidEvent event) {
        if (Config.mobSpawnOnRaid) {
            int regularCount = event.viewer_count;
            int bossCount = event.viewer_count / RAIDERS_PER_BOSS;

            Twitchy.LOG.info(
                "Incoming raid from {} with {} viewers - spawning {} regular, {} boss.",
                event.from_broadcaster_user_name,
                event.viewer_count,
                regularCount,
                bossCount);

            if (regularCount > 0) {
                List<String> regularPicks = MobSpawningConfig.pickRandom(regularCount, false);
                PacketHandler.sendToServer(new RequestMobSpawnPacket(regularPicks, false));
            }
            if (bossCount > 0) {
                List<String> bossPicks = MobSpawningConfig.pickRandom(bossCount, true);
                PacketHandler.sendToServer(new RequestMobSpawnPacket(bossPicks, true));
            }
        }

        if (Config.autoShoutout) {
            TwitchApiClient.sendShoutout(TwitchSessionManager.INSTANCE.credentials(), event.from_broadcaster_user_id)
                .exceptionally(ex -> {
                    Twitchy.LOG
                        .warn("Failed to send shoutout for {}: {}", event.from_broadcaster_user_name, ex.getMessage());
                    return null;
                });
        }
    }

    /** Simulates an incoming raid locally for testing (/twitchy testraid), bypassing Twitch entirely. */
    public static void testTrigger(int viewerCount) {
        RaidEvent fake = new RaidEvent();
        fake.from_broadcaster_user_id = "test-raider-id";
        fake.from_broadcaster_user_login = "testraider";
        fake.from_broadcaster_user_name = "TestRaider";
        fake.viewer_count = viewerCount;
        handleRaid(fake);
    }
}
