package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariZombieEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class CephalariZombieModel extends GeoModel<CephalariZombieEntity> {
    @Override
    public ResourceLocation getModelResource(CephalariZombieEntity animatable) {
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari_zombie.geo.json");
        }

        int variant = animatable.getAdultZombieVariant();
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari_zombie_" + variant + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CephalariZombieEntity animatable) {
        return animatable.getBodyTextureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(CephalariZombieEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/cephalari_zombie.animation.json");
    }
}
