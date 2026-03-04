package com.kruemblegard.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trader Beetle: Wandering Trader mount variant of Scaralon.
 *
 * - Shares Scaralon Geo/animations.
 * - Has a despawn timer (like trader llamas).
 * - Spawns with default wandering-trader decor color.
 */
public class TraderBeetleEntity extends ScaralonBeetleEntity {

    private static final String NBT_DESPAWN_DELAY = "TraderDespawnDelay";
    private static final String NBT_OWNER_TRADER = "OwnerWanderingTrader";

    // Vanilla wandering trader uses 48,000 ticks (40 minutes) by default.
    public static final int DEFAULT_DESPAWN_DELAY_TICKS = 48000;

    // Trader beetles only use a small curated subset of Scaralon textures.
    private static final int[] TRADER_TEXTURE_VARIANTS = new int[] {1, 3, 6};

    private int despawnDelay = DEFAULT_DESPAWN_DELAY_TICKS;
    private @Nullable UUID ownerTraderId = null;

    public TraderBeetleEntity(EntityType<? extends net.minecraft.world.entity.animal.horse.AbstractChestedHorse> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        net.minecraft.world.DifficultyInstance difficulty,
        MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData,
        @Nullable CompoundTag dataTag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, dataTag);

        // Force the trader beetle texture subset.
        setTextureVariant(pickTraderTextureVariant());

        // Trader-style default decor (matches wandering trader vibe; user requested “default trader colors”).
        setCarpetColor(DyeColor.BLUE);

        // Trader mounts should be immediately rideable for the trader.
        setTamed(true);

        return data;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(NBT_DESPAWN_DELAY)) {
            despawnDelay = Mth.clamp(tag.getInt(NBT_DESPAWN_DELAY), 0, DEFAULT_DESPAWN_DELAY_TICKS);
        } else {
            despawnDelay = DEFAULT_DESPAWN_DELAY_TICKS;
        }

        ownerTraderId = tag.hasUUID(NBT_OWNER_TRADER) ? tag.getUUID(NBT_OWNER_TRADER) : null;

        // If someone edited NBT or the texture set changed, keep trader beetles on the allowed subset.
        if (!isAllowedTraderVariant(getTextureVariant())) {
            setTextureVariant(pickTraderTextureVariant());
        }
    }

    /**
     * Called for wandering-trader mounts: ensure the beetle has a chest and seed it with some
     * "wandering trader loot" based on the trader's current offers.
     */
    public void ensureTraderChestLoot(WanderingTrader trader) {
        if (level().isClientSide) {
            return;
        }

        if (trader == null) {
            return;
        }

        // Ensure chest exists.
        if (!hasChest()) {
            // Vanilla AbstractChestedHorse uses this flag to add chest slots.
            setChest(true);
            createInventory();
        }

        // Best-effort: fill a few chest slots with result items from this trader's offers.
        // This avoids needing a bespoke loot table while staying "wandering trader" themed.
        var offers = trader.getOffers();
        if (offers == null || offers.isEmpty()) {
            return;
        }

        // Collect candidate result stacks.
        List<ItemStack> candidates = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            ItemStack result = offers.get(i).getResult().copy();
            if (result.isEmpty() || result.is(Items.AIR)) {
                continue;
            }
            candidates.add(result);
        }

        if (candidates.isEmpty()) {
            return;
        }

        Collections.shuffle(candidates, new java.util.Random(random.nextLong()));
        int count = Mth.clamp(Mth.nextInt(random, 3, 6), 1, candidates.size());

        // AbstractHorse inventory layout: first 2 slots are equipment (saddle/armor). Chest storage starts after.
        // We'll only fill empty slots so we don't clobber player-added items.
        SimpleContainer inv = this.inventory;
        if (inv == null) {
            return;
        }

        int insertIndex = 0;
        for (int slot = 2; slot < inv.getContainerSize() && insertIndex < count; slot++) {
            if (!inv.getItem(slot).isEmpty()) {
                continue;
            }

            inv.setItem(slot, candidates.get(insertIndex));
            insertIndex++;
        }
    }

    private int pickTraderTextureVariant() {
        return TRADER_TEXTURE_VARIANTS[random.nextInt(TRADER_TEXTURE_VARIANTS.length)];
    }

    private static boolean isAllowedTraderVariant(int variant) {
        for (int allowed : TRADER_TEXTURE_VARIANTS) {
            if (variant == allowed) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (hasCustomName()) {
            return;
        }

        if (ownerTraderId != null) {
            net.minecraft.world.entity.Entity owner = ((net.minecraft.server.level.ServerLevel) level()).getEntity(ownerTraderId);
            if (!(owner instanceof WanderingTrader) || !owner.isAlive()) {
                discard();
                return;
            }
        }

        if (despawnDelay > 0) {
            despawnDelay--;
            if (despawnDelay <= 0) {
                discard();
            }
        }
    }

    public void setDespawnDelay(int ticks) {
        this.despawnDelay = Math.max(0, ticks);
    }

    public int getDespawnDelay() {
        return despawnDelay;
    }

    public void setOwnerTrader(@Nullable WanderingTrader trader) {
        this.ownerTraderId = trader == null ? null : trader.getUUID();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_DESPAWN_DELAY, despawnDelay);
        if (ownerTraderId != null) {
            tag.putUUID(NBT_OWNER_TRADER, ownerTraderId);
        }
    }

}
