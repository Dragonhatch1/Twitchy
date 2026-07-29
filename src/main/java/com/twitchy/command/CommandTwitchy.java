package com.twitchy.command;

import com.twitchy.api.TwitchModels.Reward;
import com.twitchy.api.TwitchModels.RewardRedemptionEvent;
import com.twitchy.client.TwitchSessionManager;
import com.twitchy.rewards.RewardConfig;
import com.twitchy.rewards.RewardManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandTwitchy extends CommandBase {

    @Override
    public String getCommandName() {
        return "twitchy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/twitchy <connect|reauth|disconnect|logout|status|say <msg>|testredeem <key>|reload>";
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
                    "Twitchy status: " + (hasToken ? "authorized as " + TwitchSessionManager.INSTANCE.credentials().userLogin
                        : "not authorized")
                        + ", redemptions " + (listening ? "LIVE" : "not listening"));
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
            case "reload" -> {
                RewardConfig.load();
                reply(sender, "Reloaded rewards.json.");
            }
            default -> reply(sender, "Unknown subcommand. " + getCommandUsage(sender));
        }
    }

    private void reply(ICommandSender sender, String text) {
        sender.addChatMessage(new ChatComponentText("[Twitchy] " + text));
    }
}
