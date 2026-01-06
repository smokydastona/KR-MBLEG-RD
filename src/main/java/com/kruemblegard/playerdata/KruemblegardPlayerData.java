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
public record KruemblegardPlayerData(boolean givenCrumblingCodex) {

    public static final String ROOT_KEY = Kruemblegard.MOD_ID;

    private static final String TAG_GIVEN_CODEX = "given_crumbling_codex";

    public static final Codec<KruemblegardPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf(TAG_GIVEN_CODEX, false).forGetter(KruemblegardPlayerData::givenCrumblingCodex)
    ).apply(instance, KruemblegardPlayerData::new));

    public static KruemblegardPlayerData read(CompoundTag playerPersistent) {
        if (playerPersistent == null) {
            return new KruemblegardPlayerData(false);
        }

        CompoundTag root = playerPersistent.getCompound(ROOT_KEY);
        return new KruemblegardPlayerData(root.getBoolean(TAG_GIVEN_CODEX));
    }

    public void write(CompoundTag playerPersistent) {
        if (playerPersistent == null) {
            return;
        }

        CompoundTag root = playerPersistent.getCompound(ROOT_KEY);
        root.putBoolean(TAG_GIVEN_CODEX, this.givenCrumblingCodex);
        playerPersistent.put(ROOT_KEY, root);
    }

    public KruemblegardPlayerData withGivenCrumblingCodex(boolean value) {
        return new KruemblegardPlayerData(value);
    }
}
