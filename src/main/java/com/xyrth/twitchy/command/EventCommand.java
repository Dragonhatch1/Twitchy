package com.xyrth.twitchy.command;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.xyrth.twitchy.reference.TwitchEvent;
import com.xyrth.twitchy.util.LogUtil;

public class EventCommand extends GenericCommand {

    @Override
    public String getCommandName() {
        return "twitchy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Run a twitch event manually. Available are: " + Arrays.toString(TwitchEvent.values());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        World world = player.worldObj;

        if (args.length == 1) {
            if (TwitchEvent.isValidEnum(args[0].toUpperCase())) {
                TwitchEvent event = TwitchEvent.valueOf(args[0].toUpperCase());

                try {
                    // Call the constructor of the instance of the generic class of TwitchEvent.genericEventClass
                    // Player is EntityPlayerMP while the constructor wants EntityLivingBase,
                    // but since PlayerMp is an instance of EntityLivingBase, it gets cast implicitly
                    event.genericEventClass
                        .getDeclaredConstructor(
                            World.class,
                            double.class,
                            double.class,
                            double.class,
                            EntityLivingBase.class)
                        .newInstance(world, player.posX, player.posY, player.posZ, player);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                    sendChatToSender(sender, EnumChatFormatting.RED + "Failed to trigger event.");
                    LogUtil.error(e);
                    LogUtil.error(e.getMessage());
                }
            } else {
                sendChatToSender(
                    sender,
                    String.format(EnumChatFormatting.YELLOW + "Event '%s' does not exist.", args[0]));
            }
        } else {
            sendChatToSender(sender, EnumChatFormatting.YELLOW + "Missing an event type.");
        }
    }

    @Override
    protected boolean isAdminOnly() {
        return true;
    }
}
