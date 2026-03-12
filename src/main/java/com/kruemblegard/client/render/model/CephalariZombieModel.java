package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariZombieEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class CephalariZombieModel extends GeoModel<CephalariZombieEntity> {
    @Override
    public ResourceLocation getModelResource(CephalariZombieEntity animatable) {
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
    public ResourceLocation getTextureResource(CephalariZombieEntity animatable) {
        // Zombified Cephalari always use Kruemblegard's dedicated zombified textures.
        // (Adult "variant" selects the geo, not the texture.)
        return animatable.getBodyTextureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(CephalariZombieEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/cephalari_zombie.animation.json");
    }
}
