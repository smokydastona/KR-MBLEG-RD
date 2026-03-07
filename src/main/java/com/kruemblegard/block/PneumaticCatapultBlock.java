package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

public class PneumaticCatapultBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
    public static final IntegerProperty CHARGE_LEVEL = IntegerProperty.create("charge_level", 0, 3);

    public PneumaticCatapultBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(CHARGE_LEVEL, 0));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        int signal = context.getLevel().getBestNeighborSignal(context.getClickedPos());
        boolean powered = signal > 0;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, powered)
                .setValue(CHARGE_LEVEL, chargeFromSignal(signal));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, CHARGE_LEVEL);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide) {
            return;
        }

        int signal = level.getBestNeighborSignal(pos);
        boolean poweredNow = signal > 0;
        boolean wasPowered = state.getValue(POWERED);
        int nextCharge = chargeFromSignal(signal);

        BlockState next = state
                .setValue(POWERED, poweredNow)
                .setValue(CHARGE_LEVEL, nextCharge);

        if (next != state) {
            level.setBlock(pos, next, Block.UPDATE_CLIENTS);
        }

        if (poweredNow && !wasPowered) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) {
            return;
        }

        int charge = state.getValue(CHARGE_LEVEL);
        if (charge <= 0) {
            return;
        }

        Direction facing = state.getValue(FACING);
        double horiz = 0.6 + 0.3 * charge;
        double up = 0.5 + 0.2 * charge;

        AABB box = new AABB(pos).inflate(0.5, 1.0, 0.5).move(0, 0.1, 0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, entity -> entity.isAlive() && !entity.isSpectator())) {
            e.setDeltaMovement(e.getDeltaMovement().add(facing.getStepX() * horiz, up, facing.getStepZ() * horiz));
            e.hurtMarked = true;
        }

        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, box, entity -> entity.isAlive() && !entity.getItem().isEmpty())) {
            e.setDeltaMovement(e.getDeltaMovement().add(facing.getStepX() * horiz, up, facing.getStepZ() * horiz));
            e.hurtMarked = true;
        }

        level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.6F, 1.2F);
    }

    private static int chargeFromSignal(int signal) {
        if (signal <= 0) return 0;
        if (signal <= 5) return 1;
        if (signal <= 10) return 2;
        return 3;
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
