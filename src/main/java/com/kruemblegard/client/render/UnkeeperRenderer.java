package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.UnkeeperModel;
import com.kruemblegard.entity.UnkeeperEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class UnkeeperRenderer extends GeoEntityRenderer<UnkeeperEntity> {

    public UnkeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new UnkeeperModel());
        this.shadowRadius = 0.6f;
    }
}
