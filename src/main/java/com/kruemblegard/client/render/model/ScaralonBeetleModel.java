package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

import software.bernie.geckolib.model.GeoModel;

public class ScaralonBeetleModel extends GeoModel<ScaralonBeetleEntity> {
    private static final String SADDLE_BONE = "saddle";
    private static final String LARVA_ROOT_BONE = "root";

    @Override
    public ResourceLocation getModelResource(ScaralonBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_larva.geo.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_beetle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScaralonBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_larva.png");
        }
        int variant = animatable.getTextureVariant();
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_beetle_" + variant + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScaralonBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_larva.animation.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_beetle.animation.json");
    }

    @Override
    public void setCustomAnimations(ScaralonBeetleEntity animatable, long instanceId, AnimationState<ScaralonBeetleEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animatable.isBaby()) {
            // Larva wall-climb alignment: rotate the larva onto the surface it's attached to.
            CoreGeoBone root = this.getAnimationProcessor().getBone(LARVA_ROOT_BONE);
            if (root != null && animatable.isLarvaClimbing()) {
                Direction face = animatable.getLarvaClimbFace();

                float rotX = 0.0F;
                float rotZ = 0.0F;

                if (face != null) {
                    // Align larva "belly" (down) to the wall normal.
                    // Ground: no override; Walls: rotate 90 degrees towards the face.
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

        // Hide saddle geometry unless the beetle is actually saddled.
        CoreGeoBone saddle = this.getAnimationProcessor().getBone(SADDLE_BONE);
        if (saddle != null) {
            saddle.setHidden(!animatable.isSaddled());
        }
    }
}
