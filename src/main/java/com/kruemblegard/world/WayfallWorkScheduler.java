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
     * - Prefer player-driven chunk loading like vanilla dimensions.
     * - Allow a ONE-TIME preload on first generation/first visit to avoid long "waiting for chunks" stalls.
     */
    public static void enqueueWayfallInit(ServerLevel wayfall) {
        if (!wayfall.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        Deque<WayfallTask> existingQueue = QUEUES.get(wayfall);
        if (existingQueue != null && !existingQueue.isEmpty()) {
            return;
        }

            WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);

        // If already placed, there is nothing to do.
            if (data.isPlaced()) {
            return;
        }

        BlockPos anchor = WayfallSpawnPlatform.getSpawnIslandAnchor();
        Vec3i templateSize = WayfallSpawnPlatform.getSpawnIslandTemplateSizeForPreload(wayfall);

        List<ChunkPos> chunks = computeTemplateChunkEnvelope(anchor, templateSize, WayfallSpawnPlatform.getChunkLoadPaddingBlocks());

        if (ModConfig.WAYFALL_DEBUG_LOGGING.get()) {
            Kruemblegard.LOGGER.info(
                "Wayfall init queued: anchor={} templateSize={}x{}x{} chunks={} tasksPerTick={} preloadDone={}",
                anchor,
                templateSize.getX(),
                templateSize.getY(),
                templateSize.getZ(),
                chunks.size(),
                ModConfig.WAYFALL_INIT_TASKS_PER_TICK.get(),
                data.isPreloadDone()
            );
        }

        // One-time preload: temporarily add portal tickets to the template envelope so the first ever
        // entry doesn't stall waiting for chunks to load.
        if (!data.isPreloadDone()) {
            enqueue(wayfall, new TicketChunksOnceTask(chunks, anchor));
        }

        // Wait for chunks to be present, either due to tickets (first run) or player proximity (later).
        enqueue(wayfall, new WaitForChunksLoadedTask(chunks, !data.isPreloadDone()));

        // Mark preload as done once the envelope is loaded at least once.
        if (!data.isPreloadDone()) {
            enqueue(wayfall, (level) -> {
                if (!areAllLoaded(level, chunks)) {
                    return false;
                }
                WayfallSpawnIslandSavedData d = WayfallSpawnIslandSavedData.get(level);
                if (!d.isPreloadDone()) {
                    d.setPreloadDone(true);
                    d.setDirty();
                }
                return true;
            });
        }

        // Once chunks are loaded, do the actual placement (still on main thread, but now the heavy chunk
        // generation work has been spread across ticks).
        enqueue(wayfall, (level) -> {
            if (!areAllLoaded(level, chunks)) {
                return false;
            }

            WayfallSpawnPlatform.ensureSpawnIslandPlaced(level, true);
            return true;
        });

        // Remove tickets after placement so subsequent chunk loading is purely player-driven.
        if (!data.isPreloadDone()) {
            enqueue(wayfall, new RemoveTicketsTask(chunks, anchor));
        }
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

    private static final class WaitForChunksLoadedTask implements WayfallTask {
        private final List<ChunkPos> chunks;
        private final boolean logAsPreload;
        private int ticksWaited;

        private WaitForChunksLoadedTask(List<ChunkPos> chunks, boolean logAsPreload) {
            this.chunks = chunks;
            this.logAsPreload = logAsPreload;
        }

        @Override
        public boolean tick(ServerLevel wayfall) {
            if (areAllLoaded(wayfall, chunks)) {
                if (ModConfig.WAYFALL_DEBUG_LOGGING.get()) {
                    Kruemblegard.LOGGER.info(
                        "Wayfall init: chunks loaded{} ({})",
                        logAsPreload ? " (preload)" : "",
                        chunks.size()
                    );
                }
                return true;
            }

            ticksWaited++;
            if (ModConfig.WAYFALL_DEBUG_LOGGING.get() && (ticksWaited == 1 || ticksWaited % 100 == 0)) {
                Kruemblegard.LOGGER.info(
                    "Wayfall init: waiting for {}... waited {} ticks",
                    logAsPreload ? "preloaded chunks" : "player-loaded chunks",
                    ticksWaited
                );
            }
            return false;
        }
    }

    private static final class TicketChunksOnceTask implements WayfallTask {
        private static final int CHUNKS_PER_TICK = 4;
        private final List<ChunkPos> chunks;
        private final BlockPos ticketId;
        private int index;

        private TicketChunksOnceTask(List<ChunkPos> chunks, BlockPos ticketId) {
            this.chunks = chunks;
            this.ticketId = ticketId;
        }

        @Override
        public boolean tick(ServerLevel wayfall) {
            int budget = CHUNKS_PER_TICK;
            while (budget-- > 0 && index < chunks.size()) {
                ChunkPos pos = chunks.get(index++);
                wayfall.getChunkSource().addRegionTicket(TicketType.PORTAL, pos, 2, ticketId);
            }

            if (ModConfig.WAYFALL_DEBUG_LOGGING.get() && (index == 0 || index == chunks.size())) {
                Kruemblegard.LOGGER.info("Wayfall init: preload tickets queued {}/{}", index, chunks.size());
            }

            return index >= chunks.size();
        }
    }

    private static final class RemoveTicketsTask implements WayfallTask {
        private static final int CHUNKS_PER_TICK = 8;
        private final List<ChunkPos> chunks;
        private final BlockPos ticketId;
        private int index;

        private RemoveTicketsTask(List<ChunkPos> chunks, BlockPos ticketId) {
            this.chunks = chunks;
            this.ticketId = ticketId;
        }

        @Override
        public boolean tick(ServerLevel wayfall) {
            int budget = CHUNKS_PER_TICK;
            while (budget-- > 0 && index < chunks.size()) {
                ChunkPos pos = chunks.get(index++);
                wayfall.getChunkSource().removeRegionTicket(TicketType.PORTAL, pos, 2, ticketId);
            }
            return index >= chunks.size();
        }
    }
}
