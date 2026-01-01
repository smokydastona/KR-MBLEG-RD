package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.KruemblegardBossEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

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
}
