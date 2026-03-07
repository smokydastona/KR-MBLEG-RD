package com.kruemblegard.rotationlogic;

import com.kruemblegard.block.PressureTurbineBlock;
import com.kruemblegard.block.SpiralGearboxBlock;
import com.kruemblegard.block.SpiralShaftBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;

public final class RotationUtil {
    private RotationUtil() {}

    public static int clampLevel(int level) {
        return Mth.clamp(level, 0, 5);
    }

    /**
     * Returns the best available rotation level (0..5) usable by a consumer at {@code consumerPos}.
     *
     * Rotation is sourced from {@link PressureTurbineBlock} and carried through {@link SpiralShaftBlock}
     * and {@link SpiralGearboxBlock}.
     */
    public static int getRotationLevel(Level level, BlockPos consumerPos) {
        if (level.isClientSide) {
            return 0;
        }

        int best = 0;
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = consumerPos.relative(dir);
            best = Math.max(best, getRotationLevelFromNeighbor(level, consumerPos, neighbor, dir.getOpposite()));
            if (best >= 5) {
                return 5;
            }
        }
        return best;
    }

    /**
     * Computes rotation at {@code consumerPos} by walking the mechanical network starting at {@code startPos}.
     *
     * @param cameFrom Direction from {@code startPos} back toward {@code consumerPos}.
     */
    private static int getRotationLevelFromNeighbor(Level level, BlockPos consumerPos, BlockPos startPos, Direction cameFrom) {
        record Node(BlockPos pos, Direction cameFrom, double invMultiplier, int depth) {}

        final int maxDepth = 96;
        final int maxVisited = 256;

        ArrayDeque<Node> queue = new ArrayDeque<>();
        HashSet<Long> visited = new HashSet<>();

        queue.add(new Node(startPos, cameFrom, 1.0, 0));

        int best = 0;

        while (!queue.isEmpty() && visited.size() < maxVisited) {
            Node node = queue.removeFirst();
            if (node.depth >= maxDepth) {
                continue;
            }

            long key = node.pos.asLong() ^ (((long) node.cameFrom.ordinal()) << 56);
            if (!visited.add(key)) {
                continue;
            }

            BlockState state = level.getBlockState(node.pos);

            // Source: turbine outputs on its FACING side.
            if (state.getBlock() instanceof PressureTurbineBlock) {
                Direction out = state.getValue(PressureTurbineBlock.FACING);
                if (node.cameFrom == out) {
                    int src = clampLevel(state.getValue(PressureTurbineBlock.ROTATION_SPEED));
                    int effective = clampLevel((int) Math.round(src / node.invMultiplier));
                    best = Math.max(best, effective);
                    if (best >= 5) {
                        return 5;
                    }
                }
                continue;
            }

            // Carrier: shaft (tappable from any side; carries along its axis).
            if (state.getBlock() instanceof SpiralShaftBlock) {
                Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
                for (Direction nextDir : axisDirections(axis)) {
                    BlockPos nextPos = node.pos.relative(nextDir);
                    Direction nextCameFrom = nextDir.getOpposite();
                    if (nextPos.equals(consumerPos)) {
                        continue;
                    }
                    queue.add(new Node(nextPos, nextCameFrom, node.invMultiplier, node.depth + 1));
                }
                continue;
            }

            // Transformer: gearbox (inline between FACING and FACING.opposite). Can be tapped from any side.
            if (state.getBlock() instanceof SpiralGearboxBlock) {
                Direction outDir = state.getValue(SpiralGearboxBlock.FACING);
                Direction inDir = outDir.getOpposite();
                SpiralGearboxBlock.Ratio ratio = state.getValue(SpiralGearboxBlock.RATIO);
                double m = ratioMultiplier(ratio);

                if (node.cameFrom == inDir) {
                    // consumer is on input side, walk toward output side: input -> output
                    queue.add(new Node(node.pos.relative(outDir), outDir.getOpposite(), node.invMultiplier * m, node.depth + 1));
                } else if (node.cameFrom == outDir) {
                    // consumer is on output side, walk toward input side: output -> input
                    queue.add(new Node(node.pos.relative(inDir), inDir.getOpposite(), node.invMultiplier * (1.0 / m), node.depth + 1));
                } else {
                    // side tap: allow walking to both sides, treating the tap as either input or output.
                    queue.add(new Node(node.pos.relative(outDir), outDir.getOpposite(), node.invMultiplier * m, node.depth + 1));
                    queue.add(new Node(node.pos.relative(inDir), inDir.getOpposite(), node.invMultiplier * (1.0 / m), node.depth + 1));
                }

                continue;
            }
        }

        return best;
    }

    private static Direction[] axisDirections(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Direction[]{Direction.EAST, Direction.WEST};
            case Y -> new Direction[]{Direction.UP, Direction.DOWN};
            case Z -> new Direction[]{Direction.NORTH, Direction.SOUTH};
        };
    }

    private static double ratioMultiplier(SpiralGearboxBlock.Ratio ratio) {
        return switch (ratio) {
            case RATIO_1_1 -> 1.0;
            case RATIO_1_2 -> 2.0;
            case RATIO_2_1 -> 0.5;
            case RATIO_1_4 -> 4.0;
            case RATIO_4_1 -> 0.25;
        };
    }
}
