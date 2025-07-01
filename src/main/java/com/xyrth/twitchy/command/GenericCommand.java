package com.xyrth.twitchy.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import com.xyrth.twitchy.util.ChatUtil;
import com.xyrth.twitchy.util.LogUtil;

public abstract class GenericCommand extends CommandBase {

    // There are a lot of instances where a command could be called,
    // so this function help us with just giving the player a chat message for each one
    protected static void sendChatToSender(ICommandSender sender, String message) {
        if (sender instanceof EntityPlayerMP) ChatUtil.sendChatToPlayer(getCommandSenderAsPlayer(sender), message);
        else if (sender instanceof EntityPlayer) ChatUtil.sendChatToPlayer((EntityPlayer) sender, message);
        else if (sender instanceof CommandBlockLogic) sender.addChatMessage(new ChatComponentTranslation(message));
        else LogUtil.warn(message);
    }

    // The permission level provided by op is set in server.properties under op-permission-level
    @Override
    public final int getRequiredPermissionLevel() {
        return this.isAdminOnly() ? 4 : 0;
    }

    protected abstract boolean isAdminOnly();
}
