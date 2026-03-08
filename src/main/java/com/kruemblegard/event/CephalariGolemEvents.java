package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;
import com.kruemblegard.entity.CephalariGolemEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.IronGolem;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * When a village with Cephalari spawns a vanilla iron golem, replace it with a Cephalari golem.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CephalariGolemEvents {
    private CephalariGolemEvents() {}

    private static final double SEARCH_RADIUS = 32.0D;

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

        // Don’t replace player-built golems.
        if (golem.isPlayerCreated()) {
            return;
        }

        // Only do this if Cephalari are actually present nearby.
        if (level.getEntitiesOfClass(CephalariEntity.class, golem.getBoundingBox().inflate(SEARCH_RADIUS)).isEmpty()) {
            return;
        }

        // Replace the vanilla golem spawn.
        event.setCanceled(true);

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

        level.addFreshEntity(cephalariGolem);
    }
}
