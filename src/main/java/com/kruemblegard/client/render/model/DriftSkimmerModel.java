package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.mount.DriftSkimmerEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class DriftSkimmerModel extends GeoModel<DriftSkimmerEntity> {

    @Override
    public ResourceLocation getModelResource(DriftSkimmerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/driftskimmer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DriftSkimmerEntity animatable) {
        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/mounts/cephalari_mount_" + animatable.getTextureVariant() + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(DriftSkimmerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/driftskimmer.animation.json");
    }
}
