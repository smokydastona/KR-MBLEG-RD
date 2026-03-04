package com.kruemblegard.client.render;

import com.kruemblegard.entity.MoogloomEntity;
import com.kruemblegard.client.render.layer.MoogloomGriefcapLayer;
import com.kruemblegard.client.render.model.MoogloomModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoogloomRenderer extends GeoEntityRenderer<MoogloomEntity> {

    public MoogloomRenderer(EntityRendererProvider.Context context) {
        super(context, new MoogloomModel());
        this.shadowRadius = 0.7F;
        this.addRenderLayer(new MoogloomGriefcapLayer(this));
    }
}
