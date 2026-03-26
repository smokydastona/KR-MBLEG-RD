package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.CephalariBodyOverlayLayer;
import com.kruemblegard.client.render.layer.CephalariProfessionLayer;
import com.kruemblegard.client.render.model.CephalariModel;
import com.kruemblegard.entity.CephalariEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class CephalariRenderer extends GeoEntityRenderer<CephalariEntity> {
    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    public CephalariRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariModel());
        this.shadowRadius = 0.5F;

        addRenderLayer(new CephalariProfessionLayer(this));
        addRenderLayer(new CephalariBodyOverlayLayer(this));
    }

    @Override
    public void renderRecursively(
        PoseStack poseStack,
        CephalariEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        // Adult Cephalari adult-form appearances use an adult-form geo that embeds a Cephalari subtree.
        // We want explicit, deterministic pass ordering like the Scaralon carpet fix:
        // 1) profession + badge overlays (on their dedicated bones)
        // 2) Cephalari biome/bonus body overlay (only on embedded Cephalari bones)
        // 3) golem/base texture (only on non-Cephalari bones)
        if (!isReRender
            && animatable != null
            && !animatable.isBaby()
            && animatable.hasAdultFormAppearance()
            && bone != null) {

            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);

            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            // Render layers first (registered in the requested order).
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

            // Then render the golem/base texture last, but never paint the embedded Cephalari subtree.
            if (!isInCephalariSubtree(bone)) {
                renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }

            renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

            poseStack.popPose();
            return;
        }

        super.renderRecursively(
            poseStack,
            animatable,
            bone,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            red,
            green,
            blue,
            alpha
        );
    }

    private static boolean isInCephalariSubtree(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        if (CEPHALARI_ROOT_BONE.equals(bone.getName())) {
            return true;
        }

        GeoBone current = bone;
        while (current != null) {
            GeoBone parent = current.getParent();
            if (parent != null && CEPHALARI_ROOT_BONE.equals(parent.getName())) {
                return true;
            }
            current = parent;
        }

        return false;
    }

    @Override
    public RenderType getRenderType(
        CephalariEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
