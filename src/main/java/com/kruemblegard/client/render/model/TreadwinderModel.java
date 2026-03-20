package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.mount.TreadwinderEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class TreadwinderModel extends GeoModel<TreadwinderEntity> {
    private static final int GOLEM_TEXTURE_VARIANT_MIN = 1;
    private static final int GOLEM_TEXTURE_VARIANT_MAX = 6;

    @Override
    public ResourceLocation getModelResource(TreadwinderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/treadwinder.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TreadwinderEntity animatable) {
        int variant = animatable.getTextureVariant();
        if (variant < GOLEM_TEXTURE_VARIANT_MIN || variant > GOLEM_TEXTURE_VARIANT_MAX) {
            variant = GOLEM_TEXTURE_VARIANT_MIN;
        }

        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/mounts/cephalari_mount_" + variant + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(TreadwinderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/treadwinder.animation.json");
    }
}
