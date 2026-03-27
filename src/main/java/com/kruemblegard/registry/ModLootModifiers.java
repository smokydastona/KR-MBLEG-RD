package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.loot.AddItemsLootModifier;
import com.kruemblegard.loot.AddTelekinesisLootModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
        DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Kruemblegard.MOD_ID);

    public static final RegistryObject<Codec<AddItemsLootModifier>> ADD_ITEMS =
        LOOT_MODIFIERS.register("add_items", AddItemsLootModifier.CODEC);

    public static final RegistryObject<Codec<AddTelekinesisLootModifier>> ADD_TELEKINESIS_LOOT =
        LOOT_MODIFIERS.register("add_telekinesis_loot", AddTelekinesisLootModifier.CODEC);

    private ModLootModifiers() {
    }
}
