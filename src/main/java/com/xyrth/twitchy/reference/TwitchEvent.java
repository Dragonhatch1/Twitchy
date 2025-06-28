package com.xyrth.twitchy.reference;

//

import com.xyrth.twitchy.event.EventMobRaid;
import com.xyrth.twitchy.event.EventRandomPotion;
import com.xyrth.twitchy.event.EventRandomSpawn;
import com.xyrth.twitchy.event.EventSpawnTest;
import com.xyrth.twitchy.event.EventSubscribe;
import com.xyrth.twitchy.event.GenericEvent;

public enum TwitchEvent {

    UNKNOWN("Uknown Event", GenericEvent.class),
    RANDOMSPAWN("Spawn Random Mob", EventRandomSpawn.class),
    RANDOMPOTION("Spawn Random Potion", EventRandomPotion.class),
    MOBRAID("Spawn Raid Mobs", EventMobRaid.class),
    SUBSTUFF("Sub Stuff", EventSubscribe.class),
    SPAWNINGTEST("Test Entity Spawn", EventSpawnTest.class);

    public final String eventAction;
    public final Class<? extends GenericEvent> genericEventClass;

    TwitchEvent(String eventAction, Class<? extends GenericEvent> genericEventClass) {
        this.eventAction = eventAction;
        this.genericEventClass = genericEventClass;
    };

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
            Enum.valueOf(TwitchEvent.class, enumName);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
