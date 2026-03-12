package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.CephalariZombieDrownedOuterLayer;
import com.kruemblegard.client.render.layer.CephalariZombieProfessionLayer;
import com.kruemblegard.client.render.model.CephalariZombieModel;
import com.kruemblegard.entity.CephalariZombieEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CephalariZombieRenderer extends GeoEntityRenderer<CephalariZombieEntity> {
    public CephalariZombieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariZombieModel());
        this.shadowRadius = 0.5F;

        addRenderLayer(new CephalariZombieDrownedOuterLayer(this));
        addRenderLayer(new CephalariZombieProfessionLayer(this));
    }

    @Override
    public RenderType getRenderType(
        CephalariZombieEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        // The zombified inner/outer textures rely on alpha; cutout can make them appear invisible.
        return RenderType.entityTranslucent(texture);
    }
}
