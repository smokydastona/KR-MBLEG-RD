package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jetbrains.annotations.Nullable;

public class PressureRailBlock extends HorizontalDirectionalBlock {
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

    public PressureRailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(RAIL_MODE, RailMode.NORMAL));
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
        builder.add(FACING, POWERED, RAIL_MODE);
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

        Direction facing = state.getValue(FACING);
        double speed = switch (state.getValue(RAIL_MODE)) {
            case SOFT -> 0.06;
            case NORMAL -> 0.12;
            case HIGH -> 0.20;
        };

        entity.setDeltaMovement(
                entity.getDeltaMovement().add(facing.getStepX() * speed, 0.0, facing.getStepZ() * speed)
        );
        entity.hurtMarked = true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!state.getValue(POWERED)) {
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
