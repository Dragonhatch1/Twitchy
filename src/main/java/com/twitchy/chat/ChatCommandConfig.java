package com.twitchy.chat;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.twitchy.Twitchy;

import cpw.mods.fml.common.Loader;

/** Chat trigger-phrase -> response mappings, loaded from config/twitchy/chatcommands.json. */
public class ChatCommandConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static final Type LIST_TYPE = new TypeToken<ArrayList<ChatCommand>>() {}.getType();

    private static volatile List<ChatCommand> commands = new ArrayList<>();

    private ChatCommandConfig() {}

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "chatcommands.json");
    }

    public static synchronized void load() {
        File f = file();
        if (!f.exists()) {
            commands = defaultCommands();
            save();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            List<ChatCommand> loaded = GSON.fromJson(reader, LIST_TYPE);
            commands = loaded != null ? loaded : new ArrayList<>();
            Twitchy.LOG.info("Loaded {} chat command(s) from chatcommands.json", commands.size());
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to load chatcommands.json, using empty command set.", e);
            commands = new ArrayList<>();
        }
    }

    public static synchronized void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(commands, LIST_TYPE, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save chatcommands.json", e);
        }
    }

    /** Matches the first whitespace-delimited word of a chat message, case-insensitively. */
    public static synchronized Optional<ChatCommand> findForMessage(String messageText) {
        if (messageText == null || messageText.isBlank()) return Optional.empty();
        String firstWord = messageText.trim()
            .split("\\s+", 2)[0];
        for (ChatCommand cmd : commands) {
            if (cmd.enabled && cmd.trigger != null && cmd.trigger.equalsIgnoreCase(firstWord)) {
                return Optional.of(cmd);
            }
        }
        return Optional.empty();
    }

    private static List<ChatCommand> defaultCommands() {
        List<ChatCommand> defaults = new ArrayList<>();

        ChatCommand commands = new ChatCommand();
        commands.trigger = "!help";
        commands.response = "discord | kills | setname | model";
        defaults.add(commands);

        ChatCommand discord = new ChatCommand();
        discord.trigger = "!discord";
        discord.response = "Join our Discord: https://discord.gg/your-invite-here";
        defaults.add(discord);

        return defaults;

    }
}
