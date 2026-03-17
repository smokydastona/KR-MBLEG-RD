package com.kruemblegard.client.render.layer;

import javax.annotation.Nullable;

import com.kruemblegard.entity.CephalariEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders vanilla villager profession overlays (and level badge) on top of the Cephalari texture.
 *
 * Uses the same texture locations vanilla villagers use:
 * - textures/entity/villager/profession/<profession>.png
 * - textures/entity/villager/profession_level/<badge>.png
 */
public final class CephalariProfessionLayer extends GeoRenderLayer<CephalariEntity> {

    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    // Profession/badge overlays should only apply to the dedicated profession-only bones.
    // This prevents painting shell/mouth/limbs (and, for adult mounts, any mount bones).
    private static final java.util.Set<String> PROFESSION_BONES = java.util.Set.of(
        "profession"
    );

    public CephalariProfessionLayer(GeoRenderer<CephalariEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        CephalariEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone so we can limit overlays to specific bones.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null || !PROFESSION_BONES.contains(bone.getName())) {
            return;
        }

        if (animatable.isBaby()) {
            return;
        }

        VillagerData data = animatable.getVillagerData();
        VillagerProfession profession = data.getProfession();
        if (profession == VillagerProfession.NONE) {
            return;
        }

        // Adult Cephalari use a mount geometry with an embedded Cephalari subtree.
        // Only render profession/badge overlays on that subtree to avoid painting the mount bones.
        if (animatable.hasAdultMountAppearance() && !isInCephalariSubtree(bone)) {
            return;
        }

        ResourceLocation professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
        if (professionId != null) {
            ResourceLocation professionTexture = new ResourceLocation(
                "minecraft",
                "textures/entity/villager/profession/" + professionId.getPath() + ".png"
            );

            RenderType professionType = RenderType.entityCutoutNoCull(professionTexture);
            VertexConsumer professionBuffer = bufferSource.getBuffer(professionType);
            super.renderForBone(poseStack, animatable, bone, professionType, bufferSource, professionBuffer, partialTick, packedLight, packedOverlay);
        }

        ResourceLocation levelTexture = getProfessionLevelTexture(data.getLevel());
        if (levelTexture != null) {
            RenderType levelType = RenderType.entityCutoutNoCull(levelTexture);
            VertexConsumer levelBuffer = bufferSource.getBuffer(levelType);
            super.renderForBone(poseStack, animatable, bone, levelType, bufferSource, levelBuffer, partialTick, packedLight, packedOverlay);
        }
    }

    private static boolean isInCephalariSubtree(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        // Avoid matching the root itself; it is usually a pivot-only bone.
        if (CEPHALARI_ROOT_BONE.equals(bone.getName())) {
            return false;
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

    private static @Nullable ResourceLocation getProfessionLevelTexture(int level) {
        int clamped = Math.max(1, Math.min(5, level));
        String badge = switch (clamped) {
            case 1 -> "stone";
            case 2 -> "iron";
            case 3 -> "gold";
            case 4 -> "emerald";
            case 5 -> "diamond";
            default -> "stone";
        };

        return new ResourceLocation("minecraft", "textures/entity/villager/profession_level/" + badge + ".png");
    }
}
