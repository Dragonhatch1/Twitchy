package com.xyrth.twitchy.reference;

import com.xyrth.twitchy.event.GenericEvent;
import com.xyrth.twitchy.event.potions.PotionPrinter;
import com.xyrth.twitchy.event.spawn.entity.SpawnARSentry;
import com.xyrth.twitchy.event.spawn.entity.SpawnChicken;
import com.xyrth.twitchy.event.spawn.entity.SpawnCow;
import com.xyrth.twitchy.event.spawn.entity.SpawnEZDireWolf;
import com.xyrth.twitchy.event.spawn.entity.SpawnEZFallenKnight;
import com.xyrth.twitchy.event.spawn.entity.SpawnGolem;
import com.xyrth.twitchy.event.spawn.entity.SpawnPig;
import com.xyrth.twitchy.event.spawn.entity.SpawnSheep;
import com.xyrth.twitchy.event.spawn.entity.SpawnSkeleton;
import com.xyrth.twitchy.event.spawn.entity.SpawnSlime;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFFireBeetle;
import com.xyrth.twitchy.event.spawn.entity.SpawnZombie;

// This class provides a list of event types, with properties for their description and corresponding class
public enum TwitchEvent {

    UNKNOWN("Unknown Event", GenericEvent.class),
    COW("Spawn Cow", SpawnCow.class),
    CHICKEN("Spawn Chicken", SpawnChicken.class),
    GOLEM("Spawn Golem", SpawnGolem.class),
    PIG("Spawn Pig", SpawnPig.class),
    SHEEP("Spawn Sheep", SpawnSheep.class),
    SKELETON("Spawn Skeleton", SpawnSkeleton.class),
    SLIME("Spawn Slime", SpawnSlime.class),
    ZOMBIE("Spawn Zombie", SpawnZombie.class),
    SENTRY("Spawn AR Sentry", SpawnARSentry.class),
    DIREWOLF("Spawn EZ Dire Wolf", SpawnEZDireWolf.class),
    FALLENKNIGHT("Spawn EZ Fallen Knight", SpawnEZFallenKnight.class),
    POTIONPRINTER("Prints the Potions in the game", PotionPrinter.class),
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
