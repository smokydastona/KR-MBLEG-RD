package com.kruemblegard.event;

import java.util.UUID;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PebblitShoulderEvents {
    private PebblitShoulderEvents() {}

    // Stable UUID so the modifier is idempotent and easy to remove.
    private static final UUID PEBBLIT_SHOULDER_KB_UUID = UUID.fromString("9b063db3-468e-4b56-9a38-fd999132d955");

    private static final AttributeModifier PEBBLIT_SHOULDER_KB_RESIST = new AttributeModifier(
            PEBBLIT_SHOULDER_KB_UUID,
            "Pebblit shoulder knockback resist",
            1.0,
            AttributeModifier.Operation.ADDITION
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }

        ResourceLocation pebblitKey = new ResourceLocation(Kruemblegard.MOD_ID, "pebblit");
        boolean hasPebblitOnShoulder = player.getPassengers().stream().anyMatch(e -> EntityType.getKey(e.getType()).equals(pebblitKey));

        AttributeInstance kbResist = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbResist == null) {
            return;
        }

        AttributeModifier existing = kbResist.getModifier(PEBBLIT_SHOULDER_KB_UUID);

        if (hasPebblitOnShoulder) {
            if (existing == null) {
                kbResist.addPermanentModifier(PEBBLIT_SHOULDER_KB_RESIST);
            }
        } else {
            if (existing != null) {
                kbResist.removeModifier(PEBBLIT_SHOULDER_KB_UUID);
            }
        }
    }

}
