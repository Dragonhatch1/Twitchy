package com.xyrth.twitchy.command;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.xyrth.twitchy.reference.PotionEvent;
import com.xyrth.twitchy.reference.TwitchEvent;
import com.xyrth.twitchy.util.LogUtil;

public class EventCommand extends GenericCommand {

    @Override
    public String getCommandName() {
        return "twitchy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Run a twitch event manually. Available are: " + Arrays.toString(TwitchEvent.values())
            + Arrays.toString(PotionEvent.values());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        // EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        EntityPlayerMP player = MinecraftServer.getServer()
            .getConfigurationManager()
            .func_152612_a("Developer");
        World world = player.worldObj;

        // Args is an array of strings split by the command input on spaces, and sliced after the command name
        if (args.length == 6) {
            // Check if the provided event type exists
            if (TwitchEvent.isValidEnum(args[0].toUpperCase())) {
                // Get the enum object of our event type
                TwitchEvent event = TwitchEvent.valueOf(args[0].toUpperCase());

                try {
                    // Call the constructor of the instance of the generic class of TwitchEvent.genericEventClass
                    // Player is EntityPlayerMP while the constructor wants EntityLivingBase,
                    // but since PlayerMp is an instance of EntityLivingBase, it gets cast implicitly
                    // Generally, this just starts a new event of the provided type
                    event.genericEventClass
                        .getDeclaredConstructor(
                            World.class,
                            double.class,
                            double.class,
                            double.class,
                            EntityLivingBase.class,
                            int.class,
                            int.class,
                            double.class,
                            String.class,
                            int.class)
                        .newInstance(
                            world,
                            player.posX,
                            player.posY,
                            player.posZ,
                            player,
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2]),
                            Double.parseDouble(args[3]),
                            args[4],
                            Integer.parseInt(args[5]));
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
        } else if (args.length == 4) {

            if (PotionEvent.isValidEnum(args[0].toUpperCase())) {

                PotionEvent event = PotionEvent.valueOf(args[0].toUpperCase());

                try {
                    event.genericEventClass
                        .getDeclaredConstructor(World.class, int.class, int.class, int.class, EntityLivingBase.class)
                        .newInstance(
                            world,
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3]),
                            player);
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

    // We only want ops to be able to run this command
    @Override
    protected boolean isAdminOnly() {
        return true;
    }
}
