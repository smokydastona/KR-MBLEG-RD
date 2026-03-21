package com.kruemblegard.block;

import com.kruemblegard.blockentity.AirLiftTubeBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jetbrains.annotations.Nullable;

public class AirLiftTubeBlock extends Block implements EntityBlock {
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
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AirLiftTubeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.AIR_LIFT_TUBE.get(), AirLiftTubeBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.AIR_LIFT_TUBE.get(), AirLiftTubeBlockEntity::serverTick);
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

        if (!PressureAtmosphere.isStable(level, pos)) {
            return;
        }

        TubeMode mode = state.getValue(TUBE_MODE);
        boolean powered = state.getValue(POWERED);

        // Redstone acts as control: UP/DOWN modes require power to run; BIDIRECTIONAL is always on,
        // and power toggles direction.
        if (mode != TubeMode.BIDIRECTIONAL && !powered) {
            return;
        }

        boolean down = mode == TubeMode.DOWN || (mode == TubeMode.BIDIRECTIONAL && powered);

        // Mandatory spec interpretation: higher flow consumes more pressure.
        BlockPos conduitPos = findBestConduit(level, pos);
        if (conduitPos == null) {
            return;
        }

        int available = PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (available <= 0) {
            return;
        }

        int cost = switch (state.getValue(FLOW_RATE)) {
            case SOFT -> 1;
            case NORMAL -> 2;
            case HIGH -> 4;
        };

        if (available < cost) {
            return;
        }
        PressureUtil.addPressure(level, conduitPos, -cost);

        double speed = switch (state.getValue(FLOW_RATE)) {
            case SOFT -> 0.08;
            case NORMAL -> 0.16;
            case HIGH -> 0.26;
        };

        double targetY = down ? -speed : speed;
        double currentY = entity.getDeltaMovement().y;
        double newY = currentY * 0.6 + targetY * 0.4;
        entity.setDeltaMovement(entity.getDeltaMovement().x, newY, entity.getDeltaMovement().z);
        entity.fallDistance = 0.0F;
        entity.hurtMarked = true;
    }

    private static @Nullable BlockPos findBestConduit(Level level, BlockPos pos) {
        BlockPos best = null;
        int bestPressure = 0;

        BlockPos below = pos.below();
        int belowPressure = PressureUtil.getConduitPressureOrState(level, below);
        if (belowPressure > 0) {
            best = below;
            bestPressure = belowPressure;
        }

        for (Direction dir : Direction.values()) {
            BlockPos p = pos.relative(dir);
            int pressure = PressureUtil.getConduitPressureOrState(level, p);
            if (pressure > bestPressure) {
                best = p;
                bestPressure = pressure;
            }
        }

        return best;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 5);
    }
}
