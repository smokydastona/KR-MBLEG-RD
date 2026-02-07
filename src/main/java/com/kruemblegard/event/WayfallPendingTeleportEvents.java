package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.WayfallSpawnIslandSavedData;
import com.kruemblegard.world.WayfallTravel;
import com.kruemblegard.world.WayfallWorkScheduler;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Completes deferred Wayfall portal teleports once the spawn island is initialized.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallPendingTeleportEvents {
    private WayfallPendingTeleportEvents() {}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        ServerLevel wayfall = server.getLevel(ModWorldgenKeys.Levels.WAYFALL);
        if (wayfall == null) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player == null || !player.isAlive()) {
                continue;
            }

            // If they're already in Wayfall, nothing to do.
            if (player.level() instanceof ServerLevel level && level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
                continue;
            }

            if (!player.getPersistentData().getBoolean(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING)) {
                continue;
            }

            String pendingFromDim = player.getPersistentData().getString(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_FROM_DIM);
            if (player.level() instanceof ServerLevel current && !pendingFromDim.isEmpty()) {
                if (!current.dimension().location().toString().equals(pendingFromDim)) {
                    player.getPersistentData().remove(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING);
                    player.getPersistentData().remove(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_NEXT_TICK);
                    player.getPersistentData().remove(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_FROM_DIM);
                    continue;
                }
            }

            int nextTick = player.getPersistentData().getInt(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_NEXT_TICK);
            if (player.tickCount < nextTick) {
                continue;
            }

            // Keep init queued while we're waiting.
            WayfallWorkScheduler.enqueueWayfallInit(wayfall);

            if (!WayfallSpawnIslandSavedData.get(wayfall).isPlaced()) {
                player.getPersistentData().putInt(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_NEXT_TICK, player.tickCount + 10);
                continue;
            }

            // Ready: clear pending tag and perform the actual dimension change.
            player.getPersistentData().remove(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING);
            player.getPersistentData().remove(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_NEXT_TICK);
            player.getPersistentData().remove(WayfallTravel.TAG_WAYFALL_PORTAL_ENTRY_PENDING_FROM_DIM);

            if (player.level() instanceof ServerLevel from) {
                WayfallTravel.teleportToWayfallSpawnLanding(player, from);
            }
        }
    }
}
