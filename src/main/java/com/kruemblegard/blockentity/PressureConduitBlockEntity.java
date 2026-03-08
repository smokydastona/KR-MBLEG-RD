package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureConduitBlock;
import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureConstants;
import com.kruemblegard.pressurelogic.PressureNbt;
import com.kruemblegard.pressurelogic.PressurePortMode;
import com.kruemblegard.pressurelogic.PressureUtil;
import com.kruemblegard.pressurelogic.PressureValue;
import com.kruemblegard.pressurelogic.capability.IPressureHandler;
import com.kruemblegard.pressurelogic.capability.PressureCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PressureConduitBlockEntity extends BlockEntity {
    private static final String TAG_PORT_MODES = "PortModes";

    private final PressureValue pressure = new PressureValue(0);

    // Indexed by Direction.get3DDataValue() (0..5). Defaults to BOTH.
    private final int[] portModes = new int[] {
        PressurePortMode.BOTH.ordinal(),
        PressurePortMode.BOTH.ordinal(),
        PressurePortMode.BOTH.ordinal(),
        PressurePortMode.BOTH.ordinal(),
        PressurePortMode.BOTH.ordinal(),
        PressurePortMode.BOTH.ordinal()
    };

    private final IPressureHandler pressureHandler = new IPressureHandler() {
        @Override
        public int getPressure() {
            return pressure.get();
        }

        @Override
        public void setPressure(int value) {
            int max = getMaxPressure();
            int next = Mth.clamp(value, PressureConstants.MIN_PRESSURE, max);
            if (next != pressure.get()) {
                pressure.set(next);
                setChanged();
            }
        }

        @Override
        public int getMaxPressure() {
            return Math.max(1, Math.min(PressureConstants.MAX_PRESSURE, ModConfig.PRESSURE_CONDUIT_MAX_PRESSURE.get()));
        }
    };

    private LazyOptional<IPressureHandler> pressureCap = LazyOptional.of(() -> pressureHandler);

    @SuppressWarnings("unchecked")
    private LazyOptional<IPressureHandler>[] sidedPressureCaps = (LazyOptional<IPressureHandler>[]) new LazyOptional[Direction.values().length];

    private static boolean loggedPressureDisabled = false;
    private static long lastPressureTickErrorLogTime = Long.MIN_VALUE;

    public PressureConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_CONDUIT.get(), pos, state);
    }

    public PressureValue getPressure() {
        return pressure;
    }

    public PressurePortMode getPortMode(Direction side) {
        int idx = side.get3DDataValue();
        int ord = (idx >= 0 && idx < portModes.length) ? portModes[idx] : PressurePortMode.BOTH.ordinal();
        PressurePortMode[] values = PressurePortMode.values();
        if (ord < 0 || ord >= values.length) {
            return PressurePortMode.BOTH;
        }
        return values[ord];
    }

    public void setPortMode(Direction side, PressurePortMode mode) {
        int idx = side.get3DDataValue();
        if (idx < 0 || idx >= portModes.length) {
            return;
        }

        int next = mode.ordinal();
        if (portModes[idx] != next) {
            portModes[idx] = next;
            setChanged();
        }
    }

    public PressurePortMode cyclePortMode(Direction side) {
        PressurePortMode next = getPortMode(side).next();
        setPortMode(side, next);
        return next;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        PressureNbt.save(tag, pressure);

        tag.putIntArray(TAG_PORT_MODES, portModes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        PressureNbt.loadInto(tag, pressure);

        if (tag.contains(TAG_PORT_MODES)) {
            int[] arr = tag.getIntArray(TAG_PORT_MODES);
            if (arr.length == portModes.length) {
                System.arraycopy(arr, 0, portModes, 0, portModes.length);
            }
        }
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
            int max = be.pressureHandler.getMaxPressure();
            next = Mth.clamp(next, PressureConstants.MIN_PRESSURE, max);

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
        for (LazyOptional<IPressureHandler> cap : sidedPressureCaps) {
            if (cap != null) {
                cap.invalidate();
            }
        }
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        pressureCap = LazyOptional.of(() -> pressureHandler);
        for (Direction dir : Direction.values()) {
            sidedPressureCaps[dir.get3DDataValue()] = LazyOptional.of(() -> new IPressureHandler() {
                @Override
                public int getPressure() {
                    return pressureHandler.getPressure();
                }

                @Override
                public void setPressure(int value) {
                    if (!ModConfig.PRESSURE_SIDED_PORTS_ENABLED.get()) {
                        pressureHandler.setPressure(value);
                        return;
                    }

                    PressurePortMode mode = getPortMode(dir);
                    if (mode == PressurePortMode.INPUT || mode == PressurePortMode.BOTH) {
                        pressureHandler.setPressure(value);
                    }
                }

                @Override
                public int getMaxPressure() {
                    return pressureHandler.getMaxPressure();
                }
            });
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == PressureCapabilities.PRESSURE_HANDLER) {
            if (side == null) {
                return pressureCap.cast();
            }

            if (ModConfig.PRESSURE_SIDED_PORTS_ENABLED.get() && getPortMode(side) == PressurePortMode.DISABLED) {
                return LazyOptional.empty();
            }

            LazyOptional<IPressureHandler> sided = sidedPressureCaps[side.get3DDataValue()];
            if (sided != null) {
                return sided.cast();
            }
            return pressureCap.cast();
        }
        return super.getCapability(cap, side);
    }
}
