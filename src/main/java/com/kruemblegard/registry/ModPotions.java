package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModPotions {
    private ModPotions() {}

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, Kruemblegard.MOD_ID);

    public static final RegistryObject<Potion> VISIBILITY = POTIONS.register(
            "visibility",
            () -> new Potion(
                    new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 180),
                    new MobEffectInstance(MobEffects.GLOWING, 20 * 45)
            )
    );
}
