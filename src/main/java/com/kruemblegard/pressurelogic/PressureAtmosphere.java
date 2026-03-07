package com.kruemblegard.pressurelogic;

import com.kruemblegard.block.AtmosphericCompressorBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class PressureAtmosphere {
    private PressureAtmosphere() {}

    private static final ResourceLocation WAYFALL_DIMENSION = new ResourceLocation("kruemblegard", "wayfall");

    /**
     * Whether Pressure-Logic machines should be considered "in stable air" at this position.
     *
     * Spec intent: Wayfall has native stability; in other dimensions, an Atmospheric Compressor
     * provides a 5x5x5 stable bubble.
     */
    public static boolean isStable(Level level, BlockPos pos) {
        if (level.dimension().location().equals(WAYFALL_DIMENSION)) {
            return true;
        }

        // 5x5x5 scan (radius 2). Cheap, deterministic, and matches the design doc.
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState state = level.getBlockState(cursor);
                    if (state.getBlock() instanceof AtmosphericCompressorBlock) {
                        // Any non-zero stability grants a stable bubble.
                        if (state.getValue(AtmosphericCompressorBlock.STABILITY_LEVEL) > 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
