package com.kruemblegard.client.render.layer;

import javax.annotation.Nullable;

import com.kruemblegard.entity.CephalariZombieEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
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
 * Renders vanilla zombie villager profession overlays (and level badge) on top of the Zombified Cephalari texture.
 *
 * Uses the same texture locations vanilla zombie villagers use:
 * - textures/entity/zombie_villager/profession/<profession>.png
 * - textures/entity/zombie_villager/profession_level/<badge>.png
 */
public final class CephalariZombieProfessionLayer extends GeoRenderLayer<CephalariZombieEntity> {

    private static final String PROFESSION_BONE = "profession";
    private static final String PROFESSION_LEVEL_BONE = "profession_level";

    private static final java.util.Map<ResourceLocation, java.util.Optional<ResourceLocation>> PROFESSION_TEXTURE_CACHE =
        new java.util.concurrent.ConcurrentHashMap<>();

    public CephalariZombieProfessionLayer(GeoRenderer<CephalariZombieEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        CephalariZombieEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone so overlays only affect the intended geometry.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariZombieEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null) {
            return;
        }

        boolean isProfessionBone = PROFESSION_BONE.equals(bone.getName());
        boolean isProfessionLevelBone = PROFESSION_LEVEL_BONE.equals(bone.getName());
        if (!isProfessionBone && !isProfessionLevelBone) {
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

        if (isProfessionBone) {
            ResourceLocation professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
            if (professionId == null) {
                return;
            }

            ResourceLocation professionTexture = PROFESSION_TEXTURE_CACHE
                .computeIfAbsent(
                    professionId,
                    id -> java.util.Optional.ofNullable(
                        resolveProfessionTexture(
                            id,
                            "textures/entity/zombie_villager/profession/" + id.getPath() + ".png",
                            new ResourceLocation("minecraft", "textures/entity/zombie_villager/profession/" + id.getPath() + ".png")
                        )
                    )
                )
                .orElse(null);

            if (professionTexture == null) {
                return;
            }

            RenderType professionType = RenderType.entityCutoutNoCull(professionTexture);
            VertexConsumer professionBuffer = bufferSource.getBuffer(professionType);
            super.renderForBone(poseStack, animatable, bone, professionType, bufferSource, professionBuffer, partialTick, packedLight, packedOverlay);
            return;
        }

        ResourceLocation levelTexture = getProfessionLevelTexture(data.getLevel());
        if (levelTexture == null) {
            return;
        }

        RenderType levelType = RenderType.entityCutoutNoCull(levelTexture);
        VertexConsumer levelBuffer = bufferSource.getBuffer(levelType);
        super.renderForBone(poseStack, animatable, bone, levelType, bufferSource, levelBuffer, partialTick, packedLight, packedOverlay);
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

        return new ResourceLocation("minecraft", "textures/entity/zombie_villager/profession_level/" + badge + ".png");
    }

    private static @Nullable ResourceLocation resolveProfessionTexture(ResourceLocation professionId, String path, ResourceLocation vanillaFallback) {
        ResourceLocation namespaced = new ResourceLocation(professionId.getNamespace(), path);
        if (resourceExists(namespaced)) {
            return namespaced;
        }

        if (resourceExists(vanillaFallback)) {
            return vanillaFallback;
        }

        return null;
    }

    private static boolean resourceExists(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }
}
