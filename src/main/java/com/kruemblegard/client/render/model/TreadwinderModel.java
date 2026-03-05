package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.mount.TreadwinderEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class TreadwinderModel extends GeoModel<TreadwinderEntity> {

    @Override
    public ResourceLocation getModelResource(TreadwinderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/treadwinder.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TreadwinderEntity animatable) {
        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/mounts/cephalari_mount_" + animatable.getTextureVariant() + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(TreadwinderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/treadwinder.animation.json");
    }
}
