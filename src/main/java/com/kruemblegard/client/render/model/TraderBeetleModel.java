package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraderBeetleEntity;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

/**
 * Uses the same Geo/animations as Scaralon beetles, but swaps to trader-beetle textures.
 */
public class TraderBeetleModel extends GeoModel<TraderBeetleEntity> {
    private static final String SADDLE_BONE = "saddle";
    private static final String CHEST_BONE = "chest";
    private static final String CARPET_BONE = "carpet";
    private static final String LARVA_ROOT_BONE = "root";

    @Override
    public ResourceLocation getModelResource(TraderBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_larva.geo.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_beetle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TraderBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_beetle/scaralon_larva.png");
        }
        int variant = animatable.getTextureVariant();
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_beetle/scaralon_beetle_" + variant + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(TraderBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_larva.animation.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_beetle.animation.json");
    }

    @Override
    public void setCustomAnimations(TraderBeetleEntity animatable, long instanceId, AnimationState<TraderBeetleEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animatable.isBaby()) {
            CoreGeoBone root = this.getAnimationProcessor().getBone(LARVA_ROOT_BONE);
            if (root != null && animatable.isLarvaClimbing()) {
                Direction face = animatable.getLarvaClimbFace();

                float rotX = 0.0F;
                float rotZ = 0.0F;

                if (face != null) {
                    switch (face) {
                        case NORTH -> rotX = 90.0F * Mth.DEG_TO_RAD;
                        case SOUTH -> rotX = -90.0F * Mth.DEG_TO_RAD;
                        case EAST -> rotZ = 90.0F * Mth.DEG_TO_RAD;
                        case WEST -> rotZ = -90.0F * Mth.DEG_TO_RAD;
                        default -> {
                        }
                    }
                }

                root.setRotX(rotX);
                root.setRotZ(rotZ);
            }

            return;
        }

        CoreGeoBone saddle = this.getAnimationProcessor().getBone(SADDLE_BONE);
        if (saddle != null) {
            saddle.setHidden(!animatable.isSaddled());
        }

        CoreGeoBone chest = this.getAnimationProcessor().getBone(CHEST_BONE);
        if (chest != null) {
            chest.setHidden(!animatable.hasChest());
        }

        CoreGeoBone carpet = this.getAnimationProcessor().getBone(CARPET_BONE);
        if (carpet != null) {
            carpet.setHidden(true);
        }

        CoreGeoBone carpetFrame = this.getAnimationProcessor().getBone("carpet_frame");
        if (carpetFrame != null) {
            carpetFrame.setHidden(true);
        }

        CoreGeoBone carpetColor = this.getAnimationProcessor().getBone("carpet_color");
        if (carpetColor != null) {
            carpetColor.setHidden(true);
        }
    }
}
