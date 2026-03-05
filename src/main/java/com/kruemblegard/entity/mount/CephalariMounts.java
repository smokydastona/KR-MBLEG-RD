package com.kruemblegard.entity.mount;

import com.kruemblegard.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public final class CephalariMounts {

    private CephalariMounts() {}

    public static final String NBT_MOUNT_ID = "KruemblegardCephalariMountId";

    public static String getOrAssignMountId(LivingEntity rider) {
        String existing = rider.getPersistentData().getString(NBT_MOUNT_ID);
        if (!existing.isBlank()) {
            return existing;
        }

        String assigned = getRandomMountId(rider.level() instanceof ServerLevel serverLevel ? serverLevel : null, rider.getRandom().nextInt());
        rider.getPersistentData().putString(NBT_MOUNT_ID, assigned);
        return assigned;
    }

    public static void setMountId(LivingEntity rider, String mountId) {
        rider.getPersistentData().putString(NBT_MOUNT_ID, normalize(mountId));
    }

    public static @Nullable String getMountId(LivingEntity rider) {
        String id = rider.getPersistentData().getString(NBT_MOUNT_ID);
        return id.isBlank() ? null : normalize(id);
    }

    public static @Nullable String getMountIdFromVehicle(Entity vehicle) {
        EntityType<?> type = vehicle.getType();
        if (type == ModEntities.SPIRAL_STRIDER.get()) return "spiral_strider";
        if (type == ModEntities.DRIFTSKIMMER.get()) return "driftskimmer";
        if (type == ModEntities.TREADWINDER.get()) return "treadwinder";
        if (type == ModEntities.ECHO_HARNESS.get()) return "echo_harness";
        return null;
    }

    public static Optional<EntityType<? extends CephalariMountEntity>> getMountTypeById(String mountId) {
        return switch (normalize(mountId)) {
            case "spiral_strider" -> Optional.of(ModEntities.SPIRAL_STRIDER.get());
            case "driftskimmer" -> Optional.of(ModEntities.DRIFTSKIMMER.get());
            case "treadwinder" -> Optional.of(ModEntities.TREADWINDER.get());
            case "echo_harness" -> Optional.of(ModEntities.ECHO_HARNESS.get());
            default -> Optional.empty();
        };
    }

    public static String getRandomMountId(@Nullable ServerLevel serverLevel, int randomInt) {
        int roll = Math.floorMod(randomInt, 4);
        return switch (roll) {
            case 0 -> "spiral_strider";
            case 1 -> "driftskimmer";
            case 2 -> "treadwinder";
            default -> "echo_harness";
        };
    }

    public static boolean spawnMountAndRide(LivingEntity rider, ServerLevel level, String mountId) {
        Optional<EntityType<? extends CephalariMountEntity>> mountType = getMountTypeById(mountId);
        if (mountType.isEmpty()) {
            return false;
        }

        CephalariMountEntity mount = mountType.get().create(level);
        if (mount == null) {
            return false;
        }

        mount.moveTo(rider.getX(), rider.getY(), rider.getZ(), rider.getYRot(), rider.getXRot());
        level.addFreshEntity(mount);
        rider.startRiding(mount, true);
        return true;
    }

    private static String normalize(String mountId) {
        return mountId.trim().toLowerCase(Locale.ROOT);
    }
}
