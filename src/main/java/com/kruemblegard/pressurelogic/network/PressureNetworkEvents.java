package com.kruemblegard.pressurelogic.network;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.PressureConduitBlock;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge event hooks for the pressure network manager.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PressureNetworkEvents {
    private PressureNetworkEvents() {}

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            PressureNetworkManager.onLevelUnload(level);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // The event doesn't supply a level; tick all loaded server levels.
        if (event.getServer() == null) {
            return;
        }

        for (ServerLevel level : event.getServer().getAllLevels()) {
            PressureNetworkManager.onServerTick(level);
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Mark all conduit block entities in the chunk as needing validation.
        var chunk = event.getChunk();
        for (var pos : chunk.getBlockEntitiesPos()) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (level.getBlockState(pos).getBlock() instanceof PressureConduitBlock) {
                PressureNetworkManager.markDirty(serverLevel, pos);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Mark all conduit block entities in the chunk as needing validation.
        var chunk = event.getChunk();
        for (var pos : chunk.getBlockEntitiesPos()) {
            PressureNetworkManager.markDirty(serverLevel, pos);
        }
    }
}
