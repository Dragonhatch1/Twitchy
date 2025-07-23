package com.xyrth.twitchy.reference;

import com.xyrth.twitchy.event.GenericNpcEvent;
import com.xyrth.twitchy.event.spawn.npc.SpawnRedeemNpc;
import com.xyrth.twitchy.event.spawn.npc.SpawnSubNpc;

// This class provides a list of event types, with properties for their description and corresponding class
public enum CNpcEvent {

    UNKNOWN("Unknown Event", GenericNpcEvent.class),
    SUBNPC("Spawns Custom Sub NPC", SpawnSubNpc.class),
    REDEEMNPC("Spawns Custom Redeemed NPC", SpawnRedeemNpc.class);

    public final String eventAction;
    public final Class<? extends GenericNpcEvent> genericEventClass;

    CNpcEvent(String eventAction, Class<? extends GenericNpcEvent> genericEventClass) {
        this.eventAction = eventAction;
        this.genericEventClass = genericEventClass;
    }

    /**
     * Checks if the specified name is a valid enum for the class.
     *
     * @param enumName the enum name, null returns false
     * @return true if the enum name is valid, otherwise false
     */
    public static <E extends Enum<E>> boolean isValidEnum(final String enumName) {
        if (enumName == null) {
            return false;
        }
        try {
            Enum.valueOf(CNpcEvent.class, enumName);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
