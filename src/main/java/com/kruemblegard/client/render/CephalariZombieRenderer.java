package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.CephalariZombieDrownedOuterLayer;
import com.kruemblegard.client.render.layer.CephalariZombieProfessionLayer;
import com.kruemblegard.client.render.layer.CephalariZombieBodyOverlayLayer;
import com.kruemblegard.client.render.model.CephalariZombieModel;
import com.kruemblegard.entity.CephalariZombieEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CephalariZombieRenderer extends GeoEntityRenderer<CephalariZombieEntity> {
    public CephalariZombieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariZombieModel());
        this.shadowRadius = 0.5F;

        addRenderLayer(new CephalariZombieBodyOverlayLayer(this));
        addRenderLayer(new CephalariZombieDrownedOuterLayer(this));
        addRenderLayer(new CephalariZombieProfessionLayer(this));
    }
}
