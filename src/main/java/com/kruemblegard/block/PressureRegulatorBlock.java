package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
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

public class PressureRegulatorBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty SIGNAL = IntegerProperty.create("signal", 0, 15);

    public PressureRegulatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SIGNAL, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int signal = context.getLevel().getBestNeighborSignal(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
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

        int signalNow = level.getBestNeighborSignal(pos);
        if (signalNow != state.getValue(SIGNAL)) {
            level.setBlock(pos, state.setValue(SIGNAL, signalNow), Block.UPDATE_CLIENTS);
        }

        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!PressureAtmosphere.isStable(level, pos)) {
            level.scheduleTick(pos, this, 10);
            return;
        }

        Direction facing = state.getValue(FACING);
        int signal = state.getValue(SIGNAL);
        int targetPressure = Mth.clamp((int) Math.round((signal / 15.0) * 100.0), 0, 100);

        BlockPos inPos = pos.relative(facing.getOpposite());
        BlockPos outPos = pos.relative(facing);

        int inPressure = PressureUtil.getConduitPressureOrState(level, inPos);
        int outPressure = PressureUtil.getConduitPressureOrState(level, outPos);

        int allowedOut = Math.min(inPressure, targetPressure);

        // Move pressure forward up to the allowed level.
        if (outPressure < allowedOut) {
            int move = Math.min(allowedOut - outPressure, 8);
            if (move > 0) {
                PressureUtil.addPressure(level, inPos, -move);
                PressureUtil.addPressure(level, outPos, move);
            }
        } else if (outPressure > allowedOut) {
            // Bleed excess back (best-effort).
            int bleed = Math.min(outPressure - allowedOut, 8);
            if (bleed > 0) {
                PressureUtil.addPressure(level, outPos, -bleed);
                PressureUtil.addPressure(level, inPos, bleed);
            }
        }

        level.scheduleTick(pos, this, 10);
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
