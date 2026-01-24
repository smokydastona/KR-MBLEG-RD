package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.WayfallWorkScheduler;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallSpawnIslandLoadEvents {
    private WayfallSpawnIslandLoadEvents() {}

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // Requirement: place the origin island at (0, 175, 0) on first load of the Wayfall dimension.
        WayfallWorkScheduler.enqueueWayfallInit(level);
    }
}
