package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

public class VentPistonBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty EXTENSION = IntegerProperty.create("extension", 0, 16);

    public VentPistonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(EXTENSION, 0));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        int target = targetExtension(context.getLevel(), context.getClickedPos());
        BlockState state = this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(EXTENSION, target);

        if (!context.getLevel().isClientSide) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 2);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENSION);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int current = state.getValue(EXTENSION);
        int target = targetExtension(level, pos);

        int next;
        if (target > current) {
            next = current + 1;
        } else if (target < current) {
            next = current - 1;
        } else {
            next = current;
        }

        if (next != current) {
            // When extending, apply a gentle push in the facing direction.
            if (next > current) {
                Direction facing = state.getValue(FACING);
                BlockPos front = pos.relative(facing);
                AABB box = new AABB(front).inflate(0.25);
                double dx = facing.getStepX() * 0.05;
                double dz = facing.getStepZ() * 0.05;
                for (net.minecraft.world.entity.Entity entity : level.getEntities(null, box)) {
                    entity.push(dx, 0.0, dz);
                }
            }

            level.setBlock(pos, state.setValue(EXTENSION, next), 2);
        }

        if (next != target) {
            level.scheduleTick(pos, this, 2);
        } else {
            level.scheduleTick(pos, this, 10);
        }
    }

    private static int targetExtension(Level level, BlockPos pos) {
        int signal = level.getBestNeighborSignal(pos);
        int target = (int) Math.round((signal / 15.0) * 16.0);
        if (target < 0) target = 0;
        if (target > 16) target = 16;
        return target;
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
