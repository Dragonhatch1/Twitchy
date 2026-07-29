package com.twitchy.network;

import java.util.Optional;

import com.twitchy.Twitchy;
import com.twitchy.rewards.RewardAction;
import com.twitchy.rewards.RewardConfig;
import com.twitchy.rewards.ViewerLinkRegistry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

/** Runs server-side. Looks the reward up from the server's OWN rewards.json and executes it. */
public class MessageRedeemActionHandler implements IMessageHandler<MessageRedeemAction, IMessage> {

    @Override
    public IMessage onMessage(MessageRedeemAction message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        execute(MinecraftServer.getServer(), message, sender);
        return null;
    }

    private void execute(MinecraftServer server, MessageRedeemAction message, EntityPlayerMP sender) {
        Optional<RewardAction> maybeAction = RewardConfig.findByKey(message.rewardKey);
        if (maybeAction.isEmpty()) {
            Twitchy.LOG.warn(
                "Received redemption for unconfigured reward key '{}', ignoring.",
                message.rewardKey);
            return;
        }
        RewardAction action = maybeAction.get();
        Twitchy.LOG.info("Executing action {} for reward '{}' redeemed by {}", action.type, message.rewardKey, message.viewerDisplayName);

        switch (action.type) {
            case GIVE_ITEM -> giveItem(server, action, message, sender);
            case RUN_COMMAND -> runCommand(server, action, message);
            case SPAWN_ENTITY -> spawnEntity(server, action, message, sender);
            case SERVER_CHAT_MESSAGE -> broadcastChat(server, action, message);
            case CLIENT_EFFECT -> {
                // Should not normally arrive here - CLIENT_EFFECT is handled client-side without a packet.
            }
        }
    }

    private EntityPlayerMP resolveTargetPlayer(MinecraftServer server, RewardAction action, MessageRedeemAction message, EntityPlayerMP sender) {
        if ("linked".equalsIgnoreCase(action.target)) {
            String linked = ViewerLinkRegistry.resolve(message.viewerLogin);
            if (linked != null && !linked.isBlank()) {
                for (Object obj : server.getConfigurationManager().playerEntityList) {
                    EntityPlayerMP candidate = (EntityPlayerMP) obj;
                    if (candidate.getCommandSenderName().equalsIgnoreCase(linked)) {
                        return candidate;
                    }
                }
                // Configured but not currently online - fall through to sender below.
            }
        }
        // Default "broadcaster" target: whichever client's own Twitch session sent this redemption.
        return sender;
    }

    private void giveItem(MinecraftServer server, RewardAction action, MessageRedeemAction message, EntityPlayerMP sender) {
        EntityPlayerMP player = resolveTargetPlayer(server, action, message, sender);
        if (player == null) {
            Twitchy.LOG.warn("GIVE_ITEM: target player not online, skipping.");
            return;
        }
        Item item = (Item) Item.itemRegistry.getObject(action.item);
        if (item == null) {
            Twitchy.LOG.warn("GIVE_ITEM: unknown item id '{}'", action.item);
            return;
        }
        ItemStack stack = new ItemStack(item, Math.max(1, action.amount), action.metadata);
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
    }

    private void runCommand(MinecraftServer server, RewardAction action, MessageRedeemAction message) {
        if (action.command == null || action.command.isBlank()) return;
        String command = action.command.replace("{viewer}", safe(message.viewerDisplayName))
            .replace("{input}", safe(message.userInput));
        ICommandSender sender = server; // MinecraftServer implements ICommandSender with full permission.
        server.getCommandManager().executeCommand(sender, command);
    }

    private void spawnEntity(MinecraftServer server, RewardAction action, MessageRedeemAction message, EntityPlayerMP sender) {
        if (action.entity == null || action.entity.isBlank()) return;
        EntityPlayerMP player = resolveTargetPlayer(server, action, message, sender);
        WorldServer world = player != null ? (WorldServer) player.worldObj : server.worldServerForDimension(0);
        double x = player != null ? player.posX : world.getSpawnPoint().posX;
        double y = player != null ? player.posY : world.getSpawnPoint().posY;
        double z = player != null ? player.posZ : world.getSpawnPoint().posZ;

        int count = Math.max(1, action.count);
        for (int i = 0; i < count; i++) {
            Entity entity = EntityList.createEntityByName(action.entity, world);
            if (entity == null) {
                Twitchy.LOG.warn("SPAWN_ENTITY: unknown entity name '{}'", action.entity);
                return;
            }
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            world.spawnEntityInWorld(entity);
        }
    }

    private void broadcastChat(MinecraftServer server, RewardAction action, MessageRedeemAction message) {
        if (action.message == null || action.message.isBlank()) return;
        String text = action.message.replace("{viewer}", safe(message.viewerDisplayName))
            .replace("{input}", safe(message.userInput));
        server.getConfigurationManager().sendChatMsg(new ChatComponentText(text));
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
