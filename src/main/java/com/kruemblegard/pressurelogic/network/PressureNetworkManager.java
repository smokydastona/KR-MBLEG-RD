package com.kruemblegard.pressurelogic.network;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.PressureConduitBlock;
import com.kruemblegard.blockentity.PressureConduitBlockEntity;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.pressurelogic.PressureConstants;
import com.kruemblegard.pressurelogic.PressureUtil;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pressure network manager.
 *
 * Goals:
 * - Coalesce "network rebuild" work to avoid rebuild storms.
 * - Provide an optional path to tick by network (bounded work per tick).
 * - Never rely on permanent machine references (machines should resolve via adjacent conduit).
 */
public final class PressureNetworkManager {
    private PressureNetworkManager() {}

    private static final Map<ServerLevel, LevelState> STATES = new ConcurrentHashMap<>();

    private static LevelState state(ServerLevel level) {
        return STATES.computeIfAbsent(level, ignored -> new LevelState());
    }

    /**
     * Marks a position as needing network validation.
     * This does not rebuild immediately; work is coalesced and processed later.
     */
    public static void markDirty(ServerLevel level, BlockPos pos) {
        if (!ModConfig.PRESSURE_NETWORK_MANAGER_ENABLED.get()) {
            return;
        }
        state(level).dirtyStarts.add(pos.asLong());
    }

    public static void markDirtyAround(ServerLevel level, BlockPos pos) {
        markDirty(level, pos);
        for (var dir : net.minecraft.core.Direction.values()) {
            BlockPos n = pos.relative(dir);
            if (level.isLoaded(n)) {
                markDirty(level, n);
            }
        }
    }

    static void onLevelUnload(ServerLevel level) {
        STATES.remove(level);
    }

    static void onServerTick(ServerLevel level) {
        if (!ModConfig.PRESSURE_SYSTEM_ENABLED.get()) {
            return;
        }

        if (!ModConfig.PRESSURE_NETWORK_MANAGER_ENABLED.get()) {
            return;
        }

        LevelState s = state(level);
        long t = level.getGameTime();

        int rebuildInterval = Math.max(1, ModConfig.PRESSURE_NETWORK_REBUILD_INTERVAL_TICKS.get());
        if ((t % rebuildInterval) == 0) {
            int maxStarts = Math.max(1, ModConfig.PRESSURE_NETWORK_MAX_REBUILDS_PER_PASS.get());
            rebuildDirty(level, s, maxStarts);
        }

        if (!ModConfig.PRESSURE_NETWORK_TICKING_ENABLED.get()) {
            return;
        }

        // Network tick interval reuses the conduit tick interval knob.
        int tickInterval = Math.max(1, ModConfig.PRESSURE_TICK_INTERVAL_TICKS.get());
        if ((t % tickInterval) != 0) {
            return;
        }

        int maxNodesPerTick = Math.max(1, ModConfig.PRESSURE_NETWORK_MAX_NODES_PER_TICK.get());
        tickNetworks(level, s, maxNodesPerTick);
    }

    private static void rebuildDirty(ServerLevel level, LevelState s, int maxStarts) {
        if (s.dirtyStarts.isEmpty()) {
            return;
        }

        int processed = 0;
        for (var it = s.dirtyStarts.iterator(); it.hasNext() && processed < maxStarts; ) {
            long startLong = it.nextLong();
            it.remove();
            processed++;

            BlockPos startPos = BlockPos.of(startLong);
            if (!level.isLoaded(startPos)) {
                continue;
            }

            if (!(level.getBlockState(startPos).getBlock() instanceof PressureConduitBlock)) {
                // If the conduit was removed, clear any mapping.
                long old = s.nodeToNetworkId.remove(startLong);
                if (old != 0L) {
                    PressureNetwork oldNet = s.networks.get(old);
                    if (oldNet != null) {
                        oldNet.removeNode(startLong);
                        if (oldNet.isEmpty()) {
                            s.networks.remove(old);
                        }
                    }
                }
                continue;
            }

            rebuildNetworkFrom(level, s, startPos);
        }
    }

    private static void rebuildNetworkFrom(ServerLevel level, LevelState s, BlockPos startPos) {
        LongOpenHashSet visited = new LongOpenHashSet();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);

        long anchorLong = startPos.asLong();

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            if (!level.isLoaded(pos)) {
                continue;
            }

            long posLong = pos.asLong();
            if (!visited.add(posLong)) {
                continue;
            }

            if (!(level.getBlockState(pos).getBlock() instanceof PressureConduitBlock)) {
                continue;
            }

            if (posLong < anchorLong) {
                anchorLong = posLong;
            }

            for (BlockPos neighbor : PressureUtil.getConnectedConduits(level, pos)) {
                if (level.isLoaded(neighbor) && !visited.contains(neighbor.asLong())) {
                    queue.add(neighbor);
                }
            }
        }

        if (visited.isEmpty()) {
            return;
        }

        long dimHash = level.dimension().location().toString().hashCode();
        long networkId = (dimHash << 32) ^ anchorLong;

        // If this network already exists, we'll replace it wholesale.
        PressureNetwork net = new PressureNetwork(networkId, anchorLong, visited);
        s.networks.put(networkId, net);

        int nodeCount = visited.size();
        for (var it = visited.iterator(); it.hasNext();) {
            long nodeLong = it.nextLong();

            long oldId = s.nodeToNetworkId.put(nodeLong, networkId);
            if (oldId != 0L && oldId != networkId) {
                PressureNetwork oldNet = s.networks.get(oldId);
                if (oldNet != null) {
                    oldNet.removeNode(nodeLong);
                    if (oldNet.isEmpty()) {
                        s.networks.remove(oldId);
                    }
                }
            }

            BlockPos nodePos = BlockPos.of(nodeLong);
            if (level.getBlockEntity(nodePos) instanceof PressureConduitBlockEntity be) {
                be.setNetworkDebugInfo(networkId, nodeCount);
            }
        }

        if (ModConfig.PRESSURE_DEBUG_LOGGING.get() && ModConfig.PRESSURE_DEBUG_INSPECT.get()) {
            Kruemblegard.LOGGER.debug("Pressure network rebuilt: id={} nodes={} anchor={}", Long.toHexString(networkId), nodeCount, BlockPos.of(anchorLong));
        }
    }

    private static void tickNetworks(ServerLevel level, LevelState s, int maxNodesTotal) {
        if (s.networks.isEmpty()) {
            return;
        }

        int remaining = maxNodesTotal;
        for (var it = s.networks.long2ObjectEntrySet().fastIterator(); it.hasNext() && remaining > 0;) {
            var entry = it.next();
            long networkId = entry.getLongKey();
            PressureNetwork network = entry.getValue();
            if (network == null || network.isEmpty()) {
                it.remove();
                continue;
            }

            // Round-robin within the network; bounded total work.
            int perNet = Math.max(1, Math.min(remaining, ModConfig.PRESSURE_NETWORK_MAX_NODES_PER_TICK.get()));
            int processed = tickNetwork(level, s, networkId, network, perNet);
            remaining -= processed;
        }
    }

    private static int tickNetwork(ServerLevel level, LevelState s, long networkId, PressureNetwork network, int budget) {
        int processed = 0;

        int maxStep = Math.max(1, ModConfig.PRESSURE_CONDUIT_MAX_STEP_PER_UPDATE.get());
        int leak = Math.max(0, ModConfig.PRESSURE_CONDUIT_LEAK_PER_UPDATE.get());
        int maxPressure = Math.max(1, Math.min(PressureConstants.MAX_PRESSURE, ModConfig.PRESSURE_CONDUIT_MAX_PRESSURE.get()));

        while (processed < budget && !network.isEmpty()) {
            long posLong = network.nextNode();
            if (posLong == 0L) {
                break;
            }

            // Skip stale membership.
            if (s.nodeToNetworkId.get(posLong) != networkId) {
                network.removeNode(posLong);
                continue;
            }

            BlockPos pos = BlockPos.of(posLong);
            if (!level.isLoaded(pos)) {
                processed++;
                continue;
            }

            if (!(level.getBlockState(pos).getBlock() instanceof PressureConduitBlock)) {
                s.nodeToNetworkId.remove(posLong);
                network.removeNode(posLong);
                processed++;
                continue;
            }

            if (!(level.getBlockEntity(pos) instanceof PressureConduitBlockEntity be)) {
                // No BE means we can't simulate storage; defer until it exists.
                processed++;
                continue;
            }

            int current = be.getPressure().get();
            int target = PressureUtil.sampleNeighborAveragePressure(level, pos);
            int next = PressureUtil.approach(current, target, maxStep);
            if (leak > 0 && next > PressureConstants.MIN_PRESSURE) {
                next = Math.max(PressureConstants.MIN_PRESSURE, next - leak);
            }
            next = Mth.clamp(next, PressureConstants.MIN_PRESSURE, maxPressure);

            if (next != current) {
                be.getPressure().set(next);
                be.setChanged();
            }

            PressureUtil.syncConduitVisualState(level, pos, be.getPressure().get());

            processed++;
        }

        return processed;
    }

    /**
     * Debug helper: computes a snapshot via flood-fill on demand.
     *
     * This is safe but can be expensive on huge networks; prefer cached network IDs
     * and manager-driven inspection where available.
     */
    public static PressureNetworkSnapshot computeSnapshot(Level level, BlockPos startPos) {
        if (!(level.getBlockState(startPos).getBlock() instanceof PressureConduitBlock)) {
            return new PressureNetworkSnapshot(0L, startPos, 0, 0, 0, 0);
        }

        LongSet visited = new LongOpenHashSet();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);

        long anchorLong = startPos.asLong();
        int nodeCount = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        long sum = 0;

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            if (!level.isLoaded(pos)) {
                continue;
            }

            long posLong = pos.asLong();
            if (!visited.add(posLong)) {
                continue;
            }

            if (!(level.getBlockState(pos).getBlock() instanceof PressureConduitBlock)) {
                continue;
            }

            nodeCount++;
            if (posLong < anchorLong) {
                anchorLong = posLong;
            }

            int p = PressureUtil.getConduitPressureOrState(level, pos);
            p = PressureConstants.clampPressure(p);
            min = Math.min(min, p);
            max = Math.max(max, p);
            sum += p;

            for (BlockPos neighbor : PressureUtil.getConnectedConduits(level, pos)) {
                if (level.isLoaded(neighbor) && !visited.contains(neighbor.asLong())) {
                    queue.add(neighbor);
                }
            }
        }

        if (nodeCount <= 0) {
            return new PressureNetworkSnapshot(0L, startPos, 0, 0, 0, 0);
        }

        int avg = (int) Math.round(sum / (double) nodeCount);
        long dimHash = level.dimension().location().toString().hashCode();
        long networkId = (dimHash << 32) ^ anchorLong;

        return new PressureNetworkSnapshot(networkId, BlockPos.of(anchorLong), nodeCount, min, max, avg);
    }

    private static final class LevelState {
        final LongOpenHashSet dirtyStarts = new LongOpenHashSet();

        final Long2LongOpenHashMap nodeToNetworkId = new Long2LongOpenHashMap();
        final Long2ObjectOpenHashMap<PressureNetwork> networks = new Long2ObjectOpenHashMap<>();

        LevelState() {
            nodeToNetworkId.defaultReturnValue(0L);
        }
    }
}
