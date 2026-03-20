package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.mount.EchoHarnessEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class EchoHarnessModel extends GeoModel<EchoHarnessEntity> {
    private static final int GOLEM_TEXTURE_VARIANT_MIN = 1;
    private static final int GOLEM_TEXTURE_VARIANT_MAX = 6;

    @Override
    public ResourceLocation getModelResource(EchoHarnessEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/echo_harness.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EchoHarnessEntity animatable) {
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
    public ResourceLocation getAnimationResource(EchoHarnessEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/echo_harness.animation.json");
    }
}
