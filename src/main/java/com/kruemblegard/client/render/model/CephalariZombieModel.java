package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariZombieEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class CephalariZombieModel<T extends CephalariZombieEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        ResourceLocation base = new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari_zombie.geo.json");
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return base;
        }

        int variant = animatable.getAdultZombieVariant();
        ResourceLocation variantGeo = new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari_zombie_" + variant + ".geo.json");

        // Fail-safe: if a geo variant is missing or broken in an in-dev workspace, fall back to base.
        // (Prevents adult zombified Cephalari from rendering invisible.)
        if (Minecraft.getInstance().getResourceManager().getResource(variantGeo).isPresent()) {
            return variantGeo;
        }

        return base;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        // Zombified Cephalari never use mount textures.
        // The base pass always renders the selected zombified inner layer directly.
        return animatable.getBodyTextureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/cephalari_zombie.animation.json");
    }
}
