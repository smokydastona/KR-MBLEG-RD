package com.kruemblegard.pressurelogic.network;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.blockentity.PressureConduitBlockEntity;

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

        // Mark conduit block entities in this chunk as needing validation.
        // IMPORTANT: during ChunkEvent.Load, avoid calling level.isLoaded/level.getBlockState here.
        // Those can block the chunk pipeline and stall integrated-server startup.
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        final ChunkPos chunkPos = chunk.getPos();
        for (var pos : chunk.getBlockEntitiesPos()) {
            if (chunk.getBlockEntity(pos) instanceof PressureConduitBlockEntity) {
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

        // Mark conduit block entities in this chunk as needing validation.
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        final ChunkPos chunkPos = chunk.getPos();
        for (var pos : chunk.getBlockEntitiesPos()) {
            if (chunk.getBlockEntity(pos) instanceof PressureConduitBlockEntity) {
                PressureNetworkManager.markDirty(serverLevel, pos);
            }
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
