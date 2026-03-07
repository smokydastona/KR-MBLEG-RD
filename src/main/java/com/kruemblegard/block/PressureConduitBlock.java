package com.kruemblegard.block;

import com.kruemblegard.blockentity.PressureConduitBlockEntity;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

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
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return BaseEntityBlock.createTickerHelper(type, com.kruemblegard.init.ModBlockEntities.PRESSURE_CONDUIT.get(), PressureConduitBlockEntity::tick);
    }
}
