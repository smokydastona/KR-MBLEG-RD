package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureKilnBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.pressurelogic.PressureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureKilnBlockEntity extends CephalariMachineBlockEntity {
    private float heat;

    public PressureKilnBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_KILN.get(), pos, state);
        this.heat = 0.0F;
    }

    public float getHeat() {
        return heat;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureKilnBlockEntity be) {
        float target = 0.0F;
        if (state.getValue(PressureKilnBlock.POWERED)) {
            int best = 0;
            for (Direction dir : Direction.values()) {
                best = Math.max(best, PressureUtil.getConduitPressureOrState(level, pos.relative(dir)));
            }
            target = PressureUtil.pressureToLevel(best) / 5.0F;
        }

        float modeMul = switch (state.getValue(PressureKilnBlock.KILN_MODE)) {
            case LOW -> 0.8F;
            case OVERPRESSURE -> 1.3F;
            default -> 1.0F;
        };

        be.heat = Mth.lerp(0.15F, be.heat, target);
        be.stepAnim(be.heat <= 0.0F ? 0.0F : (0.01F + be.heat * 0.03F) * modeMul);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureKilnBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 80) != 0) {
            return;
        }
        if (state.getValue(PressureKilnBlock.POWERED)) {
            be.recordActiveSample(80);
        }
    }
}
