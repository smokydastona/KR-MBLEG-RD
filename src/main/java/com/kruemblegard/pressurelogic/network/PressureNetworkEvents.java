package com.kruemblegard.pressurelogic.network;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.PressureConduitBlock;

import java.util.concurrent.TimeUnit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

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

    private static final long SLOW_CHUNK_HOOK_LOG_THRESHOLD_NANOS = TimeUnit.MILLISECONDS.toNanos(100);

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
        final long startNanos = System.nanoTime();

        Level level = (Level) event.getLevel();
        if (level.isClientSide) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Mark all conduit block entities in the chunk as needing validation.
        var chunk = event.getChunk();

        final ChunkPos chunkPos = (chunk instanceof LevelChunk levelChunk) ? levelChunk.getPos() : null;
        for (var pos : chunk.getBlockEntitiesPos()) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (level.getBlockState(pos).getBlock() instanceof PressureConduitBlock) {
                PressureNetworkManager.markDirty(serverLevel, pos);
            }
        }

        logIfSlow("chunk load", startNanos, serverLevel, chunkPos);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        final long startNanos = System.nanoTime();

        Level level = (Level) event.getLevel();
        if (level.isClientSide) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Mark all conduit block entities in the chunk as needing validation.
        var chunk = event.getChunk();

        final ChunkPos chunkPos = (chunk instanceof LevelChunk levelChunk) ? levelChunk.getPos() : null;
        for (var pos : chunk.getBlockEntitiesPos()) {
            PressureNetworkManager.markDirty(serverLevel, pos);
        }

        logIfSlow("chunk unload", startNanos, serverLevel, chunkPos);
    }

    private static void logIfSlow(String action, long startNanos, ServerLevel level, ChunkPos chunkPos) {
        long elapsedNanos = System.nanoTime() - startNanos;
        if (elapsedNanos < SLOW_CHUNK_HOOK_LOG_THRESHOLD_NANOS) {
            return;
        }

        double elapsedMs = elapsedNanos / 1_000_000.0;
        String chunk = chunkPos == null ? "<unknown>" : (chunkPos.x + "," + chunkPos.z);
        Kruemblegard.LOGGER.warn(
                "PressureNetworkEvents {} slow: {} ms (dim={}, chunk={})",
                action,
                String.format("%.2f", elapsedMs),
                level.dimension().location(),
                chunk
        );
    }
}
