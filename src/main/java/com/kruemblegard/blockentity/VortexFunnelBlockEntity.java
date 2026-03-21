package com.kruemblegard.blockentity;

import com.kruemblegard.block.VortexFunnelBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VortexFunnelBlockEntity extends CephalariMachineBlockEntity {
    private float intensity;

    public VortexFunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VORTEX_FUNNEL.get(), pos, state);
        this.intensity = 0.0F;
    }

    public float getIntensity() {
        return intensity;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, VortexFunnelBlockEntity be) {
        float target = 0.0F;
        if (state.getValue(VortexFunnelBlock.POWERED)) {
            int best = 0;
            for (Direction dir : Direction.values()) {
                best = Math.max(best, PressureUtil.getConduitPressureOrState(level, pos.relative(dir)));
            }
            target = PressureUtil.pressureToLevel(best) / 5.0F;
        }

        be.intensity = Mth.lerp(0.20F, be.intensity, target);
        be.stepAnim(be.intensity <= 0.0F ? 0.0F : (0.01F + be.intensity * 0.04F));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VortexFunnelBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(VortexFunnelBlock.POWERED)) {
            be.recordActiveSample(40);
        }
    }
}
