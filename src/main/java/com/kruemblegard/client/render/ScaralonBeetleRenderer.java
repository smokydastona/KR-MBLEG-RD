package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.ScaralonCarpetColorLayer;
import com.kruemblegard.client.render.model.ScaralonBeetleModel;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class ScaralonBeetleRenderer extends GeoEntityRenderer<ScaralonBeetleEntity> {
    private static final String BODY_BONE = "body";

    private static final String CARPET_BONE = "carpet";
    private static final String CARPET_FRAME_BONE = "carpet_frame";
    private static final String CARPET_COLOR_BONE = "carpet_color";

    public ScaralonBeetleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScaralonBeetleModel());
        this.shadowRadius = 0.7F;

        addRenderLayer(new ScaralonCarpetColorLayer(this));
    }

    @Override
    public void renderRecursively(
        PoseStack poseStack,
        ScaralonBeetleEntity animatable,
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
        // Prevent carpet meshes from rendering with the base beetle texture.
        // They are re-rendered by our carpet layer using the correct decor texture.
        if (!isReRender
            && animatable != null
            && !animatable.isBaby()
            && animatable.hasCarpet()
            && bone != null
            && isCarpetBoneName(bone.getName())) {

            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

            poseStack.popPose();
            return;
        }

        // When a carpet is applied, ensure the carpet subtree is rendered last.
        // This guarantees the carpet cannot affect (depth-occlude) unrelated bones regardless of
        // render-order quirks in the baked model.
        if (!isReRender
            && animatable != null
            && !animatable.isBaby()
            && animatable.hasCarpet()
            && bone != null
            && BODY_BONE.equals(bone.getName())) {

            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

            java.util.List<GeoBone> carpetChildren = new java.util.ArrayList<>();
            for (GeoBone child : bone.getChildBones()) {
                if (child == null) {
                    continue;
                }

                if (isCarpetBoneName(child.getName())) {
                    carpetChildren.add(child);
                    continue;
                }

                renderRecursively(
                    poseStack,
                    animatable,
                    child,
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

            for (GeoBone child : carpetChildren) {
                renderRecursively(
                    poseStack,
                    animatable,
                    child,
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

    private static boolean isCarpetBoneName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        return CARPET_BONE.equals(name)
            || CARPET_FRAME_BONE.equals(name)
            || CARPET_COLOR_BONE.equals(name)
            || name.startsWith(CARPET_BONE + "_");
    }

    @Override
    public RenderType getRenderType(
        ScaralonBeetleEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        // Use cutout so fully-transparent pixels do NOT write depth.
        // This prevents transparent overlay bones (e.g., carpet meshes) from hiding the base beetle.
        return RenderType.entityCutoutNoCull(texture);
    }
}
