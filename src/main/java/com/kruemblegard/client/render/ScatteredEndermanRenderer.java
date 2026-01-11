package com.kruemblegard.client.render;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.render.model.ScatteredEndermanModel;
import com.kruemblegard.entity.ScatteredEndermanEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ScatteredEndermanRenderer extends GeoEntityRenderer<ScatteredEndermanEntity> {
    public ScatteredEndermanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScatteredEndermanModel());
        this.shadowRadius = 0.6f;

        addRenderLayer(new EyesLayer(this));
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
