package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.KruemblegardBossEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Locale;

public class KruemblegardBossModel extends GeoModel<KruemblegardBossEntity> {

    @Override
    public ResourceLocation getModelResource(KruemblegardBossEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/kruemblegard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KruemblegardBossEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/kruemblegard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KruemblegardBossEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/kruemblegard.animation.json");
    }

    @Override
    public void setCustomAnimations(KruemblegardBossEntity animatable, long instanceId, AnimationState<KruemblegardBossEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        int phase = animatable.getPhase();
        boolean hideArmor = phase >= 2;
        boolean hidePhase3Tagged = phase >= 3;
        boolean hideFinal = phase >= 4;

        for (CoreGeoBone bone : getAnimationProcessor().getRegisteredBones()) {
            String name = bone.getName();
            String lower = name.toLowerCase(Locale.ROOT);

            boolean hide = (hideArmor && lower.contains("armor"))
                    || (hidePhase3Tagged && lower.contains("-p3"))
                    || (hideFinal && (lower.contains("whirlwind") || lower.contains("debris")));

            bone.setHidden(hide);
        }
    }
}
