package com.kruemblegard.client.render.layer;

import javax.annotation.Nullable;

import com.kruemblegard.entity.CephalariEntity;

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
 * Renders vanilla villager profession overlays (and level badge) on top of the Cephalari texture.
 *
 * Uses the same texture locations vanilla villagers use:
 * - textures/entity/villager/profession/<profession>.png
 * - textures/entity/villager/profession_level/<badge>.png
 */
public final class CephalariProfessionLayer extends GeoRenderLayer<CephalariEntity> {

    private static final String PROFESSION_BONE = "profession";
    private static final String PROFESSION_HAT_BONE = "profession_hat";
    private static final String PROFESSION_LEVEL_BONE = "profession_level";

    private static final java.util.Map<ResourceLocation, java.util.Optional<ResourceLocation>> PROFESSION_TEXTURE_CACHE =
        new java.util.concurrent.ConcurrentHashMap<>();

    private static final java.util.Map<String, java.util.Optional<ResourceLocation>> PROFESSION_LEVEL_TEXTURE_CACHE =
        new java.util.concurrent.ConcurrentHashMap<>();

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
        if (bone == null) {
            return;
        }

        boolean isProfessionBone = PROFESSION_BONE.equals(bone.getName());
        boolean isProfessionHatBone = PROFESSION_HAT_BONE.equals(bone.getName());
        boolean isProfessionLevelBone = PROFESSION_LEVEL_BONE.equals(bone.getName());
        if (!isProfessionBone && !isProfessionHatBone && !isProfessionLevelBone) {
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

        if (isProfessionBone || isProfessionHatBone) {
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
                            "textures/entity/villager/profession/" + id.getPath() + ".png",
                            new ResourceLocation("minecraft", "textures/entity/villager/profession/" + id.getPath() + ".png")
                        )
                    )
                )
                .orElse(null);

            if (professionTexture == null) {
                return;
            }

            RenderType professionType = useDepthSafeOverlay(animatable)
                ? RenderType.entityTranslucent(professionTexture)
                : RenderType.entityCutoutNoCull(professionTexture);
            VertexConsumer professionBuffer = bufferSource.getBuffer(professionType);

            // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
            getRenderer().renderCubesOfBone(poseStack, bone, professionBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }

        // Badge overlay: render only on the dedicated profession_level bone.
        ResourceLocation professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
        if (professionId == null) {
            return;
        }

        ResourceLocation levelTexture = getProfessionLevelTexture(professionId, data.getLevel());
        if (levelTexture == null) {
            return;
        }

        RenderType levelType = useDepthSafeOverlay(animatable)
            ? RenderType.entityTranslucent(levelTexture)
            : RenderType.entityCutoutNoCull(levelTexture);
        VertexConsumer levelBuffer = bufferSource.getBuffer(levelType);

        // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
        getRenderer().renderCubesOfBone(poseStack, bone, levelBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static @Nullable ResourceLocation getProfessionLevelTexture(ResourceLocation professionId, int level) {
        int clamped = Math.max(1, Math.min(5, level));
        String badge = switch (clamped) {
            case 1 -> "stone";
            case 2 -> "iron";
            case 3 -> "gold";
            case 4 -> "emerald";
            case 5 -> "diamond";
            default -> "stone";
        };

        String cacheKey = professionId.getNamespace() + "|" + badge;
        return PROFESSION_LEVEL_TEXTURE_CACHE
            .computeIfAbsent(
                cacheKey,
                key -> java.util.Optional.ofNullable(
                    resolveProfessionTexture(
                        professionId,
                        "textures/entity/villager/profession_level/" + badge + ".png",
                        new ResourceLocation("minecraft", "textures/entity/villager/profession_level/" + badge + ".png")
                    )
                )
            )
            .orElse(null);
    }

    private static @Nullable ResourceLocation resolveProfessionTexture(ResourceLocation professionId, String path, ResourceLocation vanillaFallback) {
        // Prefer a mod-provided texture under the profession's namespace.
        ResourceLocation namespaced = new ResourceLocation(professionId.getNamespace(), path);
        if (resourceExists(namespaced)) {
            return namespaced;
        }

        // Then try the vanilla path as a fallback.
        if (resourceExists(vanillaFallback)) {
            return vanillaFallback;
        }

        // No texture found: skip overlay rather than rendering missing-texture.
        return null;
    }

    private static boolean resourceExists(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }

    private static boolean useDepthSafeOverlay(CephalariEntity animatable) {
        int adultFormVariant = animatable.getAdultFormVariant();
        return adultFormVariant == 0 || adultFormVariant == 1;
    }
}
