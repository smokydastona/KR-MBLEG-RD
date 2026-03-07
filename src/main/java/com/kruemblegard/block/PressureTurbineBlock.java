package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

public class PressureTurbineBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty ROTATION_SPEED = IntegerProperty.create("rotation_speed", 0, 5);

    public PressureTurbineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(ROTATION_SPEED, 0));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        int speed = sampleInputPressure(context.getLevel(), context.getClickedPos(), facing);
        BlockState state = this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(ROTATION_SPEED, speed);

        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 10);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION_SPEED);
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
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        int current = state.getValue(ROTATION_SPEED);
        int target = PressureAtmosphere.isStable(level, pos) ? sampleInputPressure(level, pos, facing) : 0;

        int next;
        if (target > current) {
            next = current + 1;
        } else if (target < current) {
            next = current - 1;
        } else {
            next = current;
        }

        if (next != current) {
            level.setBlock(pos, state.setValue(ROTATION_SPEED, next), 2);
        }

        // Consume some input pressure proportional to speed.
        if (target > 0 && PressureAtmosphere.isStable(level, pos)) {
            BlockPos inputPos = pos.relative(facing.getOpposite());
            int consume = target * 3; // 0..15 per tick
            PressureUtil.addPressure(level, inputPos, -consume);
        }

        level.scheduleTick(pos, this, 10);
    }

    private static int sampleInputPressure(Level level, BlockPos pos, Direction facing) {
        Direction inputSide = facing.getOpposite();
        BlockPos inputPos = pos.relative(inputSide);
        BlockState inputState = level.getBlockState(inputPos);

        if (inputState.getBlock() instanceof PressureConduitBlock) {
            int pressure = PressureUtil.getConduitPressureOrState(level, inputPos);
            return PressureUtil.pressureToLevel(pressure);
        }

        int signal = level.getBestNeighborSignal(pos);
        int scaled = (int) Math.round((signal / 15.0) * 5.0);
        if (scaled < 0) scaled = 0;
        if (scaled > 5) scaled = 5;
        return scaled;
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
