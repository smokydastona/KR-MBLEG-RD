package com.kruemblegard.client.render;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.MoogloomEntity;
import com.kruemblegard.client.render.layer.MoogloomGriefcapLayer;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoogloomRenderer extends MobRenderer<MoogloomEntity, CowModel<MoogloomEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/moogloom.png");

    public MoogloomRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel<>(context.bakeLayer(ModelLayers.MOOSHROOM)), 0.7F);
        this.addLayer(new MoogloomGriefcapLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoogloomEntity entity) {
        return TEXTURE;
    }
}
