package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.enchantment.TelekinesisEnchantment;

import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {
    private ModEnchantments() {}

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Kruemblegard.MOD_ID);

    public static final RegistryObject<Enchantment> TELEKINESIS = ENCHANTMENTS.register(
            "telekinesis",
            TelekinesisEnchantment::new
    );
}