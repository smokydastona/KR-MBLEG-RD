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

import org.joml.Matrix4f;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class CephalariRenderer extends GeoEntityRenderer<CephalariEntity> {
    private static final String CEPHALARI_ROOT_BONE = "cephalari";
    private static final String ROOT_BONE = "root";

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

            if (isAdultFormRootBone(bone)) {
                poseStack.pushPose();
                RenderUtils.translateMatrixToBone(poseStack, bone);
                RenderUtils.translateToPivotPoint(poseStack, bone);
                RenderUtils.rotateMatrixAroundBone(poseStack, bone);
                RenderUtils.scaleMatrixForBone(poseStack, bone);

                if (bone.isTrackingMatrices()) {
                    Matrix4f poseState = new Matrix4f(poseStack.last().pose());
                    Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

                    bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                    bone.setLocalSpaceMatrix(RenderUtils.translateMatrix(localMatrix, getRenderOffset(this.animatable, 1).toVector3f()));
                    bone.setWorldSpaceMatrix(RenderUtils.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
                }

                RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

                applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

                if (!isInCephalariSubtree(bone)) {
                    renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                }

                java.util.List<GeoBone> deferredChildren = new java.util.ArrayList<>();
                for (GeoBone child : bone.getChildBones()) {
                    if (child == null) {
                        continue;
                    }

                    if (CEPHALARI_ROOT_BONE.equals(child.getName())) {
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
                        continue;
                    }

                    deferredChildren.add(child);
                }

                for (GeoBone child : deferredChildren) {
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

            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);

            if (bone.isTrackingMatrices()) {
                Matrix4f poseState = new Matrix4f(poseStack.last().pose());
                Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

                bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                bone.setLocalSpaceMatrix(RenderUtils.translateMatrix(localMatrix, getRenderOffset(this.animatable, 1).toVector3f()));
                bone.setWorldSpaceMatrix(RenderUtils.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
            }

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

    private static boolean isAdultFormRootBone(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        return ROOT_BONE.equals(bone.getName()) && bone.getParent() == null;
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
