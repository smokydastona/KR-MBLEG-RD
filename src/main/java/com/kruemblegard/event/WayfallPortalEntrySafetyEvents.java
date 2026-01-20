package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.world.WayfallSpawnPlatform;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallPortalEntrySafetyEvents {
    private WayfallPortalEntrySafetyEvents() {}

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!event.getTo().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel)) {
            return;
        }

        // Mark Wayfall as visited the first time we actually enter the dimension.
        KruemblegardPlayerData data = KruemblegardPlayerData.read(player.getPersistentData());
        if (!data.visitedWayfall()) {
            data = data.withVisitedWayfall(true);
            data.write(player.getPersistentData());
        }

        // Post-change fail-safe: ensure the spawn island exists and the player is synced onto it.
        player.server.execute(() -> {
            if (!player.isAlive()) {
                return;
            }

            if (!(player.level() instanceof ServerLevel level) || !level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
                return;
            }

            BlockPos landing = WayfallSpawnPlatform.ensureSpawnLanding(level);

            double x = landing.getX() + 0.5D;
            double y = landing.getY();
            double z = landing.getZ() + 0.5D;

            player.fallDistance = 0;
            player.setDeltaMovement(Vec3.ZERO);
            player.teleportTo(level, x, y, z, player.getYRot(), player.getXRot());

            Kruemblegard.LOGGER.debug("Wayfall entry safety teleport: {} -> {}", player.getGameProfile().getName(), landing);
        });
    }
}
