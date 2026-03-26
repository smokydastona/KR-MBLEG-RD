package com.kruemblegard.client.render.model;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;

final class AdultSleepPoseMirroring {
    private AdultSleepPoseMirroring() {
    }

    static <T extends LivingEntity & GeoAnimatable> void apply(GeoModel<T> model, T animatable) {
        if (!animatable.isSleeping()) {
            return;
        }

        Direction bedOrientation = animatable.getBedOrientation();
        if (bedOrientation == null || !shouldMirrorFor(bedOrientation)) {
            return;
        }

        for (CoreGeoBone bone : model.getAnimationProcessor().getRegisteredBones()) {
            bone.setPosX(-bone.getPosX());
            bone.setRotY(-bone.getRotY());
            bone.setRotZ(-bone.getRotZ());
        }
    }

    private static boolean shouldMirrorFor(Direction bedOrientation) {
        return bedOrientation == Direction.SOUTH || bedOrientation == Direction.EAST;
    }
}