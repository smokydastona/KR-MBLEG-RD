package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.MossbackTortoiseEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class MossbackTortoiseModel extends MirroredMobGeoModel<MossbackTortoiseEntity> {

    private static final String MOSS_PATCH_1 = "moss_patch_1";
    private static final String MOSS_PATCH_2 = "moss_patch_2";
    private static final String MOSS_PATCH_3 = "moss_patch_3";
    private static final String MOSS_PATCH_4 = "moss_patch_4";

    @Override
    public ResourceLocation getModelResource(MossbackTortoiseEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/mossback_tortoise.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MossbackTortoiseEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/mossback_tortoise.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MossbackTortoiseEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/mossback_tortoise.animation.json");
    }

    @Override
    public void setCustomAnimations(MossbackTortoiseEntity animatable, long instanceId, AnimationState<MossbackTortoiseEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        boolean hideAll = animatable.isSheared();
        int variant = animatable.getMossVariant();

        setHidden(MOSS_PATCH_1, hideAll || (variant != 0 && variant != 2));
        setHidden(MOSS_PATCH_2, hideAll || (variant != 1 && variant != 2));
        setHidden(MOSS_PATCH_3, hideAll || (variant != 0));
        setHidden(MOSS_PATCH_4, hideAll || (variant != 1 && variant != 2));
    }

    private void setHidden(String boneName, boolean hidden) {
        CoreGeoBone bone = this.getAnimationProcessor().getBone(boneName);
        if (bone != null) {
            bone.setHidden(hidden);
        }
    }
}
