package com.kruemblegard.world;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallWorkScheduler {
    private WayfallWorkScheduler() {}

    private static final Map<ServerLevel, Deque<WayfallTask>> QUEUES = new ConcurrentHashMap<>();

    public static void enqueue(ServerLevel level, WayfallTask task) {
        QUEUES.computeIfAbsent(level, ignored -> new ArrayDeque<>()).add(task);
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            QUEUES.remove(level);
        }
    }

    /**
     * Queue Wayfall init work in a budgeted way (spread across ticks).
     *
     * Key goals:
     * - Avoid forced FULL chunk loading in a single tick.
     * - Use region tickets to request chunk loading/generation gradually.
     */
    public static void enqueueWayfallInit(ServerLevel wayfall) {
        if (!wayfall.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // If already placed, there is nothing to do.
        if (WayfallSpawnIslandSavedData.get(wayfall).isPlaced()) {
            return;
        }

        BlockPos anchor = WayfallSpawnPlatform.getSpawnIslandAnchor();
        Vec3i templateSize = WayfallSpawnPlatform.getSpawnIslandTemplateSizeForPreload(wayfall);

        List<ChunkPos> chunks = computeTemplateChunkEnvelope(anchor, templateSize, WayfallSpawnPlatform.getChunkLoadPaddingBlocks());
        enqueue(wayfall, new TicketChunksTask(anchor, chunks, true));

        // Once chunks are loaded, do the actual placement (still on main thread, but now the heavy chunk
        // generation work has been spread across ticks).
        enqueue(wayfall, (level) -> {
            if (!areAllLoaded(level, chunks)) {
                return false;
            }

            WayfallSpawnPlatform.ensureSpawnIslandPlaced(level, true);
            return true;
        });

        // Cleanup: drop tickets after placement.
        enqueue(wayfall, new RemoveTicketsTask(anchor, chunks, true));
    }

    public static void enqueueOriginMonumentPlacement(ServerLevel wayfall) {
        if (!wayfall.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (WayfallOriginMonumentSavedData.get(wayfall).isPlaced()) {
            return;
        }

        BlockPos origin = WayfallOriginMonument.getOrigin();
        int radius = WayfallOriginMonument.getIslandRadius();
        List<ChunkPos> chunks = computeChunkSquare(origin, radius);

        enqueue(wayfall, new TicketChunksTask(origin, chunks, false));
        enqueue(wayfall, WayfallOriginMonument.createPlacementTask());
        enqueue(wayfall, new RemoveTicketsTask(origin, chunks, false));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        for (Map.Entry<ServerLevel, Deque<WayfallTask>> entry : QUEUES.entrySet()) {
            ServerLevel level = entry.getKey();
            Deque<WayfallTask> queue = entry.getValue();

            if (queue.isEmpty()) {
                continue;
            }

            int budget = Math.max(1, ModConfig.WAYFALL_INIT_TASKS_PER_TICK.get());
            while (budget-- > 0 && !queue.isEmpty()) {
                WayfallTask task = queue.peek();
                boolean done;
                try {
                    done = task.tick(level);
                } catch (Exception e) {
                    Kruemblegard.LOGGER.error("Wayfall work task failed; dropping task.", e);
                    done = true;
                }

                if (done) {
                    queue.poll();
                } else {
                    // Current task wants to retry later; stop for this tick.
                    break;
                }
            }
        }
    }

    @FunctionalInterface
    public interface WayfallTask {
        /** @return true when task is finished (remove from queue), false to retry next tick */
        boolean tick(ServerLevel wayfall);
    }

    private static List<ChunkPos> computeTemplateChunkEnvelope(BlockPos anchor, Vec3i size, int padBlocks) {
        int sizeX = Math.max(1, size.getX());
        int sizeZ = Math.max(1, size.getZ());

        int minBlockX = anchor.getX() - padBlocks;
        int maxBlockX = anchor.getX() + sizeX + padBlocks;
        int minBlockZ = anchor.getZ() - padBlocks;
        int maxBlockZ = anchor.getZ() + sizeZ + padBlocks;

        int minChunkX = minBlockX >> 4;
        int maxChunkX = maxBlockX >> 4;
        int minChunkZ = minBlockZ >> 4;
        int maxChunkZ = maxBlockZ >> 4;

        List<ChunkPos> result = new ArrayList<>((maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1));
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                result.add(new ChunkPos(cx, cz));
            }
        }
        return result;
    }

    private static boolean areAllLoaded(ServerLevel level, List<ChunkPos> chunks) {
        for (ChunkPos pos : chunks) {
            if (!level.getChunkSource().hasChunk(pos.x, pos.z)) {
                return false;
            }
        }
        return true;
    }

    private static List<ChunkPos> computeChunkSquare(BlockPos center, int radiusBlocks) {
        int minChunkX = (center.getX() - radiusBlocks) >> 4;
        int maxChunkX = (center.getX() + radiusBlocks) >> 4;
        int minChunkZ = (center.getZ() - radiusBlocks) >> 4;
        int maxChunkZ = (center.getZ() + radiusBlocks) >> 4;

        List<ChunkPos> result = new ArrayList<>((maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1));
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                result.add(new ChunkPos(cx, cz));
            }
        }
        return result;
    }

    private static final class TicketChunksTask implements WayfallTask {
        private final BlockPos ticketKey;
        private final List<ChunkPos> chunks;
        private final boolean requireAllLoaded;
        private int index;

        private TicketChunksTask(BlockPos ticketKey, List<ChunkPos> chunks, boolean requireAllLoaded) {
            this.ticketKey = ticketKey;
            this.chunks = chunks;
            this.requireAllLoaded = requireAllLoaded;
        }

        @Override
        public boolean tick(ServerLevel wayfall) {
            if (requireAllLoaded && areAllLoaded(wayfall, chunks)) {
                return true;
            }

            if (index >= chunks.size()) {
                return true;
            }

            // Ticket a few chunks per tick to spread generation/load cost.
            int perTick = Math.max(1, ModConfig.WAYFALL_INIT_CHUNKS_TICKETED_PER_TICK.get());
            for (int i = 0; i < perTick && index < chunks.size(); i++, index++) {
                ChunkPos pos = chunks.get(index);
                wayfall.getChunkSource().addRegionTicket(TicketType.PORTAL, pos, 1, ticketKey);
            }
            return index >= chunks.size();
        }
    }

    private static final class RemoveTicketsTask implements WayfallTask {
        private final BlockPos ticketKey;
        private final List<ChunkPos> chunks;
        private final boolean requireSpawnIslandPlaced;
        private int index;

        private RemoveTicketsTask(BlockPos ticketKey, List<ChunkPos> chunks, boolean requireSpawnIslandPlaced) {
            this.ticketKey = ticketKey;
            this.chunks = chunks;
            this.requireSpawnIslandPlaced = requireSpawnIslandPlaced;
        }

        @Override
        public boolean tick(ServerLevel wayfall) {
            // Only remove after placement is recorded.
            if (requireSpawnIslandPlaced) {
                if (!WayfallSpawnIslandSavedData.get(wayfall).isPlaced()) {
                    return false;
                }
            } else {
                if (!WayfallOriginMonumentSavedData.get(wayfall).isPlaced()) {
                    return false;
                }
            }

            if (index >= chunks.size()) {
                return true;
            }

            int perTick = Math.max(1, ModConfig.WAYFALL_INIT_CHUNKS_TICKETED_PER_TICK.get()) * 2;
            for (int i = 0; i < perTick && index < chunks.size(); i++, index++) {
                ChunkPos pos = chunks.get(index);
                wayfall.getChunkSource().removeRegionTicket(TicketType.PORTAL, pos, 1, ticketKey);
            }
            return index >= chunks.size();
        }
    }
}
