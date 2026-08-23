package com.twitchy.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import com.twitchy.chat.ChatCommandConfig;
import com.twitchy.client.RaidManager;
import com.twitchy.client.TwitchSessionManager;
import com.twitchy.network.PacketHandler;
import com.twitchy.network.SetStorageTargetPacket;
import com.twitchy.rewards.RewardConfig;
import com.twitchy.rewards.RewardManager;

public class CommandTwitchy extends CommandBase {

    @Override
    public String getCommandName() {
        return "twitchy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/twitchy <connect|reauth|disconnect|logout|status|say <msg>|testredeem <key>|testchat <trigger>|testraid <count>|setstorage <x> <y> <z> [dim]|reload>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // client-only command, no server permission check needed
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            reply(sender, getCommandUsage(sender));
            return;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "connect" -> {
                reply(sender, "Opening your browser to authorize with Twitch...");
                TwitchSessionManager.INSTANCE.connect()
                    .thenRun(() -> reply(sender, "Connected to Twitch! Listening for channel point redemptions."))
                    .exceptionally(ex -> {
                        reply(sender, "Twitch connection failed: " + ex.getMessage());
                        return null;
                    });
            }
            case "reauth" -> {
                reply(sender, "Re-authorizing with Twitch...");
                TwitchSessionManager.INSTANCE.reauthorize()
                    .thenRun(() -> reply(sender, "Re-connected to Twitch!"))
                    .exceptionally(ex -> {
                        reply(sender, "Twitch re-authorization failed: " + ex.getMessage());
                        return null;
                    });
            }
            case "disconnect" -> {
                TwitchSessionManager.INSTANCE.disconnect();
                reply(sender, "Disconnected the EventSub session (token kept - use /twitchy connect to resume).");
            }
            case "logout" -> {
                TwitchSessionManager.INSTANCE.logout();
                reply(sender, "Logged out and cleared the stored Twitch token.");
            }
            case "status" -> {
                boolean hasToken = TwitchSessionManager.INSTANCE.hasStoredToken();
                boolean listening = TwitchSessionManager.INSTANCE.isEventSubReady();
                reply(
                    sender,
                    "Twitchy status: "
                        + (hasToken ? "authorized as " + TwitchSessionManager.INSTANCE.credentials().userLogin
                            : "not authorized")
                        + ", redemptions "
                        + (listening ? "LIVE" : "not listening"));
            }
            case "say" -> {
                if (args.length < 2) {
                    reply(sender, "Usage: /twitchy say <message>");
                    return;
                }
                String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                TwitchSessionManager.INSTANCE.sendChatMessage(message)
                    .thenRun(() -> reply(sender, "Sent to Twitch chat."))
                    .exceptionally(ex -> {
                        reply(sender, "Failed to send: " + ex.getMessage());
                        return null;
                    });
            }
            case "testredeem" -> {
                if (args.length < 2) {
                    reply(sender, "Usage: /twitchy testredeem <reward key>");
                    return;
                }
                String key = args[1];
                if (!RewardManager.testAction(key)) {
                    reply(sender, "No mapping found for reward key '" + key + "'. Check rewards.json.");
                    return;
                }
                reply(sender, "Simulated redemption of key '" + key + "'.");
            }
            case "testchat" -> {
                if (args.length < 2) {
                    reply(sender, "Usage: /twitchy testchat <trigger, e.g. !discord>");
                    return;
                }
                String trigger = args[1];
                if (!com.twitchy.chat.ChatCommandManager.testTrigger(trigger)) {
                    reply(sender, "No chat command found for trigger '" + trigger + "'. Check chatcommands.json.");
                    return;
                }
                reply(sender, "Simulated chat trigger '" + trigger + "'.");
            }
            case "setstorage" -> {
                if (args.length < 4) {
                    reply(sender, "Usage: /twitchy setstorage <x> <y> <z> [dimension]");
                    return;
                }
                try {
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);
                    int dimension = args.length >= 5 ? Integer.parseInt(args[4])
                        : (net.minecraft.client.Minecraft.getMinecraft().thePlayer != null
                            ? net.minecraft.client.Minecraft.getMinecraft().thePlayer.dimension
                            : 0);
                    PacketHandler.sendToServer(new SetStorageTargetPacket(x, y, z, dimension));
                } catch (NumberFormatException e) {
                    reply(sender, "x/y/z/dimension must be whole numbers.");
                }
            }
            case "testraid" -> {
                if (args.length < 2) {
                    reply(sender, "Usage: /twitchy testraid <viewer count>");
                    return;
                }
                int viewerCount;
                try {
                    viewerCount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    reply(sender, "'" + args[1] + "' isn't a valid number.");
                    return;
                }
                RaidManager.testTrigger(viewerCount);
                reply(sender, "Simulated a raid of " + viewerCount + " viewers.");
            }
            case "reload" -> {
                RewardConfig.load();
                ChatCommandConfig.load();
                reply(sender, "Reloaded rewards.json and chatcommands.json.");
            }
            default -> reply(sender, "Unknown subcommand. " + getCommandUsage(sender));
        }
    }

    private void reply(ICommandSender sender, String text) {
        sender.addChatMessage(new ChatComponentText("[Twitchy] " + text));
    }
}
