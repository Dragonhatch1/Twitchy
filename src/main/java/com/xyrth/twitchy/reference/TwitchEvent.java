package com.xyrth.twitchy.reference;

import com.xyrth.twitchy.event.EventMobRaid;
import com.xyrth.twitchy.event.EventRandomPotion;
import com.xyrth.twitchy.event.EventRandomSpawn;
import com.xyrth.twitchy.event.EventSubscribe;
import com.xyrth.twitchy.event.GenericEvent;
import com.xyrth.twitchy.event.spawn.entity.*;

// This class provides a list of event types, with properties for their description and corresponding class
public enum TwitchEvent {

    UNKNOWN("Unknown Event", GenericEvent.class),
    RANDOMSPAWN("Spawn Random Mob", EventRandomSpawn.class),
    RANDOMPOTION("Spawn Random Potion", EventRandomPotion.class),
    MOBRAID("Spawn Raid Mobs", EventMobRaid.class),
    SUBSTUFF("Sub Stuff", EventSubscribe.class),
    COW("Spawn Cow", SpawnCow.class),
    CHICKEN("Spawn Chicken", SpawnChicken.class),
    GOLEM("Spawn Golem", SpawnGolem.class),
    PIG("Spawn Pig", SpawnPig.class),
    SHEEP("Spawn Sheep", SpawnSheep.class),
    SKELETON("Spawn Skeleton", SpawnSkeleton.class),
    SLIME("Spawn Slime", SpawnSlime.class),
    ZOMBIE("Spawn Zombie", SpawnZombie.class),
    TFFIREBEETLE("Spawn TF Fire Beetle", SpawnTFFireBeetle.class);

    public final String eventAction;
    public final Class<? extends GenericEvent> genericEventClass;

    TwitchEvent(String eventAction, Class<? extends GenericEvent> genericEventClass) {
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
            Enum.valueOf(TwitchEvent.class, enumName);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
