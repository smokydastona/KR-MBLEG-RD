package com.kruemblegard.pressurelogic.network;

import com.kruemblegard.block.PressureConduitBlock;
import com.kruemblegard.pressurelogic.PressureConstants;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.HashSet;

/**
 * Utility for inspecting conduit networks.
 *
 * This is intentionally lightweight: it computes a snapshot via flood-fill on demand.
 * It can be extended later into a caching/ticking network manager once the pressure
 * model stabilizes.
 */
public final class PressureNetworkManager {
    private PressureNetworkManager() {}

    public static PressureNetworkSnapshot computeSnapshot(Level level, BlockPos startPos) {
        if (!(level.getBlockState(startPos).getBlock() instanceof PressureConduitBlock)) {
            return new PressureNetworkSnapshot(0L, startPos, 0, 0, 0, 0);
        }

        HashSet<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);

        BlockPos anchor = startPos;
        int nodeCount = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        long sum = 0;

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            if (!visited.add(pos)) {
                continue;
            }

            if (!(level.getBlockState(pos).getBlock() instanceof PressureConduitBlock)) {
                continue;
            }

            nodeCount++;
            if (pos.asLong() < anchor.asLong()) {
                anchor = pos;
            }

            int p = PressureUtil.getConduitPressureOrState(level, pos);
            p = PressureConstants.clampPressure(p);
            min = Math.min(min, p);
            max = Math.max(max, p);
            sum += p;

            for (BlockPos neighbor : PressureUtil.getConnectedConduits(level, pos)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        if (nodeCount <= 0) {
            return new PressureNetworkSnapshot(0L, startPos, 0, 0, 0, 0);
        }

        int avg = (int) Math.round(sum / (double) nodeCount);
        long dimHash = level.dimension().location().toString().hashCode();
        long networkId = (dimHash << 32) ^ anchor.asLong();

        return new PressureNetworkSnapshot(networkId, anchor, nodeCount, min, max, avg);
    }
}
