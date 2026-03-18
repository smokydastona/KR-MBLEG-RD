package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;
import com.kruemblegard.entity.CephalariGolemEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * When a village with Cephalari spawns a vanilla iron golem, replace it with a Cephalari golem.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CephalariGolemEvents {
    private CephalariGolemEvents() {}

    private static final double SEARCH_RADIUS = 32.0D;
    private static final String NBT_REPLACE_GOLEM = "KruemblegardReplaceIronGolemWithCephalariGolem";

    /**
     * Mark village-spawned iron golems for replacement.
     *
     * We intentionally only target {@link MobSpawnType#MOB_SUMMONED} so:
     * - player-built iron golems are never affected (they set {@code playerCreated=true})
     * - spawn eggs / commands / custom spawners are not restricted
     */
    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof IronGolem golem)) {
            return;
        }

        // Only mark vanilla iron golems.
        if (event.getEntity().getType() != EntityType.IRON_GOLEM) {
            return;
        }

        // Only mark villager-spawned golems.
        if (event.getSpawnType() != MobSpawnType.MOB_SUMMONED) {
            return;
        }

        // Extra safety: never interfere with player-built iron golems.
        if (golem.isPlayerCreated()) {
            return;
        }

        golem.getPersistentData().putBoolean(NBT_REPLACE_GOLEM, true);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!(event.getEntity() instanceof IronGolem golem)) {
            return;
        }

        // Critical: Cephalari golems extend IronGolem, so we must only handle *vanilla* iron golems.
        // Otherwise, spawning the replacement golem would recursively trigger this event and spawn thousands.
        if (event.getEntity().getType() != EntityType.IRON_GOLEM) {
            return;
        }

        // Only replace golems that were explicitly marked as villager-spawned.
        if (!golem.getPersistentData().getBoolean(NBT_REPLACE_GOLEM)) {
            return;
        }

        // Clear the marker immediately to avoid any weird re-entrancy.
        golem.getPersistentData().remove(NBT_REPLACE_GOLEM);

        // Don’t replace player-built golems.
        if (golem.isPlayerCreated()) {
            return;
        }

        // Only do this if Cephalari are actually present nearby.
        if (level.getEntitiesOfClass(CephalariEntity.class, golem.getBoundingBox().inflate(SEARCH_RADIUS)).isEmpty()) {
            return;
        }

        // Villages only count vanilla iron golems when deciding whether to spawn another.
        // Treat existing Cephalari golems as equivalent by blocking vanilla golem spawns when one is already nearby.
        // This prevents villages from repeatedly trying to spawn golems even though a Cephalari golem is present.
        if (!level.getEntitiesOfClass(CephalariGolemEntity.class, golem.getBoundingBox().inflate(SEARCH_RADIUS)).isEmpty()) {
            event.setCanceled(true);
            return;
        }

        try {
            CephalariGolemEntity cephalariGolem = ModEntities.CEPHALARI_GOLEM.get().create(level);
            if (cephalariGolem == null) {
                return;
            }

            cephalariGolem.moveTo(golem.getX(), golem.getY(), golem.getZ(), golem.getYRot(), golem.getXRot());
            cephalariGolem.setHealth(golem.getHealth());
            cephalariGolem.setCustomName(golem.getCustomName());
            cephalariGolem.setCustomNameVisible(golem.isCustomNameVisible());
            cephalariGolem.setPersistenceRequired();
            cephalariGolem.setPlayerCreated(false);

            if (!level.addFreshEntity(cephalariGolem)) {
                return;
            }

            // Replace the vanilla golem spawn.
            event.setCanceled(true);
        } catch (Throwable ignored) {
            // Avoid crashing the game if something goes wrong during replacement.
        }
    }
}
