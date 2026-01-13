package com.kruemblegard.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;

public final class PaleweftTint {
    private PaleweftTint() {}

    public static int stitchedGrassColor(BlockAndTintGetter level, BlockPos pos) {
        if (level == null || pos == null) {
            return GrassColor.getDefaultColor();
        }

        // "Stitching": sample nearby grass colors deterministically per-position.
        long seed = Mth.getSeed(pos.getX(), 0, pos.getZ());
        int ox1 = (int) (seed & 3L) - 1;
        int oz1 = (int) ((seed >> 2) & 3L) - 1;
        int ox2 = (int) ((seed >> 4) & 3L) - 1;
        int oz2 = (int) ((seed >> 6) & 3L) - 1;

        int c0 = BiomeColors.getAverageGrassColor(level, pos);
        int c1 = BiomeColors.getAverageGrassColor(level, pos.offset(ox1, 0, oz1));
        int c2 = BiomeColors.getAverageGrassColor(level, pos.offset(ox2, 0, oz2));
        int c3 = BiomeColors.getAverageGrassColor(level, pos.offset(ox1 + ox2, 0, oz1 + oz2));

        return averageRgb(c0, c1, c2, c3);
    }

    private static int averageRgb(int... colors) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int color : colors) {
            r += (color >> 16) & 0xFF;
            g += (color >> 8) & 0xFF;
            b += color & 0xFF;
        }
        int n = colors.length;
        r /= n;
        g /= n;
        b /= n;
        return (r << 16) | (g << 8) | b;
    }
}
