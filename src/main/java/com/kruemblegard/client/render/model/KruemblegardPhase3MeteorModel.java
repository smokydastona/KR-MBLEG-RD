package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.projectile.KruemblegardPhase3MeteorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KruemblegardPhase3MeteorModel extends GeoModel<KruemblegardPhase3MeteorEntity> {

    @Override
    public ResourceLocation getModelResource(KruemblegardPhase3MeteorEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/projectiles/kruemblegard_phase3_meteor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KruemblegardPhase3MeteorEntity animatable) {
        // Placeholder texture (replace with your own when you export from Blockbench)
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/block/standing_stone.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KruemblegardPhase3MeteorEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/projectiles/kruemblegard_phase3_meteor.animation.json");
    }
}
