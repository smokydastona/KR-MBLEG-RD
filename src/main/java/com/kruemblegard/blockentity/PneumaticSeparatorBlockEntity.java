package com.kruemblegard.blockentity;

import com.kruemblegard.block.PneumaticSeparatorBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PneumaticSeparatorBlockEntity extends CephalariMachineBlockEntity {
    public PneumaticSeparatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PNEUMATIC_SEPARATOR.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PneumaticSeparatorBlockEntity be) {
        boolean powered = state.getValue(PneumaticSeparatorBlock.POWERED);
        float modeMul = switch (state.getValue(PneumaticSeparatorBlock.SEPARATOR_MODE)) {
            case TRI -> 1.15F;
            case DENSITY -> 1.25F;
            default -> 1.0F;
        };
        be.stepAnim(powered ? (0.02F * modeMul) : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PneumaticSeparatorBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 80) != 0) {
            return;
        }
        if (state.getValue(PneumaticSeparatorBlock.POWERED)) {
            be.recordActiveSample(80);
        }
    }
}
