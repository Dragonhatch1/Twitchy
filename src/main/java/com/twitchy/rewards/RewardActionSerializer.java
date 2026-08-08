package com.twitchy.rewards;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Only writes the JSON fields actually relevant to a given RewardAction's type when SAVING, so
 * rewards.json doesn't get cluttered with default values for fields that type never reads (e.g.
 * a PLAY_SOUND entry doesn't need "amount"/"metadata"/"target" written out at all).
 *
 * Loading is unaffected by this - Gson's default reflective deserialization already leaves any
 * omitted field at its normal Java default, so no matching custom deserializer is needed here.
 */
public class RewardActionSerializer implements JsonSerializer<RewardAction> {

    @Override
    public JsonElement serialize(RewardAction action, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("type", action.type.name());

        switch (action.type) {
            case GIVE_ITEM -> {
                json.addProperty("item", action.item);
                json.addProperty("amount", action.amount);
                json.addProperty("metadata", action.metadata);
                json.addProperty("target", action.target);
            }
            case DEPOSIT_ITEM -> {
                json.addProperty("item", action.item);
                json.addProperty("amount", action.amount);
                json.addProperty("metadata", action.metadata);
            }
            case RUN_COMMAND -> json.addProperty("command", action.command);
            case SPAWN_ENTITY -> {
                json.addProperty("entity", action.entity);
                json.addProperty("count", action.count);
                json.addProperty("target", action.target);
            }
            case SERVER_CHAT_MESSAGE -> json.addProperty("message", action.message);
            case CLIENT_EFFECT -> {
                if (action.message != null && !action.message.isBlank()) json.addProperty("message", action.message);
                if (action.sound != null && !action.sound.isBlank()) {
                    json.addProperty("sound", action.sound);
                    json.addProperty("soundVolume", action.soundVolume);
                    json.addProperty("soundPitch", action.soundPitch);
                }
                if (action.cameraFlipSeconds > 0) json.addProperty("cameraFlipSeconds", action.cameraFlipSeconds);
            }
            case GRAVITY_FLIP -> json.addProperty("cameraFlipSeconds", action.cameraFlipSeconds);
            case INVENTORY_SCRAMBLE -> json.addProperty("target", action.target);
            case FOV_CHANGE -> json.addProperty("fovOffset", action.fovOffset);
            case PLAY_SOUND -> {
                json.addProperty("sound", action.sound);
                json.addProperty("soundVolume", action.soundVolume);
                json.addProperty("soundPitch", action.soundPitch);
            }
            case KEY_SEQUENCE_CHALLENGE -> {
                if (action.keySequence != null && action.keySequence.length > 0) {
                    com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
                    for (String key : action.keySequence) {
                        arr.add(new JsonPrimitive(key));
                    }
                    json.add("keySequence", arr);
                }
            }
        }

        // Toast fields apply to any type, but only worth writing if a toast is actually configured.
        if (action.toastTitle != null && !action.toastTitle.isBlank()) {
            json.addProperty("toastTitle", action.toastTitle);
            if (action.toastSubtitle != null && !action.toastSubtitle.isBlank()) {
                json.addProperty("toastSubtitle", action.toastSubtitle);
            }
            json.addProperty("toastType", action.toastType);
        }

        return json;
    }
}
