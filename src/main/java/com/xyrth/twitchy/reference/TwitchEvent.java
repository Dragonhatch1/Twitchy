package com.xyrth.twitchy.reference;

import com.xyrth.twitchy.event.GenericSpawnEvent;
import com.xyrth.twitchy.event.spawn.entity.SpawnChicken;
import com.xyrth.twitchy.event.spawn.entity.SpawnCow;
import com.xyrth.twitchy.event.spawn.entity.SpawnEZDireWolf;
import com.xyrth.twitchy.event.spawn.entity.SpawnEZFallenKnight;
import com.xyrth.twitchy.event.spawn.entity.SpawnGolem;
import com.xyrth.twitchy.event.spawn.entity.SpawnPig;
import com.xyrth.twitchy.event.spawn.entity.SpawnSheep;
import com.xyrth.twitchy.event.spawn.entity.SpawnSkeleton;
import com.xyrth.twitchy.event.spawn.entity.SpawnSlime;
import com.xyrth.twitchy.event.spawn.entity.SpawnSpider;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFAdherent;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFArmoredGiant;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFBlockAndChainGoblin;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFBossKnightPhantom;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFBossMinotaur;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFBossSnowQueen;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFCarminiteBroodling;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFCarminiteGolem;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFCaveTroll;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFDeathTome;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFFireBeetle;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFForestBunny;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFForestRaven;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFForestSquirrel;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFHelmetCrab;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFKingSpider;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFKobold;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFLowerGoblinKnight;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFMazeSlime;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFMinoshroom;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFMosquitoSwarm;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFPenguin;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFPinchBeetle;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFRedcap;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFRedcapSapper;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFSkeletonDruid;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFSlimeBeetle;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFSnowGuardian;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFSwarmSpider;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFTowerwoodBorer;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFTwilightWraith;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFWinterWolf;
import com.xyrth.twitchy.event.spawn.entity.SpawnTFYeti;
import com.xyrth.twitchy.event.spawn.entity.SpawnZombie;

// This class provides a list of event types, with properties for their description and corresponding class
public enum TwitchEvent {

    UNKNOWN("Unknown Event", GenericSpawnEvent.class),
    COW("Spawn Cow", SpawnCow.class),
    CHICKEN("Spawn Chicken", SpawnChicken.class),
    GOLEM("Spawn Golem", SpawnGolem.class),
    PIG("Spawn Pig", SpawnPig.class),
    SPIDER("Spawn Spider", SpawnSpider.class),
    SHEEP("Spawn Sheep", SpawnSheep.class),
    SKELETON("Spawn Skeleton", SpawnSkeleton.class),
    SLIME("Spawn Slime", SpawnSlime.class),
    ZOMBIE("Spawn Zombie", SpawnZombie.class),
    DIREWOLF("Spawn EZ Dire Wolf", SpawnEZDireWolf.class),
    FALLENKNIGHT("Spawn EZ Fallen Knight", SpawnEZFallenKnight.class),
    TFFIREBEETLE("Spawn TF Fire Beetle", SpawnTFFireBeetle.class),
    TFADHERENT("Spawn TF Adherent", SpawnTFAdherent.class),
    TFARMOREDGIANT("Spawn TF Armored Giant", SpawnTFArmoredGiant.class),
    TFBLOCKANDCHAINGOBLIN("Spawn TF Block and Chain Goblin", SpawnTFBlockAndChainGoblin.class),
    TFBOSSKNIGHTPHANTOM("Spawn TF Boss Knight Phantom", SpawnTFBossKnightPhantom.class),
    TFBOSSMINOTAUR("Spawn TF Boss Minotaur", SpawnTFBossMinotaur.class),
    TFBOSSSNOWQUEEN("Spawn TF Boss Snow Queen", SpawnTFBossSnowQueen.class),
    TFCARMINITEBROODLING("Spawn TF Carminite Broodling", SpawnTFCarminiteBroodling.class),
    TFCARMINITEGOLEM("Spawn TF Carminite Golem", SpawnTFCarminiteGolem.class),
    TFCAVETROLL("Spawn TF Cave Troll", SpawnTFCaveTroll.class),
    TFDEATHTOME("Spawn TF Death Tome", SpawnTFDeathTome.class),
    TFFORESTBUNNY("Spawn TF Forest Bunny", SpawnTFForestBunny.class),
    TFFORESTSQUIRREL("Spawn TF Forest Squirrel", SpawnTFForestSquirrel.class),
    TFFORESTRAVEN("Spawn TF Forest Raven", SpawnTFForestRaven.class),
    TFHELMETCRAB("Spawn TF Helmet Crab", SpawnTFHelmetCrab.class),
    TFKINGSPIDER("Spawn TF King Spider", SpawnTFKingSpider.class),
    TFKOBOLD("Spawn TF Kobold", SpawnTFKobold.class),
    TFLOWERGOBLINKNIGHT("Spawn TF Lower Goblin Knight", SpawnTFLowerGoblinKnight.class),
    TFMAZESLIME("Spawn TF Maze Slime", SpawnTFMazeSlime.class),
    TFMINOSHROOM("Spawn TF Minoshroom", SpawnTFMinoshroom.class),
    TFMOSQUITOSWARM("Spawn TF Mosquito Swarm", SpawnTFMosquitoSwarm.class),
    TFPENGUIN("Spawn TF Penguin", SpawnTFPenguin.class),
    TFPINCHBEETLE("Spawn TF Pinch Beetle", SpawnTFPinchBeetle.class),
    TFREDCAP("Spawn TF Redcap", SpawnTFRedcap.class),
    TFREDCAPSAPPER("Spawn TF Redcap Sapper", SpawnTFRedcapSapper.class),
    TFSKELETONDRUID("Spawn TF Skeleton Druid", SpawnTFSkeletonDruid.class),
    TFSLIMEBEETLE("Spawn TF Slime Beetle", SpawnTFSlimeBeetle.class),
    TFSNOWGUARDIAN("Spawn TF Snow Guardian", SpawnTFSnowGuardian.class),
    TFSWARMSPIDER("Spawn TF Swarm Spider", SpawnTFSwarmSpider.class),
    TFTOWERWOODBORER("Spawn TF Towerwood Borer", SpawnTFTowerwoodBorer.class),
    TFTWILIGHTWRAITH("Spawn TF Twilight Wraith", SpawnTFTwilightWraith.class),
    TFWINTERWOLF("Spawn TF Winter Wolf", SpawnTFWinterWolf.class),
    TFYETI("Spawn TF Yeti", SpawnTFYeti.class);

    public final String eventAction;
    public final Class<? extends GenericSpawnEvent> genericEventClass;

    TwitchEvent(String eventAction, Class<? extends GenericSpawnEvent> genericEventClass) {
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
