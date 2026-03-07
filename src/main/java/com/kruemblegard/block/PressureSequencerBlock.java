package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class PressureSequencerBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty STEP = IntegerProperty.create("step", 0, 3);

    public PressureSequencerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(STEP, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(STEP, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, STEP);
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

        boolean poweredNow = level.hasNeighborSignal(pos);
        boolean poweredBefore = state.getValue(POWERED);

        if (poweredNow == poweredBefore) {
            return;
        }

        BlockState next = state.setValue(POWERED, poweredNow);
        if (poweredNow && !poweredBefore) {
            int step = state.getValue(STEP);
            int nextStep = (step + 1) % 4;
            next = next.setValue(STEP, nextStep);
            emitPulse(level, pos, next.getValue(FACING), nextStep);
        }

        level.setBlock(pos, next, Block.UPDATE_CLIENTS);
    }

    private static void emitPulse(Level level, BlockPos pos, Direction facing, int step) {
        if (!PressureAtmosphere.isStable(level, pos)) {
            return;
        }

        Direction outDir = switch (step) {
            case 0 -> facing;
            case 1 -> facing.getClockWise();
            case 2 -> facing.getOpposite();
            default -> facing.getCounterClockWise();
        };

        int signal = level.getBestNeighborSignal(pos);
        int pulse = Mth.clamp((int) Math.round((signal / 15.0) * 24.0), 0, 24);
        if (pulse <= 0) {
            pulse = 8;
        }

        BlockPos outPos = pos.relative(outDir);
        PressureUtil.addPressure(level, outPos, pulse);
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
