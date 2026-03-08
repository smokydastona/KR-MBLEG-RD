package com.kruemblegard.client.render;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariGolemEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CephalariGolemRenderer extends GeoEntityRenderer<CephalariGolemEntity> {
    private static final class CephalariGolemModel extends GeoModel<CephalariGolemEntity> {
        private static ResourceLocation rl(String path) {
            ResourceLocation id = ResourceLocation.tryParse(Kruemblegard.MOD_ID + ":" + path);
            if (id == null) {
                throw new IllegalArgumentException("Invalid ResourceLocation path: " + path);
            }
            return id;
        }

        @Override
        public ResourceLocation getModelResource(CephalariGolemEntity animatable) {
            return rl("geo/cephalari_golem.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(CephalariGolemEntity animatable) {
            return rl("textures/entity/cephalari/cephalari_golem.png");
        }

        @Override
        public ResourceLocation getAnimationResource(CephalariGolemEntity animatable) {
            return rl("animations/cephalari_golem.animation.json");
        }
    }

    public CephalariGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariGolemModel());
        this.shadowRadius = 0.95F;
    }
}
