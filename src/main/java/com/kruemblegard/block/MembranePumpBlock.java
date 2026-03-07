package com.kruemblegard.block;

import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class MembranePumpBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty PULSE_RATE = IntegerProperty.create("pulse_rate", 0, 5);

    public MembranePumpBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(PULSE_RATE, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        BlockState state = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(PULSE_RATE, powered ? samplePulseRate(context.getLevel(), context.getClickedPos()) : 0);

        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 10);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, PULSE_RATE);
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
        if (poweredNow != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, poweredNow), Block.UPDATE_CLIENTS);
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
        if (!state.getValue(POWERED)) {
            if (state.getValue(PULSE_RATE) != 0) {
                level.setBlock(pos, state.setValue(PULSE_RATE, 0), 2);
            }
            level.scheduleTick(pos, this, 10);
            return;
        }

        int current = state.getValue(PULSE_RATE);
        int target = samplePulseRate(level, pos);

        int next;
        if (target > current) {
            next = current + 1;
        } else if (target < current) {
            next = current - 1;
        } else {
            next = current;
        }

        if (next != current) {
            level.setBlock(pos, state.setValue(PULSE_RATE, next), 2);
        }

        // Generate pressure into the network when the atmosphere is stable.
        if (PressureAtmosphere.isStable(level, pos)) {
            Direction facing = state.getValue(FACING);
            BlockPos outPos = pos.relative(facing);

            // Low, steady generation; scales with pulse rate.
            int delta = 2 + (next * 3);
            PressureUtil.addPressure(level, outPos, delta);
        }

        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!state.getValue(POWERED)) {
            return;
        }

        // Higher pulse rates produce slightly more frequent vent particles.
        int pulseRate = state.getValue(PULSE_RATE);
        int chance = Math.max(1, 6 - pulseRate);
        if (random.nextInt(chance) != 0) {
            return;
        }

        Direction facing = state.getValue(FACING);
        double x = pos.getX() + 0.5 + (facing.getStepX() * 0.35);
        double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double z = pos.getZ() + 0.5 + (facing.getStepZ() * 0.35);

        double vx = facing.getStepX() * 0.02;
        double vy = 0.01;
        double vz = facing.getStepZ() * 0.02;

        level.addParticle(ParticleTypes.CLOUD, x, y, z, vx, vy, vz);
    }

    private static int samplePulseRate(Level level, BlockPos pos) {
        int bestPressure = 0;

        // Prefer nearby conduit continuous pressure.
        for (Direction dir : Direction.values()) {
            bestPressure = Math.max(bestPressure, PressureUtil.getConduitPressureOrState(level, pos.relative(dir)));
        }

        int bestLevel = PressureUtil.pressureToLevel(bestPressure);

        // Fall back to redstone strength as scaffolding.
        int signal = level.getBestNeighborSignal(pos);
        bestLevel = Math.max(bestLevel, (int) Math.round((signal / 15.0) * 5.0));

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
