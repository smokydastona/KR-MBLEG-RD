package com.kruemblegard.pressurelogic;

import com.kruemblegard.config.ModConfig;
import com.kruemblegard.pressurelogic.capability.IPressureHandler;
import com.kruemblegard.pressurelogic.capability.PressureCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.Nullable;

/**
 * Public, stable entrypoint for interacting with Krümblegård pressure.
 *
 * External mods should generally attach to the pressure network via the
 * `pressure_conduit` block's {@link PressureCapabilities#PRESSURE_HANDLER} capability.
 *
 * Internal code should prefer these helpers instead of casting BlockEntities.
 */
public final class PressureApi {
    private PressureApi() {}

    public static boolean isPressureSystemEnabled() {
        return ModConfig.PRESSURE_SYSTEM_ENABLED.get();
    }

    public static LazyOptional<IPressureHandler> getPressureHandler(Level level, BlockPos pos, @Nullable Direction side) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return LazyOptional.empty();
        }
        return be.getCapability(PressureCapabilities.PRESSURE_HANDLER, side);
    }

    public static int getPressure(Level level, BlockPos pos, @Nullable Direction side) {
        return getPressureHandler(level, pos, side)
            .map(IPressureHandler::getPressure)
            .orElse(PressureConstants.MIN_PRESSURE);
    }

    public static boolean setPressure(Level level, BlockPos pos, @Nullable Direction side, int value) {
        return getPressureHandler(level, pos, side)
            .map(handler -> {
                handler.setPressure(value);
                return true;
            })
            .orElse(false);
    }
}
