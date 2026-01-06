package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraprockEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class TraprockModel extends GeoModel<TraprockEntity> {

    @Override
    public ResourceLocation getModelResource(TraprockEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/traprock.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TraprockEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/traprock.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TraprockEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/traprock.animation.json");
    }

    @Override
    public void setCustomAnimations(TraprockEntity animatable, long instanceId, AnimationState<TraprockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        boolean sleeping = !animatable.isAwakened();

        for (CoreGeoBone bone : getAnimationProcessor().getRegisteredBones()) {
            String name = bone.getName();

            if (!sleeping) {
                bone.setHidden(false);
                continue;
            }

            // Keep root unhidden so child traversal still renders the visible waystone bone.
            boolean keepVisible = "root".equalsIgnoreCase(name) || "waystone".equalsIgnoreCase(name);
            bone.setHidden(!keepVisible);
        }
    }
}
