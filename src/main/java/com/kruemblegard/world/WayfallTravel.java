package com.kruemblegard.world;

import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.util.ITeleporter;

public final class WayfallTravel {
    private WayfallTravel() {}

    /** Marker used to distinguish our portal travel from commands like /tp. */
    public static final String TAG_WAYFALL_PORTAL_ENTRY = "kruemblegard_wayfall_portal_entry";

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

        // Mark that this dimension change originated from the Wayfall portal.
        // Used to avoid overriding /tp destinations in the entry safety handler.
        if (entity instanceof ServerPlayer player) {
            player.getPersistentData().putBoolean(TAG_WAYFALL_PORTAL_ENTRY, true);
        }

        entity.setPortalCooldown();

        Entity result = entity.changeDimension(target, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw,
                                      java.util.function.Function<Boolean, Entity> repositionEntity) {
                // End-portal style: always enter Wayfall at its spawn landing, never at the source coords.
                Entity placed = repositionEntity.apply(false);

                BlockPos landing;
                if (placed instanceof net.minecraft.server.level.ServerPlayer) {
                    // Players are allowed to bootstrap Wayfall initialization, but do NOT do heavy work
                    // synchronously here. The safety event queues init + final teleport once ready.
                    if (WayfallSpawnIslandSavedData.get(destWorld).isPlaced()) {
                        landing = WayfallSpawnPlatform.ensureSpawnLanding(destWorld);
                    } else {
                        // Temporary holding spot: keep them near the anchor and prevent falling until
                        // the queued safety teleport moves them onto the island.
                        BlockPos anchor = WayfallSpawnPlatform.getSpawnIslandAnchor();
                        landing = anchor.above(8);
                        placed.setNoGravity(true);
                    }
                } else {
                    // Prevent mobs/items from triggering heavy initialization while a player is elsewhere.
                    // They can still travel once Wayfall has been initialized by a player.
                    landing = WayfallSpawnPlatform.getSpawnLandingIfPlaced(destWorld);
                    if (landing == null) {
                        return null;
                    }
                }
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
