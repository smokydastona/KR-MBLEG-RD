package com.kruemblegard.util;

import com.kruemblegard.config.ClientConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central policy for Krümblegård-only client cosmetics.
 *
 * This is intentionally written using common (non-client-only) Minecraft types so it can be referenced
 * from shared entity code without crashing dedicated servers.
 */
public final class CosmeticEffectsPolicy {
    private static final List<CosmeticSpawnHook> HOOKS = new CopyOnWriteArrayList<>();

    private CosmeticEffectsPolicy() {
    }

    public static void registerHook(CosmeticSpawnHook hook) {
        if (hook == null) {
            return;
        }
        HOOKS.add(hook);
    }

    public static boolean shouldSpawnCosmeticParticles(Level level, Entity effectSource, int particleCost) {
        if (level == null || effectSource == null) {
            return false;
        }
        if (!level.isClientSide) {
            return false;
        }
        if (particleCost <= 0) {
            return false;
        }

        // Sodium-like "budget first": if we're already out of budget, skip doing any other work.
        if (ClientConfig.ENABLE_COSMETIC_PARTICLE_BUDGET.get()
                && !CosmeticParticleBudget.canAfford(level, particleCost)) {
            return false;
        }

        boolean needsViewer = ClientConfig.ENABLE_DISTANCE_CULLED_COSMETICS.get()
                || ClientConfig.ENABLE_VIEW_CONE_CULLED_COSMETICS.get()
                || !HOOKS.isEmpty();

        Player viewer = null;
        if (needsViewer) {
            double maxDistance = getMaxCosmeticDistanceBlocks();
            viewer = level.getNearestPlayer(effectSource, maxDistance);
            if (viewer == null) {
                return false;
            }

            Vec3 viewerPos = viewer.getEyePosition();
            Vec3 effectPos = effectSource.position();

            if (ClientConfig.ENABLE_DISTANCE_CULLED_COSMETICS.get()) {
                if (!DistanceCulling.isWithinDistance(
                        viewerPos,
                        effectPos.x,
                        effectPos.y,
                        effectPos.z,
                        maxDistance,
                        ClientConfig.COSMETIC_VERTICAL_STRETCH.get()
                )) {
                    return false;
                }
            }

            if (ClientConfig.ENABLE_VIEW_CONE_CULLED_COSMETICS.get()) {
                if (!isWithinViewCone(viewer, effectPos,
                        ClientConfig.COSMETIC_VIEW_CONE_HALF_ANGLE_DEGREES.get(),
                        ClientConfig.COSMETIC_VIEW_CONE_MARGIN_DEGREES.get())) {
                    return false;
                }
            }

            for (CosmeticSpawnHook hook : HOOKS) {
                if (!hook.allow(level, viewer, effectSource)) {
                    return false;
                }
            }
        }

        if (ClientConfig.ENABLE_COSMETIC_PARTICLE_BUDGET.get()) {
            CosmeticParticleBudget.consume(level, particleCost);
        }

        return true;
    }

    private static double getMaxCosmeticDistanceBlocks() {
        double base = ClientConfig.COSMETIC_CULL_DISTANCE_BLOCKS.get();
        double epsilon = ClientConfig.COSMETIC_CULL_EPSILON_BLOCKS.get();

        if (checksEnabled()) {
            if (base < 0.0D || epsilon < 0.0D) {
                throw new IllegalArgumentException("Cosmetic distance must be non-negative");
            }
        }

        return Math.max(0.0D, base + epsilon);
    }

    private static boolean isWithinViewCone(Player viewer, Vec3 targetPos, double halfAngleDeg, double marginDeg) {
        if (viewer == null || targetPos == null) {
            return false;
        }

        double halfAngle = Math.min(180.0D, Math.max(0.0D, halfAngleDeg + marginDeg));
        if (halfAngle >= 179.999D) {
            return true;
        }

        Vec3 from = viewer.getEyePosition();
        Vec3 toTarget = targetPos.subtract(from);

        double len2 = toTarget.lengthSqr();
        if (len2 <= 0.0001D) {
            return true;
        }

        // Conservative: don't cull nearby effects just because the cone math is coarse.
        if (len2 < (2.0D * 2.0D)) {
            return true;
        }

        Vec3 look = viewer.getLookAngle();
        double lookLen2 = look.lengthSqr();
        if (lookLen2 <= 0.0001D) {
            return true;
        }

        Vec3 dir = look.scale(1.0D / Math.sqrt(lookLen2));
        Vec3 to = toTarget.scale(1.0D / Math.sqrt(len2));

        double dot = dir.dot(to);
        double cos = Math.cos(Math.toRadians(halfAngle));

        return dot >= cos;
    }

    static boolean checksEnabled() {
        return Boolean.getBoolean("kruemblegard.checks") || ClientConfig.ENABLE_RUNTIME_CHECKS.get();
    }

    public interface CosmeticSpawnHook {
        boolean allow(Level level, Player viewer, Entity effectSource);
    }
}
