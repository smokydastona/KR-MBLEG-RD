package com.kruemblegard.client.render;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.render.model.ScatteredEndermanModel;
import com.kruemblegard.entity.ScatteredEndermanEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ScatteredEndermanRenderer extends GeoEntityRenderer<ScatteredEndermanEntity> {
    public ScatteredEndermanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScatteredEndermanModel());
        this.shadowRadius = 0.6f;

        addRenderLayer(new EyesLayer(this));
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
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        var carried = entity.getCarriedBlock();
        if (carried == null || carried.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }

        // Render carried block in a vanilla-Enderman-like pose.
        poseStack.pushPose();
        // GeoEntityRenderer restores the pose stack after rendering the model, so re-apply the yaw rotation
        // here to keep the carried-block offset in entity-local space (prevents the block drifting behind).
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        poseStack.translate(0.0D, 0.6875D, -0.75D);
        poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.translate(0.25D, 0.1875D, 0.25D);
        // Vanilla Enderman uses negative scaling here, but GeoEntityRenderer's coordinate setup differs.
        // Use a rotation + positive scale to avoid the carried block showing up upside-down.
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            carried,
            poseStack,
            bufferSource,
            packedLight,
            OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    private static final class EyesLayer extends AutoGlowingGeoLayer<ScatteredEndermanEntity> {
        private static final ResourceLocation EYES_TEXTURE =
                new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scattered_enderman_eyes.png");

        public EyesLayer(GeoRenderer<ScatteredEndermanEntity> renderer) {
            super(renderer);
        }

        @Override
        protected ResourceLocation getTextureResource(ScatteredEndermanEntity animatable) {
            return EYES_TEXTURE;
        }
    }
}
