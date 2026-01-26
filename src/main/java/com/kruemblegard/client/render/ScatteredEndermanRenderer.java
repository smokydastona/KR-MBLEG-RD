package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.ScatteredEndermanModel;
import com.kruemblegard.client.render.layer.ScatteredEndermanHeldBlockLayer;
import com.kruemblegard.entity.ScatteredEndermanEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScatteredEndermanRenderer extends GeoEntityRenderer<ScatteredEndermanEntity> {
    public ScatteredEndermanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScatteredEndermanModel());
        this.shadowRadius = 0.6f;

        addRenderLayer(new ScatteredEndermanHeldBlockLayer(this));
    }

    @Override
    public RenderType getRenderType(
        ScatteredEndermanEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        // Explicitly use cutout so the base texture renders correctly (avoid the all-black body issue).
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void render(
        ScatteredEndermanEntity entity,
        float entityYaw,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight
    ) {
        int safePackedLight = packedLight <= 0 ? LightTexture.FULL_BRIGHT : packedLight;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, safePackedLight);
    }
}
