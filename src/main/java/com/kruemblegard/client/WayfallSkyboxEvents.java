package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class WayfallSkyboxEvents {
    private WayfallSkyboxEvents() {}

    private static final ResourceLocation WAYFALL_DIMENSION_ID = new ResourceLocation(Kruemblegard.MOD_ID, "wayfall");

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        if (!mc.level.dimension().location().equals(WAYFALL_DIMENSION_ID)) {
            return;
        }

        if (WayfallSkyboxRenderer.isDisabled()) {
            return;
        }

        WayfallSkyboxRenderer.render(event.getPoseStack(), event.getPartialTick(), mc.level.getGameTime());
    }
}
