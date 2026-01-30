package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class FranchDecay {
    public static final int DECAY_DISTANCE = 7;

    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    private FranchDecay() {}

    public static boolean shouldDecay(BlockState state) {
        return !state.getValue(PERSISTENT) && state.getValue(DISTANCE) >= DECAY_DISTANCE;
    }

    public static int updateDistance(LevelAccessor level, BlockPos pos) {
        int minDistance = DECAY_DISTANCE;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            int neighborDistance = getDistanceAt(neighborState) + 1;
            if (neighborDistance < minDistance) {
                minDistance = neighborDistance;
            }

            if (minDistance == 1) {
                break;
            }
        }

        return minDistance;
    }

    public static int getDistanceAt(BlockState state) {
        if (state.is(BlockTags.LOGS)) {
            return 0;
        }

        if (state.hasProperty(DISTANCE)) {
            return state.getValue(DISTANCE);
        }

        return DECAY_DISTANCE;
    }
}
