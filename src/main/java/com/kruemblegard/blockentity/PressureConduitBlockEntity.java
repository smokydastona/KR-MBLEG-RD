package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureConduitBlock;
import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureConstants;
import com.kruemblegard.pressurelogic.PressureNbt;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.pressurelogic.PressureValue;
import com.kruemblegard.pressurelogic.capability.IPressureHandler;
import com.kruemblegard.pressurelogic.capability.PressureCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PressureConduitBlockEntity extends BlockEntity {
    private final PressureValue pressure = new PressureValue(0);

    private final IPressureHandler pressureHandler = new IPressureHandler() {
        @Override
        public int getPressure() {
            return pressure.get();
        }

        @Override
        public void setPressure(int value) {
            int next = PressureConstants.clampPressure(value);
            if (next != pressure.get()) {
                pressure.set(next);
                setChanged();
            }
        }

        @Override
        public int getMaxPressure() {
            return PressureConstants.MAX_PRESSURE;
        }
    };

    private LazyOptional<IPressureHandler> pressureCap = LazyOptional.of(() -> pressureHandler);

    private static boolean loggedPressureDisabled = false;
    private static long lastPressureTickErrorLogTime = Long.MIN_VALUE;

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

        if (!ModConfig.PRESSURE_SYSTEM_ENABLED.get()) {
            if (!loggedPressureDisabled && ModConfig.PRESSURE_DEBUG_LOGGING.get()) {
                loggedPressureDisabled = true;
                Kruemblegard.LOGGER.info("Pressure-Logic disabled via config; conduits will not simulate.");
            }
            return;
        }

        // Keep things cheap and stable: update on an interval, staggered by position.
        long t = level.getGameTime();
        int interval = Math.max(1, ModConfig.PRESSURE_TICK_INTERVAL_TICKS.get());
        if (((t + pos.asLong()) % interval) != 0) {
            // Still ensure the blockstate representation stays in sync occasionally.
            if (((t + pos.asLong()) % 20) == 0) {
                syncState(level, pos, state, be);
            }
            return;
        }

        try {
            int current = be.pressure.get();
            int target = PressureUtil.sampleNeighborAveragePressure(level, pos);

            // Move toward the neighborhood average with a limited step (prevents jitter/explosions).
            int next = PressureUtil.approach(current, target, 4);

            if (next != current) {
                be.pressure.set(next);
                be.setChanged();
            }

            syncState(level, pos, state, be);
        } catch (Throwable ex) {
            // Rate-limit to avoid log spam if something goes sideways in a large network.
            if (t - lastPressureTickErrorLogTime >= 200) {
                lastPressureTickErrorLogTime = t;
                if (ModConfig.PRESSURE_DEBUG_LOGGING.get()) {
                    Kruemblegard.LOGGER.warn("Pressure conduit tick failed at {} (dim={})", pos, level.dimension().location(), ex);
                } else {
                    Kruemblegard.LOGGER.warn("Pressure conduit tick failed at {} (dim={})", pos, level.dimension().location());
                }
            }
        }
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

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        pressureCap.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        pressureCap = LazyOptional.of(() -> pressureHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == PressureCapabilities.PRESSURE_HANDLER) {
            return pressureCap.cast();
        }
        return super.getCapability(cap, side);
    }
}
