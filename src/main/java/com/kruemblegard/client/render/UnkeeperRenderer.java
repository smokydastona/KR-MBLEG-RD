package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.UnkeeperEyesLayer;
import com.kruemblegard.client.render.model.UnkeeperModel;
import com.kruemblegard.entity.UnkeeperEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class UnkeeperRenderer extends GeoEntityRenderer<UnkeeperEntity> {

    public UnkeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new UnkeeperModel());
        this.shadowRadius = 0.6f;

        addRenderLayer(new UnkeeperEyesLayer(this));
    }

    @Override
    public RenderType getRenderType(
        UnkeeperEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void render(
        UnkeeperEntity entity,
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
