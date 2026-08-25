package com.twitchy.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.Loader;

public final class FollowerModelRegistry {

    public static class Entry {

        public final String key;
        public final Supplier<ModelBase> modelSupplier;
        public final ResourceLocation texture;

        public Entry(String key, Supplier<ModelBase> modelSupplier, ResourceLocation texture) {
            this.key = key;
            this.modelSupplier = modelSupplier;
            this.texture = texture;
        }
    }

    private static final Map<String, Entry> entries = new LinkedHashMap<>();

    private FollowerModelRegistry() {}

    public static void registerDefaults() {
        register("STEVE", null, ModelBiped::new, new ResourceLocation("textures/entity/steve.png"));

        register("SPIDER", null, ModelSpider::new, new ResourceLocation("textures/entity/spider/spider.png"));

        register("ZOMBIE", null, ModelZombie::new, new ResourceLocation("textures/entity/zombie/zombie.png"));

        register("ENDERMAN", null, ModelEnderman::new, new ResourceLocation("textures/entity/enderman/enderman.png"));

        register("CREEPER", null, ModelCreeper::new, new ResourceLocation("textures/entity/creeper/creeper.png"));

        register(
            "VILLAGER",
            null,
            () -> new ModelVillager(0.0F),
            new ResourceLocation("textures/entity/villager/villager.png"));

        register("WITCH", null, () -> new ModelWitch(0.0F), new ResourceLocation("textures/entity/witch.png"));

        register("BLAZE", null, ModelBlaze::new, new ResourceLocation("textures/entity/blaze.png"));

        register("COW", null, ModelCow::new, new ResourceLocation("textures/entity/cow/cow.png"));

        register("PIG", null, ModelPig::new, new ResourceLocation("textures/entity/pig/pig.png"));

        register("CHICKEN", null, ModelChicken::new, new ResourceLocation("textures/entity/chicken.png"));

        // ===================== Add vetted modded entries below =====================
        // Each one requires: confirming a no-arg constructor exists on the model class,
        // finding the real texture path from that mod's own renderer source, and testing
        // it actually renders correctly before adding it here.
        //
        // registerReflective("ENDERZOO_DIREWOLF", "enderzoo",
        // "com.mcreator.enderzoo.model.ModelDireWolf", "enderzoo", "textures/entity/direwolf.png");

        registerReflective(
            "BOAR",
            "TwilightForest",
            "twilightforest.client.model.ModelTFBoar",
            "twilightforest",
            "textures/model/wildboar.png");

        registerReflective(
            "BUNNY",
            "TwilightForest",
            "twilightforest.client.model.ModelTFBunny",
            "twilightforest",
            "textures/model/bunnybrown.png");

        registerReflective(
            "FIRE_BEETLE",
            "TwilightForest",
            "twilightforest.client.model.ModelTFFireBeetle",
            "twilightforest",
            "textures/model/firebeetle.png");

        registerReflective(
            "HELMET_CRAB",
            "TwilightForest",
            "twilightforest.client.model.ModelTFHelmetCrab",
            "twilightforest",
            "textures/model/helmetcrab.png");

        registerReflective(
            "ICE_CRYSTAL",
            "TwilightForest",
            "twilightforest.client.model.ModelTFIceCrystal",
            "twilightforest",
            "textures/model/icecrystal.png");

        registerReflective(
            "KOBOLD",
            "TwilightForest",
            "twilightforest.client.model.ModelTFKobold",
            "twilightforest",
            "textures/model/kobold.png");

        registerReflective(
            "LOYAL_ZOMBIE",
            "TwilightForest",
            "twilightforest.client.model.ModelTFLoyalZombie",
            "minecraft",
            "textures/entity/zombie/zombie.png");

        registerReflective(
            "MINOSHROOM",
            "TwilightForest",
            "twilightforest.client.model.ModelTFMinoshroom",
            "twilightforest",
            "textures/model/minoshroomtaur.png");

        registerReflective(
            "PENGUIN",
            "TwilightForest",
            "twilightforest.client.model.ModelTFPenguin",
            "twilightforest",
            "textures/model/penguin.png");

        registerReflective(
            "REDCAP",
            "TwilightForest",
            "twilightforest.client.model.ModelTFRedcap",
            "twilightforest",
            "textures/model/redcap.png");

        registerReflective(
            "SKELETON_DRUID",
            "TwilightForest",
            "twilightforest.client.model.ModelTFSkeletonDruid",
            "twilightforest",
            "textures/model/skeletondruid.png");

        registerReflective(
            "SLIME_BEETLE",
            "TwilightForest",
            "twilightforest.client.model.ModelTFSlimeBeetle",
            "twilightforest",
            "textures/model/slimebeetle.png");

        registerReflective(
            "SQUIRREL",
            "TwilightForest",
            "twilightforest.client.model.ModelTFSquirrel",
            "twilightforest",
            "textures/model/squirrel2.png");

        registerReflective(
            "SWARM_SPIDER",
            "TwilightForest",
            "twilightforest.client.model.ModelTFSwarmSpider",
            "twilightforest",
            "textures/model/swarmspider.png");
    }

    private static void register(String key, String requiredModId, Supplier<ModelBase> modelSupplier,
        ResourceLocation texture) {
        if (requiredModId != null && !Loader.isModLoaded(requiredModId)) return;
        entries.put(key, new Entry(key, modelSupplier, texture));
    }

    private static void registerReflective(String key, String requiredModId, String className, String textureNamespace,
        String texturePath) {
        if (requiredModId != null && !Loader.isModLoaded(requiredModId)) return;

        try {
            Class<?> modelClass = Class.forName(className);
            Supplier<ModelBase> supplier = () -> {
                try {
                    return (ModelBase) modelClass.getDeclaredConstructor()
                        .newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            entries.put(key, new Entry(key, supplier, new ResourceLocation(textureNamespace, texturePath)));
        } catch (Exception e) {
            com.twitchy.Twitchy.LOG.warn("Failed to register follower model '{}': {}", key, e.getMessage());
        }
    }

    public static Entry get(String key) {
        return entries.get(key);
    }

    public static boolean isAvailable(String key) {
        return entries.containsKey(key);
    }

    public static Iterable<String> availableKeys() {
        return entries.keySet();
    }
}
