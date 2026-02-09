package com.kruemblegard.effect;

import java.util.function.Consumer;

import com.kruemblegard.Kruemblegard;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

public final class PebblitShoulderEffect extends MobEffect {

    // Stable UUID so the attribute modifier is consistent across sessions.
    private static final String KB_UUID = "9b063db3-468e-4b56-9a38-fd999132d955";

    // Placeholder icon (we ship our own texture so it can be replaced later).
    private static final ResourceLocation ICON = new ResourceLocation(Kruemblegard.MOD_ID, "textures/mob_effect/pebblit_shoulder.png");

    public PebblitShoulderEffect() {
        // Color is only used for particles; we disable particles when applying the effect.
        super(MobEffectCategory.BENEFICIAL, 0x7A7269);

        this.addAttributeModifier(
                Attributes.KNOCKBACK_RESISTANCE,
                KB_UUID,
                1.0D,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen,
                                               GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                guiGraphics.blit(ICON, x + 3, y + 3, 0, 0, 18, 18, 18, 18);
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics,
                                         int x, int y, float z, float alpha) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                guiGraphics.blit(ICON, x + 3, y + 3, 0, 0, 18, 18, 18, 18);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                return true;
            }
        });
    }
}
