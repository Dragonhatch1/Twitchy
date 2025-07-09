package com.xyrth.twitchy.reference;

import com.xyrth.twitchy.event.GenericPotionEvent;
import com.xyrth.twitchy.event.potions.PlayerPotion;
import com.xyrth.twitchy.event.potions.PotionPrinter;

// This class provides a list of event types, with properties for their description and corresponding class
public enum PotionEvent {

    UNKNOWN("Unknown Event", GenericPotionEvent.class),
    POTIONPRINTER("Prints the Potions in the game", PotionPrinter.class),
    POTION("Applies Potion Effect to Player", PlayerPotion.class);

    public final String eventAction;
    public final Class<? extends GenericPotionEvent> genericEventClass;

    PotionEvent(String eventAction, Class<? extends GenericPotionEvent> genericEventClass) {
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
            Enum.valueOf(PotionEvent.class, enumName);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
