package com.twitchy.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import cpw.mods.fml.common.Loader;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.util.ResourceLocation;

/** Client-side only. Holds the set of follower models actually available on this specific
 *  install - each entry is gated by an optional required mod ID, checked at registration time,
 *  so absent mods simply mean that entry never appears rather than crashing anything. Every
 *  entry here has been manually vetted (confirmed no-arg constructor, confirmed correct texture
 *  path, confirmed it renders through RenderBiped without issue) - this is deliberately a curated
 *  allowlist, not an automatic discovery dump. */
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

    /** Call once, client-side, during init. */
    public static void registerDefaults() {
        register("BIPED", null, ModelBiped::new,
            new ResourceLocation("textures/entity/steve.png")); // overridden by real skin resolution when set

        register("SPIDER", null, ModelSpider::new,
            new ResourceLocation("textures/entity/spider/spider.png"));

        // ===================== Add vetted modded entries below =====================
        // Each one requires: confirming a no-arg constructor exists on the model class,
        // finding the real texture path from that mod's own renderer source, and testing
        // it actually renders correctly before adding it here.
        //
        // registerReflective("ENDERZOO_DIREWOLF", "enderzoo",
        //     "com.mcreator.enderzoo.model.ModelDireWolf", "enderzoo", "textures/entity/direwolf.png");
    }

    private static void register(String key, String requiredModId, Supplier<ModelBase> modelSupplier,
                                 ResourceLocation texture) {
        if (requiredModId != null && !Loader.isModLoaded(requiredModId)) return;
        entries.put(key, new Entry(key, modelSupplier, texture));
    }

    private static void registerReflective(String key, String requiredModId, String className,
                                           String textureNamespace, String texturePath) {
        if (requiredModId != null && !Loader.isModLoaded(requiredModId)) return;

        try {
            Class<?> modelClass = Class.forName(className);
            Supplier<ModelBase> supplier = () -> {
                try {
                    return (ModelBase) modelClass.getDeclaredConstructor().newInstance();
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
