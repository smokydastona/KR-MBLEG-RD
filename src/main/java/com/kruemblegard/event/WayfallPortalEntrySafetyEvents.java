package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.world.WayfallSpawnPlatform;
import com.kruemblegard.world.WayfallWorkScheduler;
import com.kruemblegard.world.WayfallSpawnIslandSavedData;
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

            // Never do heavy Wayfall init work synchronously on the server thread.
            // Queue it and only complete the safety teleport once the island is actually placed.
            WayfallWorkScheduler.enqueueWayfallInit(level);

            final java.util.UUID playerId = player.getUUID();
            WayfallWorkScheduler.enqueue(level, (wayfall) -> {
                ServerPlayer p = wayfall.getServer().getPlayerList().getPlayer(playerId);
                if (p == null || !p.isAlive()) {
                    return true;
                }
                if (p.level() != wayfall || !wayfall.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
                    return true;
                }

                if (!WayfallSpawnIslandSavedData.get(wayfall).isPlaced()) {
                    return false;
                }

                BlockPos landing = WayfallSpawnPlatform.ensureSpawnLanding(wayfall);

                double x = landing.getX() + 0.5D;
                double y = landing.getY();
                double z = landing.getZ() + 0.5D;

                // If the teleporter put the player into a temporary holding state, release it now.
                p.setNoGravity(false);
                p.fallDistance = 0;
                p.setDeltaMovement(Vec3.ZERO);
                p.teleportTo(wayfall, x, y, z, p.getYRot(), p.getXRot());

                Kruemblegard.LOGGER.debug("Wayfall entry safety teleport: {} -> {}", p.getGameProfile().getName(), landing);
                return true;
            });
        });
    }
}
