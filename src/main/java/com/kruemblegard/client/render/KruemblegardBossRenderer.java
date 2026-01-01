package com.kruemblegard.client.render;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.render.model.KruemblegardBossModel;
import com.kruemblegard.entity.KruemblegardBossEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KruemblegardBossRenderer extends GeoEntityRenderer<KruemblegardBossEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/kruemblegard.png");

    public KruemblegardBossRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new KruemblegardBossModel());
        this.shadowRadius = 1.2f;
    }

    @Override
    public ResourceLocation getTextureLocation(KruemblegardBossEntity animatable) {
        return TEXTURE;
    }
}
