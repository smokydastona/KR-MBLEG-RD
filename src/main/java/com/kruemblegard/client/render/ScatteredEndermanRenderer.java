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
import com.mojang.blaze3d.vertex.VertexConsumer;

import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class ScatteredEndermanRenderer extends GeoEntityRenderer<ScatteredEndermanEntity> {
    public ScatteredEndermanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScatteredEndermanModel());
        this.shadowRadius = 0.6f;

        addRenderLayer(new EyesLayer(this));
        addRenderLayer(new CarriedBlockLayer(this));
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

    private static final class CarriedBlockLayer extends GeoRenderLayer<ScatteredEndermanEntity> {
        public CarriedBlockLayer(GeoRenderer<ScatteredEndermanEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(
            PoseStack poseStack,
            ScatteredEndermanEntity animatable,
            BakedGeoModel bakedModel,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            float partialTick,
            int packedLight,
            int packedOverlay
        ) {
            var carried = animatable.getCarriedBlock();
            if (carried == null || carried.getRenderShape() == RenderShape.INVISIBLE) {
                return;
            }

            poseStack.pushPose();

            // Roughly matches vanilla Enderman held-block placement.
            poseStack.translate(0.0D, 0.6875D, -0.75D);
            poseStack.scale(-0.5F, -0.5F, 0.5F);

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                carried,
                poseStack,
                bufferSource,
                packedLight,
                OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }
    }
}
