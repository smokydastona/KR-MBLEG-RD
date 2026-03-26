package com.kruemblegard.client.render.model;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;

final class AdultSleepPoseMirroring {
    private AdultSleepPoseMirroring() {
    }

    static <T extends LivingEntity & GeoAnimatable> void apply(GeoModel<T> model, T animatable) {
        if (!shouldMirror(animatable)) {
            return;
        }

        for (CoreGeoBone bone : model.getAnimationProcessor().getRegisteredBones()) {
            bone.setPosX(-bone.getPosX());
            bone.setRotY(-bone.getRotY());
            bone.setRotZ(-bone.getRotZ());
        }
    }

    private static boolean shouldMirror(LivingEntity animatable) {
        return shouldUseLeftHandedMirror(animatable) ^ shouldUseBedMirror(animatable);
    }

    private static boolean shouldUseLeftHandedMirror(LivingEntity animatable) {
        return (animatable.getUUID().getLeastSignificantBits() & 1L) == 0L;
    }

    private static boolean shouldUseBedMirror(LivingEntity animatable) {
        if (!animatable.isSleeping()) {
            return false;
        }

        Direction bedOrientation = animatable.getBedOrientation();
        return bedOrientation != null && shouldMirrorFor(bedOrientation);
    }

    private static boolean shouldMirrorFor(Direction bedOrientation) {
        return bedOrientation == Direction.EAST || bedOrientation == Direction.WEST;
    }
}