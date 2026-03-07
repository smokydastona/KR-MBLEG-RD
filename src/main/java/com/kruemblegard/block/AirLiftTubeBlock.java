package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jetbrains.annotations.Nullable;

public class AirLiftTubeBlock extends Block {
    public enum TubeMode implements StringRepresentable {
        UP("up"),
        DOWN("down"),
        BIDIRECTIONAL("bidirectional");

        private final String name;

        TubeMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public enum FlowRate implements StringRepresentable {
        SOFT("soft"),
        NORMAL("normal"),
        HIGH("high");

        private final String name;

        FlowRate(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final EnumProperty<TubeMode> TUBE_MODE = EnumProperty.create("tube_mode", TubeMode.class);
    public static final EnumProperty<FlowRate> FLOW_RATE = EnumProperty.create("flow_rate", FlowRate.class);

    public AirLiftTubeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(TUBE_MODE, TubeMode.UP)
                .setValue(FLOW_RATE, FlowRate.NORMAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, TUBE_MODE, FLOW_RATE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        TubeMode mode = (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) ? TubeMode.DOWN : TubeMode.UP;
        return this.defaultBlockState()
                .setValue(POWERED, powered)
                .setValue(TUBE_MODE, mode)
                .setValue(FLOW_RATE, FlowRate.NORMAL);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 5);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide) {
            return;
        }

        boolean poweredNow = level.hasNeighborSignal(pos);
        if (poweredNow != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, poweredNow), Block.UPDATE_CLIENTS);
        }

        level.scheduleTick(pos, this, 5);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide) {
            return;
        }

        TubeMode mode = state.getValue(TUBE_MODE);
        boolean powered = state.getValue(POWERED);
        boolean down = mode == TubeMode.DOWN || (mode == TubeMode.BIDIRECTIONAL && powered);

        double speed = switch (state.getValue(FLOW_RATE)) {
            case SOFT -> 0.08;
            case NORMAL -> 0.16;
            case HIGH -> 0.26;
        };

        double vy = down ? -speed : speed;
        entity.setDeltaMovement(entity.getDeltaMovement().x, vy, entity.getDeltaMovement().z);
        entity.fallDistance = 0.0F;
        entity.hurtMarked = true;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 5);
    }
}
