package com.kruemblegard.util;

import com.kruemblegard.config.ClientConfig;
import net.minecraft.world.level.Level;

final class CosmeticParticleBudget {
    private static long lastGameTime = Long.MIN_VALUE;
    private static int usedThisTick = 0;

    private CosmeticParticleBudget() {
    }

    static boolean canAfford(Level level, int cost) {
        if (cost <= 0) {
            return true;
        }
        if (level == null) {
            return false;
        }

        int cap = ClientConfig.COSMETIC_PARTICLE_BUDGET_PER_TICK.get();
        if (cap <= 0) {
            return true; // 0 = unlimited
        }

        resetIfNewTick(level);

        // Avoid overflow even if someone configures a huge cap.
        long next = (long) usedThisTick + (long) cost;
        return next <= (long) cap;
    }

    static void consume(Level level, int cost) {
        if (cost <= 0) {
            return;
        }
        if (level == null) {
            return;
        }

        int cap = ClientConfig.COSMETIC_PARTICLE_BUDGET_PER_TICK.get();
        if (cap <= 0) {
            return; // 0 = unlimited
        }

        resetIfNewTick(level);

        if (CosmeticEffectsPolicy.checksEnabled() && !canAfford(level, cost)) {
            throw new IllegalStateException("Cosmetic particle budget exceeded: cost=" + cost + ", used=" + usedThisTick + ", cap=" + cap);
        }

        usedThisTick = Math.min(cap, usedThisTick + cost);
    }

    private static void resetIfNewTick(Level level) {
        long time = level.getGameTime();
        if (time != lastGameTime) {
            lastGameTime = time;
            usedThisTick = 0;
        }
    }
}
