package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.projectile.KruemblegardPhase4BeamBoltEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KruemblegardPhase4BeamBoltModel extends GeoModel<KruemblegardPhase4BeamBoltEntity> {

    @Override
    public ResourceLocation getModelResource(KruemblegardPhase4BeamBoltEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/projectiles/kruemblegard_phase4_beam_bolt.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KruemblegardPhase4BeamBoltEntity animatable) {
        // Default texture until a dedicated one is exported from Blockbench.
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/item/rune_petals.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KruemblegardPhase4BeamBoltEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/projectiles/kruemblegard_phase4_beam_bolt.animation.json");
    }
}
