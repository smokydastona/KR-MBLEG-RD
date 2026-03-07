package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.Nullable;

public class ConveyorMembraneBlock extends Block {

    public static final IntegerProperty PULSE_PHASE = IntegerProperty.create("pulse_phase", 0, 3);

    public ConveyorMembraneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PULSE_PHASE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PULSE_PHASE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 5);
        }
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 5);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int phase = state.getValue(PULSE_PHASE);
        int next = (phase + 1) & 3;
        level.setBlock(pos, state.setValue(PULSE_PHASE, next), 2);
        level.scheduleTick(pos, this, 5);
    }
}
