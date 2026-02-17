package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.PebblitModel;
import com.kruemblegard.entity.PebblitEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.player.Player;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PebblitRenderer extends GeoEntityRenderer<PebblitEntity> {

    public PebblitRenderer(EntityRendererProvider.Context context) {
        super(context, new PebblitModel());
        this.shadowRadius = 0.35f;
    }

    @Override
    public boolean shouldRender(PebblitEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        boolean perched = livingEntity.isPassenger() && livingEntity.getVehicle() instanceof Player;
        if (perched && !PebblitShoulderRenderContext.isInShoulderLayer()) {
            return false;
        }

        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
