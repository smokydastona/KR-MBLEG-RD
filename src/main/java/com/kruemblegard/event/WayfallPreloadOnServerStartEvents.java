package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.world.WayfallSpawnIslandSavedData;
import com.kruemblegard.world.WayfallWorkScheduler;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * One-time Wayfall preload/initialization trigger.
 *
 * User intent: preload the Wayfall spawn area once when the world/server starts (pre-player),
 * then return to normal player-driven chunk loading.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallPreloadOnServerStartEvents {
    private WayfallPreloadOnServerStartEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel wayfall = server.getLevel(ModWorldgenKeys.Levels.WAYFALL);
        if (wayfall == null) {
            return;
        }

        if (WayfallSpawnIslandSavedData.get(wayfall).isPlaced()) {
            return;
        }

        if (ModConfig.WAYFALL_DEBUG_LOGGING.get()) {
            Kruemblegard.LOGGER.info("Wayfall: server start -> queue one-time preload/init");
        }

        WayfallWorkScheduler.enqueueWayfallInit(wayfall);
    }
}
