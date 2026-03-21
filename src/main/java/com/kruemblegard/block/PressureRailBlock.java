package com.kruemblegard.block;

import com.kruemblegard.blockentity.PressureRailBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.rotationlogic.RotationUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class PressureRailBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public enum RailMode implements StringRepresentable {
        SOFT("soft"),
        NORMAL("normal"),
        HIGH("high");

        private final String name;

        RailMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final EnumProperty<RailMode> RAIL_MODE = EnumProperty.create("rail_mode", RailMode.class);
    public static final IntegerProperty PULSE_PHASE = IntegerProperty.create("pulse_phase", 0, 3);

    private static final int TICK_IDLE = 10;

    public PressureRailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(RAIL_MODE, RailMode.NORMAL)
                .setValue(PULSE_PHASE, 0));
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Mandatory spec interpretation: bidirectional rail toggle.
        // Implemented as a shift-right-click 180° flip using existing FACING.
        if (player.isShiftKeyDown()) {
            Direction next = state.getValue(FACING).getOpposite();
            level.setBlock(pos, state.setValue(FACING, next), Block.UPDATE_CLIENTS);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(RAIL_MODE, RailMode.NORMAL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, RAIL_MODE, PULSE_PHASE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PressureRailBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.PRESSURE_RAIL.get(), PressureRailBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.PRESSURE_RAIL.get(), PressureRailBlockEntity::serverTick);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, TICK_IDLE);
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
            BlockState next = state.setValue(POWERED, poweredNow);
            level.setBlock(pos, next, Block.UPDATE_CLIENTS);
            state = next;
        }

        if (state.getValue(POWERED)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (level.isClientSide) {
            return;
        }
        if (!state.getValue(POWERED)) {
            return;
        }
        if (!PressureAtmosphere.isStable(level, pos)) {
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        if (conduitPos == null) {
            return;
        }

        int pressure = PressureUtil.getConduitPressureOrState(level, conduitPos);
        if (pressure <= 0) {
            return;
        }

        int rotation = RotationUtil.getRotationLevel(level, pos);

        Direction facing = state.getValue(FACING);
        double base = switch (state.getValue(RAIL_MODE)) {
            case SOFT -> 0.06;
            case NORMAL -> 0.12;
            case HIGH -> 0.20;
        };

        double pressureFactor = 0.25 + 0.75 * (pressure / 100.0);
        double speed = base * pressureFactor * (1.0 + 0.12 * rotation);

        entity.setDeltaMovement(
                entity.getDeltaMovement().add(facing.getStepX() * speed, 0.0, facing.getStepZ() * speed)
        );
        entity.hurtMarked = true;

        int cost = switch (state.getValue(RAIL_MODE)) {
            case SOFT -> 1;
            case NORMAL -> 2;
            case HIGH -> 3;
        };
        if (rotation > 0) {
            cost += 1;
        }
        PressureUtil.addPressure(level, conduitPos, -cost);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!state.getValue(POWERED)) {
            return;
        }
        if (!PressureAtmosphere.isStable(level, pos)) {
            return;
        }
        if (random.nextInt(6) != 0) {
            return;
        }

        Direction facing = state.getValue(FACING);
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
        double y = pos.getY() + 0.1;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
        double vx = facing.getStepX() * 0.02;
        double vy = 0.01;
        double vz = facing.getStepZ() * 0.02;
        level.addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD, x, y, z, vx, vy, vz);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean powered = state.getValue(POWERED);
        if (!powered) {
            if (state.getValue(PULSE_PHASE) != 0) {
                level.setBlock(pos, state.setValue(PULSE_PHASE, 0), Block.UPDATE_CLIENTS);
            }
            level.scheduleTick(pos, this, TICK_IDLE);
            return;
        }

        if (!PressureAtmosphere.isStable(level, pos)) {
            if (state.getValue(PULSE_PHASE) != 0) {
                level.setBlock(pos, state.setValue(PULSE_PHASE, 0), Block.UPDATE_CLIENTS);
            }
            level.scheduleTick(pos, this, 8);
            return;
        }

        BlockPos conduitPos = findBestConduit(level, pos);
        int pressure = (conduitPos == null) ? 0 : PressureUtil.getConduitPressureOrState(level, conduitPos);
        int rotation = RotationUtil.getRotationLevel(level, pos);
        boolean active = pressure > 0 || rotation > 0;

        int nextPhase = active ? ((state.getValue(PULSE_PHASE) + 1) & 3) : 0;
        if (nextPhase != state.getValue(PULSE_PHASE)) {
            level.setBlock(pos, state.setValue(PULSE_PHASE, nextPhase), Block.UPDATE_CLIENTS);
        }

        int baseInterval = switch (state.getValue(RAIL_MODE)) {
            case SOFT -> 6;
            case NORMAL -> 4;
            case HIGH -> 2;
        };
        int interval = Math.max(1, baseInterval - (rotation / 2));
        if (pressure <= 0 && rotation <= 0) {
            interval = 8;
        }
        level.scheduleTick(pos, this, interval);
    }

    private static BlockPos findBestConduit(Level level, BlockPos pos) {
        BlockPos best = null;
        int bestPressure = 0;

        // Prefer below (expected placement).
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
