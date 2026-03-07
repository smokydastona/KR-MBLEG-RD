package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.Nullable;

public class AtmosphericCompressorBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty STABILITY_LEVEL = IntegerProperty.create("stability_level", 0, 5);

    public AtmosphericCompressorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STABILITY_LEVEL, 0));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        int stability = sampleStabilityLevel(context.getLevel(), context.getClickedPos());
        BlockState state = this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(STABILITY_LEVEL, stability);

        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 20);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STABILITY_LEVEL);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int current = state.getValue(STABILITY_LEVEL);
        int target = sampleStabilityLevel(level, pos);

        int next;
        if (target > current) {
            next = current + 1;
        } else if (target < current) {
            next = current - 1;
        } else {
            next = current;
        }

        if (next != current) {
            level.setBlock(pos, state.setValue(STABILITY_LEVEL, next), 2);
        }

        // Late-game generator: gently pressurize adjacent conduit networks.
        if (next > 0) {
            int delta = next; // 1..5 every 20 ticks
            for (Direction dir : Direction.values()) {
                PressureUtil.addPressure(level, pos.relative(dir), delta);
            }
        }

        level.scheduleTick(pos, this, 20);
    }

    private static int sampleStabilityLevel(Level level, BlockPos pos) {
        // In Wayfall, stability is naturally high.
        if (level.dimension().location().equals(new net.minecraft.resources.ResourceLocation("kruemblegard", "wayfall"))) {
            return 5;
        }

        int bestPressure = 0;
        for (Direction dir : Direction.values()) {
            bestPressure = Math.max(bestPressure, PressureUtil.getConduitPressureOrState(level, pos.relative(dir)));
        }

        int bestLevel = PressureUtil.pressureToLevel(bestPressure);

        // Fall back to redstone strength as scaffolding.
        int signal = level.getBestNeighborSignal(pos);
        bestLevel = Math.max(bestLevel, (int) Math.round((signal / 15.0) * 5.0));

        // Baseline: compressors should create some stability on their own.
        bestLevel = Math.max(bestLevel, 1);

        return Mth.clamp(bestLevel, 0, 5);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
}
