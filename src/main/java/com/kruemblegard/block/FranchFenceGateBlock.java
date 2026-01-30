package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A fence gate that behaves like a normal fence gate, but decays using the same rules as leaves.
 *
 * Default state is non-persistent (schematic/command-placed). If placed by a player (if an item
 * is ever added), it becomes persistent like vanilla leaves.
 */
public class FranchFenceGateBlock extends FenceGateBlock {
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    private static final int DECAY_DISTANCE = 7;

    public FranchFenceGateBlock(Properties properties, WoodType type) {
        super(properties, type);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, DECAY_DISTANCE)
                .setValue(PERSISTENT, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(POWERED, false)
                .setValue(IN_WALL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DISTANCE, PERSISTENT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }

        int distance = updateDistance(context.getLevel(), context.getClickedPos());
        return state.setValue(PERSISTENT, true).setValue(DISTANCE, distance);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);

        int distance = updateDistance(level, pos);
        if (updated.getValue(DISTANCE) != distance) {
            updated = updated.setValue(DISTANCE, distance);
        }

        if (shouldDecay(updated)) {
            level.scheduleTick(pos, this, 1);
        }

        return updated;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int distance = updateDistance(level, pos);
        BlockState updated = state;
        if (updated.getValue(DISTANCE) != distance) {
            updated = updated.setValue(DISTANCE, distance);
        }

        if (updated != state) {
            level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return shouldDecay(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (shouldDecay(state)) {
            // Drops are controlled by the block loot table (sticks).
            level.destroyBlock(pos, true);
        }
    }

    private static boolean shouldDecay(BlockState state) {
        return !state.getValue(PERSISTENT) && state.getValue(DISTANCE) >= DECAY_DISTANCE;
    }

    private static int updateDistance(LevelAccessor level, BlockPos pos) {
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

    private static int getDistanceAt(BlockState state) {
        if (state.is(BlockTags.LOGS)) {
            return 0;
        }

        if (state.getBlock() instanceof FranchFenceBlock || state.getBlock() instanceof FranchFenceGateBlock) {
            return state.getValue(DISTANCE);
        }

        return DECAY_DISTANCE;
    }
}
