package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureConduitBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureNbt;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.pressurelogic.PressureValue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

public class PressureConduitBlockEntity extends BlockEntity {
    private final PressureValue pressure = new PressureValue(0);

    public PressureConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_CONDUIT.get(), pos, state);
    }

    public PressureValue getPressure() {
        return pressure;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        PressureNbt.save(tag, pressure);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        PressureNbt.loadInto(tag, pressure);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PressureConduitBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        // Keep things cheap and stable: only update every 5 ticks, staggered by position.
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 5) != 0) {
            // Still ensure the blockstate representation stays in sync occasionally.
            if (((t + pos.asLong()) % 20) == 0) {
                syncState(level, pos, state, be);
            }
            return;
        }

        int current = be.pressure.get();
        int target = PressureUtil.sampleNeighborAveragePressure(level, pos);

        // Move toward the neighborhood average with a limited step (prevents jitter/explosions).
        int next = PressureUtil.approach(current, target, 4);

        if (next != current) {
            be.pressure.set(next);
            be.setChanged();
        }

        syncState(level, pos, state, be);
    }

    private static void syncState(Level level, BlockPos pos, BlockState state, PressureConduitBlockEntity be) {
        if (!(state.getBlock() instanceof PressureConduitBlock)) {
            return;
        }
        int level5 = PressureUtil.pressureToLevel(be.pressure.get());
        int currentLevel5 = state.getValue(PressureConduitBlock.PRESSURE_LEVEL);
        if (level5 != currentLevel5) {
            level.setBlock(pos, state.setValue(PressureConduitBlock.PRESSURE_LEVEL, level5), 2);
        }
    }
}
