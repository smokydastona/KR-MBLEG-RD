package com.kruemblegard.block;

import com.kruemblegard.blockentity.HandBellowsBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureFeedback;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class HandBellowsBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final int PUMP_COOLDOWN_TICKS = 8;
    private static final int PUMP_PRESSURE = 10;

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public HandBellowsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandBellowsBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.HAND_BELLOWS.get(), HandBellowsBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.HAND_BELLOWS.get(), HandBellowsBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isCrouching()) {
            return PressureFeedback.tryInspect(state, level, pos, player, hand, hit);
        }

        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof HandBellowsBlockEntity be)) {
            return InteractionResult.CONSUME;
        }

        if (!be.canPump(level)) {
            return InteractionResult.CONSUME;
        }

        BlockPos outputConduit = PressureUtil.resolveInlineConduit(level, pos, state.getValue(FACING));
        if (outputConduit == null) {
            return InteractionResult.CONSUME;
        }

        PressureUtil.addPressure(level, outputConduit, PUMP_PRESSURE);
        be.markPumped(level);

        if (!state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, true), Block.UPDATE_CLIENTS);
        }
        level.scheduleTick(pos, this, PUMP_COOLDOWN_TICKS);
        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 0.7F);
        return InteractionResult.CONSUME;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_CLIENTS);
        }
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