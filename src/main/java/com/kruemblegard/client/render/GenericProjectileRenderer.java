package com.kruemblegard.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class GenericProjectileRenderer<T extends Entity> extends EntityRenderer<T> {

    private final float scale;
    private final ItemRenderer itemRenderer;

    public GenericProjectileRenderer(EntityRendererProvider.Context ctx, float scale) {
        super(ctx);
        this.scale = scale;
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light) {
        if (entity instanceof ItemSupplier itemSupplier) {
            ItemStack stack = itemSupplier.getItem();
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                this.itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.level(),
                    entity.getId()
                );
                poseStack.popPose();
                return;
            }
        }

        // Fallback: invisible projectile (particles only).
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
