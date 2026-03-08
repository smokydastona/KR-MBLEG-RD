package com.kruemblegard.block;

import com.kruemblegard.blockentity.PressureConduitBlockEntity;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.pressurelogic.PressurePortMode;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.pressurelogic.network.PressureNetworkManager;
import com.kruemblegard.pressurelogic.network.PressureNetworkSnapshot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class PressureConduitBlock extends BaseEntityBlock {
    public static final IntegerProperty PRESSURE_LEVEL = IntegerProperty.create("pressure_level", 0, 5);

    public PressureConduitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PRESSURE_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PRESSURE_LEVEL);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        int avgPressure = PressureUtil.sampleNeighborAveragePressure(context.getLevel(), context.getClickedPos());
        int level5 = PressureUtil.pressureToLevel(avgPressure);
        return this.defaultBlockState().setValue(PRESSURE_LEVEL, level5);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PressureConduitBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof PressureConduitBlockEntity be)) {
            return InteractionResult.PASS;
        }

        if (player.isCrouching()) {
            if (!ModConfig.PRESSURE_SIDED_PORTS_ENABLED.get()) {
                player.displayClientMessage(Component.literal("Sided pressure ports are disabled (config: pressureSidedPortModesEnabled)."), true);
                return InteractionResult.CONSUME;
            }

            Direction side = hit.getDirection();
            PressurePortMode next = be.cyclePortMode(side);
            player.displayClientMessage(Component.literal("Pressure port " + side.getName() + ": " + next), true);
            return InteractionResult.CONSUME;
        }

        if (ModConfig.PRESSURE_DEBUG_INSPECT.get()) {
            PressureNetworkSnapshot snap = PressureNetworkManager.computeSnapshot(level, pos);
            int p = be.getPressure().get();
            player.displayClientMessage(
                Component.literal(
                    "PressureNet id=" + Long.toHexString(snap.networkId()) +
                    " nodes=" + snap.nodeCount() +
                    " p=" + p +
                    " avg=" + snap.avgPressure() +
                    " min=" + snap.minPressure() +
                    " max=" + snap.maxPressure()
                ),
                false
            );
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PressureNetworkManager.markDirtyAround(serverLevel, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!level.isClientSide && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PressureNetworkManager.markDirtyAround(serverLevel, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.isClientSide && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PressureNetworkManager.markDirtyAround(serverLevel, pos);
        }
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }

        if (ModConfig.PRESSURE_NETWORK_MANAGER_ENABLED.get() && ModConfig.PRESSURE_NETWORK_TICKING_ENABLED.get()) {
            // Network ticking mode: simulation runs via PressureNetworkManager.
            return null;
        }

        return BaseEntityBlock.createTickerHelper(type, com.kruemblegard.init.ModBlockEntities.PRESSURE_CONDUIT.get(), PressureConduitBlockEntity::tick);
    }
}
