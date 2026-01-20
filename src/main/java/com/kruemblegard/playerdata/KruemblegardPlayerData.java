package com.kruemblegard.playerdata;

import com.kruemblegard.Kruemblegard;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;

/**
 * A small, codec-backed wrapper for persistent per-player data.
 *
 * <p>Forge doesn't have NeoForge-style attachments, so we store this under
 * player.getPersistentData()[kruemblegard].</p>
 */
public record KruemblegardPlayerData(boolean givenGuidebook, boolean encounteredTraprock, boolean visitedWayfall) {

    public static final String ROOT_KEY = Kruemblegard.MOD_ID;

    private static final String TAG_GIVEN_GUIDEBOOK = "given_guidebook";
    private static final String TAG_ENCOUNTERED_TRAPROCK = "encountered_traprock";
    private static final String TAG_VISITED_WAYFALL = "visited_wayfall";

    public static final Codec<KruemblegardPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf(TAG_GIVEN_GUIDEBOOK, false).forGetter(KruemblegardPlayerData::givenGuidebook),
            Codec.BOOL.optionalFieldOf(TAG_ENCOUNTERED_TRAPROCK, false).forGetter(KruemblegardPlayerData::encounteredTraprock),
            Codec.BOOL.optionalFieldOf(TAG_VISITED_WAYFALL, false).forGetter(KruemblegardPlayerData::visitedWayfall)
        ).apply(instance, KruemblegardPlayerData::new));

    public KruemblegardPlayerData(boolean givenGuidebook, boolean encounteredTraprock) {
        this(givenGuidebook, encounteredTraprock, false);
    }

    public static KruemblegardPlayerData read(CompoundTag playerPersistent) {
        if (playerPersistent == null) {
            return new KruemblegardPlayerData(false, false, false);
        }

        CompoundTag root = playerPersistent.getCompound(ROOT_KEY);
        return new KruemblegardPlayerData(
            root.getBoolean(TAG_GIVEN_GUIDEBOOK),
            root.getBoolean(TAG_ENCOUNTERED_TRAPROCK),
            root.getBoolean(TAG_VISITED_WAYFALL)
        );
    }

    public void write(CompoundTag playerPersistent) {
        if (playerPersistent == null) {
            return;
        }

        CompoundTag root = playerPersistent.getCompound(ROOT_KEY);
        root.putBoolean(TAG_GIVEN_GUIDEBOOK, this.givenGuidebook);
        root.putBoolean(TAG_ENCOUNTERED_TRAPROCK, this.encounteredTraprock);
        root.putBoolean(TAG_VISITED_WAYFALL, this.visitedWayfall);
        playerPersistent.put(ROOT_KEY, root);
    }

    public KruemblegardPlayerData withGivenGuidebook(boolean value) {
        return new KruemblegardPlayerData(value, this.encounteredTraprock, this.visitedWayfall);
    }

    public KruemblegardPlayerData withEncounteredTraprock(boolean value) {
        return new KruemblegardPlayerData(this.givenGuidebook, value, this.visitedWayfall);
    }

    public KruemblegardPlayerData withVisitedWayfall(boolean value) {
        return new KruemblegardPlayerData(this.givenGuidebook, this.encounteredTraprock, value);
    }
}
