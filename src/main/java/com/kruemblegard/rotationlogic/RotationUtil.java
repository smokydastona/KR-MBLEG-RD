package com.kruemblegard.rotationlogic;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public final class RotationUtil {
    private RotationUtil() {}

    public static int clampLevel(int level) {
        return Mth.clamp(level, 0, 5);
    }

    public static int getRotationLevel(Level level, BlockPos consumerPos) {
        return 0;
    }
}
