package com.kruemblegard.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;

public class GenericProjectileRenderer<T extends Entity> extends EntityRenderer<T> {

    private final float scale;

    public GenericProjectileRenderer(EntityRendererProvider.Context ctx, float scale) {
        super(ctx);
        this.scale = scale;
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light) {
        // Invisible projectile  particles only
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
