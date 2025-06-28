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

    protected static void sendChatToSender(ICommandSender sender, String message) {
        if (sender instanceof EntityPlayerMP) ChatUtil.sendChatToPlayer(getCommandSenderAsPlayer(sender), message);
        else if (sender instanceof EntityPlayer) ChatUtil.sendChatToPlayer((EntityPlayer) sender, message);
        else if (sender instanceof CommandBlockLogic) sender.addChatMessage(new ChatComponentTranslation(message));
        else LogUtil.warn(message);
    }

    @Override
    public final int getRequiredPermissionLevel() {
        return this.isAdminOnly() ? 4 : 0;
    }

    protected abstract boolean isAdminOnly();
}
