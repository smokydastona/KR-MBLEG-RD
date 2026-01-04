package com.kruemblegard.client.render;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.PebblitEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.resources.ResourceLocation;

public class PebblitRenderer extends SilverfishRenderer {

    private static final ResourceLocation PEBBLIT_TEXTURE =
            new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/pebblit.png");

    public PebblitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(PebblitEntity entity) {
        return PEBBLIT_TEXTURE;
    }
}
