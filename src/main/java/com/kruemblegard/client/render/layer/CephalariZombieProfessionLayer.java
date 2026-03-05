package com.kruemblegard.client.render.layer;

import javax.annotation.Nullable;

import com.kruemblegard.entity.CephalariZombieEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

import software.bernie.geckolib.cache.object.BakedGeoModel;
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
        if (animatable.isBaby()) {
            return;
        }

        VillagerData data = animatable.getVillagerData();
        VillagerProfession profession = data.getProfession();
        if (profession == VillagerProfession.NONE) {
            return;
        }

        ResourceLocation professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
        if (professionId != null) {
            ResourceLocation professionTexture = new ResourceLocation(
                "minecraft",
                "textures/entity/zombie_villager/profession/" + professionId.getPath() + ".png"
            );

            RenderType professionType = RenderType.entityCutoutNoCull(professionTexture);
            VertexConsumer professionBuffer = bufferSource.getBuffer(professionType);
            super.render(poseStack, animatable, bakedModel, professionType, bufferSource, professionBuffer, partialTick, packedLight, packedOverlay);
        }

        ResourceLocation levelTexture = getProfessionLevelTexture(data.getLevel());
        if (levelTexture != null) {
            RenderType levelType = RenderType.entityCutoutNoCull(levelTexture);
            VertexConsumer levelBuffer = bufferSource.getBuffer(levelType);
            super.render(poseStack, animatable, bakedModel, levelType, bufferSource, levelBuffer, partialTick, packedLight, packedOverlay);
        }
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
}
