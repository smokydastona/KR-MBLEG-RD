package com.kruemblegard.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

/**
 * Renderer for the Cephalari golem.
 *
 * Uses the vanilla iron golem model; texture can be swapped later to a modded one.
 */
public class CephalariGolemRenderer extends IronGolemRenderer {

    private static final ResourceLocation VANILLA_IRON_GOLEM_TEXTURE = new ResourceLocation(
        "minecraft",
        "textures/entity/iron_golem/iron_golem.png"
    );

    public CephalariGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolem entity) {
        return VANILLA_IRON_GOLEM_TEXTURE;
    }
}
