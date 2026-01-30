package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * A fence that behaves like a normal fence, but decays using the same rules as leaves.
 *
 * Default state is non-persistent (schematic/command-placed). If placed by a player (if an item
 * is ever added), it becomes persistent like vanilla leaves.
 */
public class FranchFenceBlock extends FenceBlock {
    public static final IntegerProperty DISTANCE = FranchDecay.DISTANCE;
    public static final BooleanProperty PERSISTENT = FranchDecay.PERSISTENT;

    public FranchFenceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, FranchDecay.DECAY_DISTANCE)
                .setValue(PERSISTENT, false)
                .setValue(WATERLOGGED, false));
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

        int distance = FranchDecay.updateDistance(context.getLevel(), context.getClickedPos());
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

        int distance = FranchDecay.updateDistance(level, pos);
        if (updated.getValue(DISTANCE) != distance) {
            updated = updated.setValue(DISTANCE, distance);
        }

        if (FranchDecay.shouldDecay(updated)) {
            level.scheduleTick(pos, this, 1);
        }

        return updated;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int distance = FranchDecay.updateDistance(level, pos);
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
        return FranchDecay.shouldDecay(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (FranchDecay.shouldDecay(state)) {
            // Drops are controlled by the block loot table (sticks).
            level.destroyBlock(pos, true);
        }
    }
}
