package com.kruemblegard.util;

import net.minecraft.world.phys.Vec3;

public final class DistanceCulling {
    private DistanceCulling() {}

    public static boolean isWithinDistance(Vec3 viewerPos, double x, double y, double z, double maxDistanceBlocks, double verticalStretch) {
        if (viewerPos == null) {
            return false;
        }
        if (maxDistanceBlocks <= 0.0D) {
            return false;
        }
        if (verticalStretch <= 0.0D) {
            return false;
        }

        double dx = viewerPos.x - x;
        double dy = (viewerPos.y - y) * verticalStretch;
        double dz = viewerPos.z - z;

        double maxSqr = maxDistanceBlocks * maxDistanceBlocks;
        return dx * dx + dy * dy + dz * dz <= maxSqr;
    }
}
