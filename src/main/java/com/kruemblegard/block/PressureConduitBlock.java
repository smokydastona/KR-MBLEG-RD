package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.Nullable;

public class PressureConduitBlock extends Block {
    public static final IntegerProperty PRESSURE_LEVEL = IntegerProperty.create("pressure_level", 0, 5);

    public PressureConduitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PRESSURE_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PRESSURE_LEVEL);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(PRESSURE_LEVEL, samplePressureLevel(context.getLevel(), context.getClickedPos()));
        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 10);
        }
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide) {
            return;
        }
        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int current = state.getValue(PRESSURE_LEVEL);
        int sampled = samplePressureLevel(level, pos);

        int next;
        if (sampled > current) {
            next = current + 1;
        } else if (sampled < current) {
            next = current - 1;
        } else {
            next = current;
        }

        if (next != current) {
            level.setBlock(pos, state.setValue(PRESSURE_LEVEL, next), 2);
        }

        level.scheduleTick(pos, this, 10);
    }

    private static int samplePressureLevel(LevelAccessor level, BlockPos pos) {
        int best = 0;

        // Use redstone strength as a stand-in for pressure during scaffolding.
        if (level instanceof Level l) {
            int signal = l.getBestNeighborSignal(pos);
            best = Math.max(best, (int) Math.round((signal / 15.0) * 5.0));
        }

        // Slightly "inherit" nearby conduit levels.
        for (Direction dir : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            if (neighbor.getBlock() instanceof PressureConduitBlock) {
                best = Math.max(best, neighbor.getValue(PRESSURE_LEVEL) - 1);
            }
        }

        if (best < 0) best = 0;
        if (best > 5) best = 5;
        return best;
    }
}
