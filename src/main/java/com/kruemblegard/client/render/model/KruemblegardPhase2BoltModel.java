package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.projectile.KruemblegardPhase2BoltEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KruemblegardPhase2BoltModel extends GeoModel<KruemblegardPhase2BoltEntity> {

    @Override
    public ResourceLocation getModelResource(KruemblegardPhase2BoltEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/projectiles/kruemblegard_phase2_bolt.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KruemblegardPhase2BoltEntity animatable) {
        // Placeholder texture (replace with your own when you export from Blockbench)
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/block/runed_stoneveil_rubble.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KruemblegardPhase2BoltEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/projectiles/kruemblegard_phase2_bolt.animation.json");
    }
}
