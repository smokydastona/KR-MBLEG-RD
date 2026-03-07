package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.BlockGetter;

public class PressureSensorBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty SIGNAL = IntegerProperty.create("signal", 0, 15);

    public PressureSensorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SIGNAL, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        int signal = getInputSignal(context.getLevel(), context.getClickedPos(), facing);
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(SIGNAL, signal);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIGNAL);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            BlockPos neighborPos,
            boolean isMoving
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide) {
            return;
        }

        int signalNow = getInputSignal(level, pos, state.getValue(FACING));
        if (signalNow != state.getValue(SIGNAL)) {
            level.setBlock(pos, state.setValue(SIGNAL, signalNow), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == state.getValue(FACING) ? state.getValue(SIGNAL) : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    private static int getInputSignal(Level level, BlockPos pos, Direction facing) {
        Direction inputSide = facing.getOpposite();
        BlockPos neighborPos = pos.relative(inputSide);
        Direction towardsSensor = inputSide.getOpposite();

        int weak = level.getSignal(neighborPos, towardsSensor);
        int strong = level.getDirectSignal(neighborPos, towardsSensor);
        return Math.max(weak, strong);
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
