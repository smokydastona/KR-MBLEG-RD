package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.mount.EchoHarnessEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class EchoHarnessModel extends GeoModel<EchoHarnessEntity> {

    @Override
    public ResourceLocation getModelResource(EchoHarnessEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/echo_harness.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EchoHarnessEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "textures/entity/echo_harness.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EchoHarnessEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/echo_harness.animation.json");
    }
}
