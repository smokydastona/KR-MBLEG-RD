package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.projectile.KruemblegardPhase1BoltEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KruemblegardPhase1BoltModel extends GeoModel<KruemblegardPhase1BoltEntity> {

    @Override
    public ResourceLocation getModelResource(KruemblegardPhase1BoltEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/projectiles/kruemblegard_phase1_bolt.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KruemblegardPhase1BoltEntity animatable) {
        // Placeholder texture (replace with your own when you export from Blockbench)
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/block/runebloom.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KruemblegardPhase1BoltEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/projectiles/kruemblegard_phase1_bolt.animation.json");
    }
}
