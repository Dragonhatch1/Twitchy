package com.twitchy.network;

import java.util.Optional;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

import com.twitchy.Config;
import com.twitchy.Twitchy;
import com.twitchy.rewards.GravityFlipManager;
import com.twitchy.rewards.RewardAction;
import com.twitchy.rewards.RewardConfig;
import com.twitchy.rewards.ViewerLinkRegistry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
            Twitchy.LOG.warn("Received redemption for unconfigured reward key '{}', ignoring.", message.rewardKey);
            reportResult(sender, message, false);
            return;
        }
        RewardAction action = maybeAction.get();
        Twitchy.LOG.info(
            "Executing action {} for reward '{}' redeemed by {}",
            action.type,
            message.rewardKey,
            message.viewerDisplayName);

        boolean success = switch (action.type) {
            case GIVE_ITEM -> giveItem(server, action, message, sender);
            case RUN_COMMAND -> runCommand(server, action, message);
            case SPAWN_ENTITY -> spawnEntity(server, action, message, sender);
            case SERVER_CHAT_MESSAGE -> broadcastChat(server, action, message);
            case DEPOSIT_ITEM -> depositItem(server, action);
            case GRAVITY_FLIP -> gravityFlip(action, sender);
            case CLIENT_EFFECT -> true; // Should not normally arrive here - CLIENT_EFFECT is handled client-side
                                        // without a packet.
        };
        reportResult(sender, message, success);
    }

    private void reportResult(EntityPlayerMP sender, MessageRedeemAction message, boolean success) {
        if (sender == null || message.redemptionId == null || message.redemptionId.isBlank()) return;
        PacketHandler.sendTo(new MessageRedeemResult(message.redemptionId, message.twitchRewards, success), sender);
    }

    private EntityPlayerMP resolveTargetPlayer(MinecraftServer server, RewardAction action, MessageRedeemAction message,
        EntityPlayerMP sender) {
        if ("linked".equalsIgnoreCase(action.target)) {
            String linked = ViewerLinkRegistry.resolve(message.viewerLogin);
            if (linked != null && !linked.isBlank()) {
                for (Object obj : server.getConfigurationManager().playerEntityList) {
                    EntityPlayerMP candidate = (EntityPlayerMP) obj;
                    if (candidate.getCommandSenderName()
                        .equalsIgnoreCase(linked)) {
                        return candidate;
                    }
                }
                // Configured but not currently online - fall through to sender below.
            }
        }
        // Default "broadcaster" target: whichever client's own Twitch session sent this redemption.
        return sender;
    }

    private boolean giveItem(MinecraftServer server, RewardAction action, MessageRedeemAction message,
        EntityPlayerMP sender) {
        EntityPlayerMP player = resolveTargetPlayer(server, action, message, sender);
        if (player == null) {
            Twitchy.LOG.warn("GIVE_ITEM: target player not online, skipping.");
            return false;
        }
        Item item = resolveItem(action.item);
        if (item == null) {
            Twitchy.LOG.warn("GIVE_ITEM: unknown item id '{}'", action.item);
            return false;
        }
        ItemStack stack = new ItemStack(item, Math.max(1, action.amount), action.metadata);
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
        return true;
    }

    private boolean runCommand(MinecraftServer server, RewardAction action, MessageRedeemAction message) {
        if (action.command == null || action.command.isBlank()) return false;
        String command = action.command.replace("{viewer}", safe(message.viewerDisplayName))
            .replace("{input}", safe(message.userInput));
        ICommandSender sender = server; // MinecraftServer implements ICommandSender with full permission.
        server.getCommandManager()
            .executeCommand(sender, command);
        return true;
    }

    private boolean spawnEntity(MinecraftServer server, RewardAction action, MessageRedeemAction message,
        EntityPlayerMP sender) {
        if (action.entity == null || action.entity.isBlank()) return false;
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
                return false;
            }
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            world.spawnEntityInWorld(entity);
        }
        return true;
    }

    private boolean broadcastChat(MinecraftServer server, RewardAction action, MessageRedeemAction message) {
        if (action.message == null || action.message.isBlank()) return false;
        String text = action.message.replace("{viewer}", safe(message.viewerDisplayName))
            .replace("{input}", safe(message.userInput));
        server.getConfigurationManager()
            .sendChatMsg(new ChatComponentText(text));
        return true;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private boolean depositItem(MinecraftServer server, RewardAction action) {
        if (!Config.storageTargetSet) {
            Twitchy.LOG.warn("DEPOSIT_ITEM: no storage target configured. Use /twitchy setstorage <x> <y> <z>.");
            return false;
        }
        WorldServer world = server.worldServerForDimension(Config.storageDimension);
        TileEntity te = world.getTileEntity(Config.storageX, Config.storageY, Config.storageZ);
        if (!(te instanceof IInventory inventory)) {
            Twitchy.LOG.warn(
                "DEPOSIT_ITEM: no container found at ({}, {}, {}) in dimension {}",
                Config.storageX,
                Config.storageY,
                Config.storageZ,
                Config.storageDimension);
            return false;
        }
        Item item = resolveItem(action.item);
        if (item == null) {
            Twitchy.LOG.warn("DEPOSIT_ITEM: unknown item id '{}'", action.item);
            return false;
        }

        int leftover = insertIntoInventory(inventory, item, action.metadata, Math.max(1, action.amount));
        if (leftover > 0) {
            dropItemsNear(world, Config.storageX, Config.storageY, Config.storageZ, item, action.metadata, leftover);
        }
        inventory.markDirty();
        return true;
    }

    private int insertIntoInventory(IInventory inventory, Item item, int metadata, int amount) {
        // First pass: top up any existing matching stacks.
        for (int slot = 0; slot < inventory.getSizeInventory() && amount > 0; slot++) {
            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing != null && existing.getItem() == item && existing.getItemDamage() == metadata) {
                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0) {
                    int toAdd = Math.min(space, amount);
                    existing.stackSize += toAdd;
                    amount -= toAdd;
                }
            }
        }
        // Second pass: place any remainder into empty slots.
        for (int slot = 0; slot < inventory.getSizeInventory() && amount > 0; slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                int max = Math.min(new ItemStack(item).getMaxStackSize(), inventory.getInventoryStackLimit());
                int toAdd = Math.min(max, amount);
                inventory.setInventorySlotContents(slot, new ItemStack(item, toAdd, metadata));
                amount -= toAdd;
            }
        }
        return amount; // whatever's left didn't fit
    }

    private void dropItemsNear(WorldServer world, int x, int y, int z, Item item, int metadata, int amount) {
        while (amount > 0) {
            int stackSize = Math.min(amount, new ItemStack(item).getMaxStackSize());
            EntityItem entityItem = new EntityItem(
                world,
                x + 0.5,
                y + 1.1,
                z + 0.5,
                new ItemStack(item, stackSize, metadata));
            world.spawnEntityInWorld(entityItem);
            amount -= stackSize;
        }
    }

    private Item resolveItem(String itemIdentifier) {
        if (itemIdentifier == null || itemIdentifier.isBlank()) return null;
        try {
            int numericId = Integer.parseInt(itemIdentifier.trim());
            return Item.getItemById(numericId);
        } catch (NumberFormatException notNumeric) {
            // Not a plain number - treat it as a registry name instead, e.g. "minecraft:apple".
            return (Item) Item.itemRegistry.getObject(itemIdentifier);
        }
    }

    private boolean gravityFlip(RewardAction action, EntityPlayerMP sender) {
        if (sender == null) return false;
        GravityFlipManager.activate(sender, action.cameraFlipSeconds > 0 ? action.cameraFlipSeconds : 5);
        return true;
    }
}
