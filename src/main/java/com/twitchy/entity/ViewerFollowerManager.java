package com.twitchy.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.twitchy.network.RedeemActionHandler;
import com.twitchy.rewards.RewardAction;

public class ViewerFollowerManager {

    private static final Map<String, EntityViewerFollower> activeFollowers = new HashMap<>();

    public static void reconcile(EntityPlayerMP target, List<String> userIds, List<String> userLogins,
        List<List<RewardAction.GearPiece>> gearPerUser, List<String> minecraftUsernames, List<Float> scales) {
        Set<String> currentIds = new HashSet<>(userIds);

        for (int i = 0; i < userIds.size(); i++) {
            String id = userIds.get(i);

            if (activeFollowers.containsKey(id)) continue;

            spawnFollower(target, id, userLogins.get(i), gearPerUser.get(i), minecraftUsernames.get(i), scales.get(i));
        }

        activeFollowers.entrySet()
            .removeIf(entry -> {
                if (currentIds.contains(entry.getKey())) return false;
                entry.getValue()
                    .setDead();
                return true;
            });
    }

    public static void despawnAll() {
        for (EntityViewerFollower follower : activeFollowers.values()) {
            follower.setDead();
        }
        activeFollowers.clear();
    }

    public static void despawnForPlayer(EntityPlayerMP player) {
        activeFollowers.entrySet()
            .removeIf(entry -> {
                if (entry.getValue()
                    .getTargetPlayer() != player) return false;
                entry.getValue()
                    .setDead();
                return true;
            });
    }

    public static void spawnFollower(EntityPlayerMP target, String userId, String userLogin,
        List<RewardAction.GearPiece> gear, String minecraftUsername, float scale) {
        EntityViewerFollower follower = new EntityViewerFollower(target.worldObj);
        follower.setPosition(target.posX, target.posY, target.posZ);
        follower.initFollow(target, userId, userLogin, minecraftUsername, scale);
        RedeemActionHandler resolver = new RedeemActionHandler();
        for (RewardAction.GearPiece piece : gear) {
            Item item = resolver.resolveItem(piece.item);
            if (item != null) {
                follower.setCurrentItemOrArmor(piece.slot, new ItemStack(item, 1, piece.metadata));
            }
        }
        target.worldObj.spawnEntityInWorld(follower);
        activeFollowers.put(userId, follower);
    }

    public static void removeFromTracking(String userId) {
        activeFollowers.remove(userId);
    }

    public static void applyGear(String userId, List<RewardAction.GearPiece> pieces) {
        EntityViewerFollower follower = activeFollowers.get(userId);
        if (follower == null) return; // not currently active - the saved record alone is enough, applied next time they
        // spawn
        RedeemActionHandler resolver = new RedeemActionHandler();
        for (RewardAction.GearPiece piece : pieces) {
            Item item = resolver.resolveItem(piece.item);
            if (item == null) continue;
            follower.setCurrentItemOrArmor(piece.slot, new ItemStack(item, 1, piece.metadata));
        }
    }

    public static void applyScale(String userId, float scale) {
        EntityViewerFollower follower = activeFollowers.get(userId);
        if (follower != null) follower.setFollowerScale(scale);
    }
}
