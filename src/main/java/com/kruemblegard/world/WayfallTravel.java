package com.kruemblegard.world;

import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.util.ITeleporter;

public final class WayfallTravel {
    private WayfallTravel() {}

    public static ServerLevel getWayfallTargetLevel(ServerLevel fromLevel) {
        if (fromLevel.getServer() == null) {
            return null;
        }

        // One-way: never re-trigger inside Wayfall.
        if (fromLevel.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return null;
        }

        return fromLevel.getServer().getLevel(ModWorldgenKeys.Levels.WAYFALL);
    }

    public static boolean teleportToWayfallSpawnLanding(Entity entity, ServerLevel fromLevel) {
        if (entity == null || fromLevel == null) {
            return false;
        }

        // Match vanilla portal constraints.
        if (entity.isPassenger() || entity.isVehicle() || !entity.canChangeDimensions()) {
            return false;
        }

        if (entity.isOnPortalCooldown()) {
            return false;
        }

        ServerLevel target = getWayfallTargetLevel(fromLevel);
        if (target == null) {
            return false;
        }

        entity.setPortalCooldown();

        Entity result = entity.changeDimension(target, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw,
                                      java.util.function.Function<Boolean, Entity> repositionEntity) {
                // End-portal style: always enter Wayfall at its spawn landing, never at the source coords.
                Entity placed = repositionEntity.apply(false);

                BlockPos landing = WayfallSpawnPlatform.ensureSpawnLanding(destWorld);
                double x = landing.getX() + 0.5D;
                double y = landing.getY();
                double z = landing.getZ() + 0.5D;

                placed.fallDistance = 0;
                placed.setDeltaMovement(Vec3.ZERO);

                // Use ServerPlayer teleport for correct client synchronization.
                if (placed instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    serverPlayer.teleportTo(destWorld, x, y, z, serverPlayer.getYRot(), serverPlayer.getXRot());
                } else {
                    placed.moveTo(x, y, z, placed.getYRot(), placed.getXRot());
                }
                return placed;
            }
        });

        return result != null;
    }
}
