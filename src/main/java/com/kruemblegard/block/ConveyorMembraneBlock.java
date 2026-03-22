package com.kruemblegard.block;

import com.kruemblegard.blockentity.ConveyorMembraneBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureAtmosphere;
import com.kruemblegard.pressurelogic.PressureFeedback;
import com.kruemblegard.rotationlogic.RotationUtil;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConveyorMembraneBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final IntegerProperty PULSE_PHASE = IntegerProperty.create("pulse_phase", 0, 3);

    public ConveyorMembraneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PULSE_PHASE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PULSE_PHASE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConveyorMembraneBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.CONVEYOR_MEMBRANE.get(), ConveyorMembraneBlockEntity::clientTick);
        }
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.CONVEYOR_MEMBRANE.get(), ConveyorMembraneBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return PressureFeedback.tryInspect(state, level, pos, player, hand, hit);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        PressureFeedback.animateWorking(state, level, pos, random);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        int rotation = (PressureAtmosphere.isStable(level, pos)) ? RotationUtil.getRotationLevel(level, pos) : 0;

        // Drive the pulse animation faster when rotation is stronger.
        int phase = state.getValue(PULSE_PHASE);
        int nextPhase = phase;
        if (rotation > 0) {
            int steps = Math.max(1, rotation / 2);
            nextPhase = (phase + steps) & 3;
        } else {
            nextPhase = 0;
        }

        if (nextPhase != phase) {
            level.setBlock(pos, state.setValue(PULSE_PHASE, nextPhase), 2);
            state = level.getBlockState(pos);
        }

        if (rotation > 0) {
            Direction out = state.getValue(FACING);
            double speed = 0.04 + (rotation * 0.03); // 0.07..0.19

            AABB box = new AABB(pos).inflate(0.55, 0.2, 0.55).move(0, 1.0, 0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.getItem().isEmpty());
            for (ItemEntity itemEntity : items) {
                double dx = out.getStepX() * speed;
                double dz = out.getStepZ() * speed;
                itemEntity.setDeltaMovement(dx, Math.max(itemEntity.getDeltaMovement().y, 0.02), dz);
                itemEntity.hurtMarked = true;
            }
        }

        level.scheduleTick(pos, this, 5);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
