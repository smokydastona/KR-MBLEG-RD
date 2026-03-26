package com.kruemblegard.entity.adultform;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class CephalariAdultForms {

    private CephalariAdultForms() {}

    public static final String NBT_ADULT_FORM_ID = "KruemblegardCephalariAdultFormId";
    private static final String NBT_ADULT_FORM_ID_OLD_SAVE_KEY = "KruemblegardCephalariMountId";

    public static String getOrAssignAdultFormId(LivingEntity cephalari) {
        String existing = cephalari.getPersistentData().getString(NBT_ADULT_FORM_ID);
        if (!existing.isBlank()) {
            return normalize(existing);
        }

        // Backward compat for older saves.
        String legacy = cephalari.getPersistentData().getString(NBT_ADULT_FORM_ID_OLD_SAVE_KEY);
        if (!legacy.isBlank()) {
            String normalized = normalize(legacy);
            cephalari.getPersistentData().putString(NBT_ADULT_FORM_ID, normalized);
            return normalized;
        }

        String assigned = getRandomAdultFormId(cephalari.level() instanceof ServerLevel serverLevel ? serverLevel : null, cephalari.getRandom().nextInt());
        cephalari.getPersistentData().putString(NBT_ADULT_FORM_ID, assigned);
        return assigned;
    }

    public static void setAdultFormId(LivingEntity cephalari, String adultFormId) {
        cephalari.getPersistentData().putString(NBT_ADULT_FORM_ID, normalize(adultFormId));
    }

    public static @Nullable String getAdultFormId(LivingEntity cephalari) {
        String id = cephalari.getPersistentData().getString(NBT_ADULT_FORM_ID);
        if (!id.isBlank()) {
            return normalize(id);
        }

        // Backward compat for older saves.
        String legacy = cephalari.getPersistentData().getString(NBT_ADULT_FORM_ID_OLD_SAVE_KEY);
        return legacy.isBlank() ? null : normalize(legacy);
    }

    public static String getRandomAdultFormId(@Nullable ServerLevel serverLevel, int randomInt) {
        int roll = Math.floorMod(randomInt, 4);
        return switch (roll) {
            case 0 -> "spiral_strider";
            case 1 -> "driftskimmer";
            case 2 -> "treadwinder";
            default -> "echo_harness";
        };
    }

    /**
     * Stable, compact adult-form variant mapping for rendering/sync.
     *
     * @return 0..3 for known adult-form ids, or -1 if unknown.
     */
    public static int getAdultFormVariantIndex(@Nullable String adultFormId) {
        if (adultFormId == null) {
            return -1;
        }

        return switch (normalize(adultFormId)) {
            case "spiral_strider" -> 0;
            case "driftskimmer" -> 1;
            case "treadwinder" -> 2;
            case "echo_harness" -> 3;
            default -> -1;
        };
    }

    /**
     * Inverse of {@link #getAdultFormVariantIndex(String)}.
     */
    public static @Nullable String getAdultFormIdByVariantIndex(int variantIndex) {
        return switch (variantIndex) {
            case 0 -> "spiral_strider";
            case 1 -> "driftskimmer";
            case 2 -> "treadwinder";
            case 3 -> "echo_harness";
            default -> null;
        };
    }

    private static String normalize(String adultFormId) {
        return adultFormId.trim().toLowerCase(Locale.ROOT);
    }
}
