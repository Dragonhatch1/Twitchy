package com.twitchy.rewards;

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

/**
 * Reward-to-action mappings, loaded from config/twitchy/rewards.json. Each mapping has a stable
 * local "key" shared identically across every streamer - Twitchy creates the actual Twitch reward
 * per-streamer (see RewardIdRegistry) and translates incoming redemptions back to this key before
 * the server ever sees them.
 *
 * IMPORTANT for dedicated servers: this file must exist (and match) on BOTH the client that owns
 * the Twitch session AND the server that executes the actions, since the server looks up the
 * action definition itself from its own copy rather than trusting a payload from the client.
 */
public class RewardConfig {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(RewardAction.class, new RewardActionSerializer())
        .create();

    private static final Type LIST_TYPE = new TypeToken<ArrayList<RewardMapping>>() {}.getType();

    private static volatile List<RewardMapping> mappings = new ArrayList<>();

    private RewardConfig() {}

    private static File file() {
        File dir = new File(
            Loader.instance()
                .getConfigDir(),
            "twitchy");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "rewards.json");
    }

    public static synchronized void load() {
        File f = file();
        if (!f.exists()) {
            mappings = defaultMappings();
            save();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            List<RewardMapping> loaded = GSON.fromJson(reader, LIST_TYPE);
            mappings = loaded != null ? loaded : new ArrayList<>();
            Twitchy.LOG.info("Loaded {} reward mapping(s) from rewards.json", mappings.size());
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to load rewards.json, using empty mapping set.", e);
            mappings = new ArrayList<>();
        }
    }

    public static synchronized void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(mappings, LIST_TYPE, writer);
        } catch (IOException e) {
            Twitchy.LOG.error("Failed to save rewards.json", e);
        }
    }

    public static synchronized List<RewardMapping> all() {
        return new ArrayList<>(mappings);
    }

    public static synchronized Optional<RewardAction> findByKey(String key) {
        for (RewardMapping mapping : mappings) {
            if (mapping.key != null && mapping.key.equals(key) && mapping.enabled) {
                return Optional.ofNullable(mapping.action);
            }
        }
        return Optional.empty();
    }

    private static List<RewardMapping> defaultMappings() {
        List<RewardMapping> defaults = new ArrayList<>();

        RewardMapping gamba = new RewardMapping();
        gamba.key = "Gamba";
        gamba.title = "Gamba!";
        gamba.cost = 100;
        gamba.prompt = "Spawn Gamba Tokens in the Chest!";
        gamba.enabled = false;
        gamba.action = new RewardAction();
        gamba.action.type = RewardActionType.DEPOSIT_ITEM;
        gamba.action.item = "7440";
        gamba.action.amount = 64;
        gamba.action.metadata = 32233;
        gamba.action.toastTitle = "{viewer} redeemed Gamba!";
        gamba.action.toastType = 3;
        defaults.add(gamba);

        RewardMapping camera180 = new RewardMapping();
        camera180.key = "camera_180";
        camera180.title = "Upside-Down Camera";
        camera180.cost = 100;
        camera180.prompt = "180s my camera for 10 seconds!";
        camera180.enabled = false;
        camera180.action = new RewardAction();
        camera180.action.type = RewardActionType.CLIENT_EFFECT;
        camera180.action.cameraFlipSeconds = 10;
        camera180.action.toastTitle = "{viewer} redeemed Upside-Down Camera";
        camera180.action.toastType = 3;
        defaults.add(camera180);

        RewardMapping reverseGravity = new RewardMapping();
        reverseGravity.key = "reverse_gravity";
        reverseGravity.title = "Reverse Gravity";
        reverseGravity.cost = 100;
        reverseGravity.prompt = "Reverse Gravity for 5 seconds";
        reverseGravity.enabled = false;
        reverseGravity.action = new RewardAction();
        reverseGravity.action.type = RewardActionType.GRAVITY_FLIP;
        reverseGravity.action.cameraFlipSeconds = 5;
        reverseGravity.action.toastTitle = "{viewer} redeemed Reverse Gravity";
        reverseGravity.action.toastType = 3;
        defaults.add(reverseGravity);

        RewardMapping zombie = new RewardMapping();
        zombie.key = "spawn_zombie";
        zombie.title = "Spawn a Zombie";
        zombie.cost = 300;
        zombie.prompt = "Spawn a zombie near the streamer!";
        zombie.enabled = false;
        zombie.action = new RewardAction();
        zombie.action.type = RewardActionType.SPAWN_ENTITY;
        zombie.action.entity = "Zombie";
        zombie.action.count = 1;
        defaults.add(zombie);

        RewardMapping weather = new RewardMapping();
        weather.key = "make_it_rain";
        weather.title = "Make It Rain";
        weather.cost = 200;
        weather.prompt = "Change the weather to rain!";
        weather.enabled = false;
        weather.action = new RewardAction();
        weather.action.type = RewardActionType.RUN_COMMAND;
        weather.action.command = "weather rain 1200";
        defaults.add(weather);

        RewardMapping shout = new RewardMapping();
        shout.key = "shoutout";
        shout.title = "Shoutout!";
        shout.cost = 200;
        shout.prompt = "Say something on Stream!";
        shout.enabled = false;
        shout.requiresUserInput = true;
        shout.action = new RewardAction();
        shout.action.type = RewardActionType.SERVER_CHAT_MESSAGE;
        shout.action.toastTitle = "{viewer} says:";
        shout.action.toastSubtitle = "{input}";
        shout.action.toastType = 2;
        defaults.add(shout);

        RewardMapping fovUp = new RewardMapping();
        fovUp.key = "fov_up";
        fovUp.title = "Widen FoV";
        fovUp.cost = 150;
        fovUp.prompt = "Widen my FoV!";
        fovUp.enabled = false;
        fovUp.action = new RewardAction();
        fovUp.action.type = RewardActionType.FOV_CHANGE;
        fovUp.action.fovOffset = 1.0F;
        fovUp.action.toastTitle = "{viewer} widened the FoV";
        fovUp.action.toastType = 3;
        defaults.add(fovUp);

        RewardMapping fovDown = new RewardMapping();
        fovDown.key = "fov_down";
        fovDown.title = "Shrink FoV";
        fovDown.cost = 150;
        fovDown.prompt = "Shrink my FoV!";
        fovDown.enabled = false;
        fovDown.action = new RewardAction();
        fovDown.action.type = RewardActionType.FOV_CHANGE;
        fovDown.action.fovOffset = -1.0F;
        fovDown.action.toastTitle = "{viewer} shrunk the FoV";
        fovDown.action.toastType = 3;
        defaults.add(fovDown);

        RewardMapping hotPotato = new RewardMapping();
        hotPotato.key = "hotPotato";
        hotPotato.enabled = false;
        hotPotato.title = "Hot Potato";
        hotPotato.cost = 500;
        hotPotato.prompt = "Toss an Unstable Ingot my way!";
        hotPotato.action = new RewardAction();
        hotPotato.action.type = RewardActionType.GIVE_ITEM;
        hotPotato.action.item = "minecraft:apple";
        hotPotato.action.amount = 1;
        hotPotato.action.toastTitle = "HOT POTATO";
        hotPotato.action.toastSubtitle = "{viewer} gave you an Unstable Ingot!";
        defaults.add(hotPotato);

        RewardMapping inventoryScramble = new RewardMapping();
        inventoryScramble.key = "inventory_scramble";
        inventoryScramble.enabled = false;
        inventoryScramble.title = "Scramble My Inventory";
        inventoryScramble.cost = 250;
        inventoryScramble.prompt = "Randomly shuffle my hotbar and inventory!";
        inventoryScramble.action = new RewardAction();
        inventoryScramble.action.type = RewardActionType.INVENTORY_SCRAMBLE;
        inventoryScramble.action.toastTitle = "{viewer} scrambled the inventory";
        inventoryScramble.action.toastType = 3;
        defaults.add(inventoryScramble);

        RewardMapping powerfail = new RewardMapping();
        powerfail.key = "powerfail";
        powerfail.enabled = false;
        powerfail.title = "Powerfail!";
        powerfail.cost = 75;
        powerfail.prompt = "Play the Powerfail sound effect!";
        powerfail.action = new RewardAction();
        powerfail.action.type = RewardActionType.PLAY_SOUND;
        powerfail.action.sound = "ic2:machines:InterruptOne";
        powerfail.action.soundVolume = 0.2F;
        powerfail.action.soundPitch = 1.0F;
        defaults.add(powerfail);

        RewardMapping explosion = new RewardMapping();
        explosion.key = "explosion";
        explosion.enabled = false;
        explosion.title = "Explosion!";
        explosion.cost = 75;
        explosion.prompt = "Play the TnT Explosion sound effect!";
        explosion.action = new RewardAction();
        explosion.action.type = RewardActionType.PLAY_SOUND;
        explosion.action.sound = "random.explode";
        explosion.action.soundVolume = 0.2F;
        explosion.action.soundPitch = 1.0F;
        defaults.add(explosion);

        RewardMapping heylisten = new RewardMapping();
        heylisten.key = "heylisten";
        heylisten.enabled = false;
        heylisten.title = "Hey! Listen!";
        heylisten.cost = 75;
        heylisten.prompt = "Play the Hey! Listen! sound effect from Legend of Zelda!";
        heylisten.action = new RewardAction();
        heylisten.action.type = RewardActionType.PLAY_SOUND;
        heylisten.action.sound = "twitchy:heylisten";
        heylisten.action.soundVolume = 0.2F;
        heylisten.action.soundPitch = 1.0F;
        defaults.add(heylisten);

        return defaults;
    }
}
