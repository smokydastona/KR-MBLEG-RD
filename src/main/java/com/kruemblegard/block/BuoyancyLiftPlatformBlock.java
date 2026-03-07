package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import org.jetbrains.annotations.Nullable;

public class BuoyancyLiftPlatformBlock extends Block {

    public enum LiftState implements StringRepresentable {
        IDLE("idle"),
        RISING("rising"),
        FALLING("falling");

        private final String name;

        LiftState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<LiftState> LIFT_STATE = EnumProperty.create("lift_state", LiftState.class);

    public BuoyancyLiftPlatformBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIFT_STATE, LiftState.IDLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIFT_STATE});
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return this.defaultBlockState().setValue(LIFT_STATE, powered ? LiftState.RISING : LiftState.IDLE);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean isMoving) {
        if (level.isClientSide) {
            return;
        }

        boolean powered = level.hasNeighborSignal(pos);
        LiftState current = state.getValue(LIFT_STATE);

        if (powered) {
            if (current != LiftState.RISING) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.RISING), 2);
            }
        } else {
            if (current == LiftState.RISING) {
                level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.FALLING), 2);
                level.scheduleTick(pos, this, 20);
            }
        }

        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(LIFT_STATE) == LiftState.FALLING) {
            level.setBlock(pos, state.setValue(LIFT_STATE, LiftState.IDLE), 2);
        }
    }
}
