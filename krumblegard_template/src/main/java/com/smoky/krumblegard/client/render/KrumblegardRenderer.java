package com.smoky.krumblegard.client.render;

import com.smoky.krumblegard.client.model.KrumblegardModel;
import com.smoky.krumblegard.entity.boss.KrumblegardEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KrumblegardRenderer extends GeoEntityRenderer<KrumblegardEntity> {
    public KrumblegardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KrumblegardModel());
        this.shadowRadius = 1.1F;
    }
}
