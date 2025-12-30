package com.smoky.krumblegard.init;

import com.smoky.krumblegard.KrumblegardMod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(ForgeRegistries.CREATIVE_MODE_TABS, KrumblegardMod.MODID);

    public static final RegistryObject<CreativeModeTab> KRUMBLEGARD = TABS.register(
            "krumblegard",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.krumblegard"))
                    .icon(() -> new ItemStack(ModItems.HAUNTED_WAYSTONE_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HAUNTED_WAYSTONE_ITEM.get());
                        output.accept(ModItems.FALSE_WAYSTONE_ITEM.get());
                        output.accept(ModItems.ANCIENT_WAYSTONE_ITEM.get());

                        output.accept(ModItems.STANDING_STONE_ITEM.get());
                        output.accept(ModItems.ATTUNED_STONE_ITEM.get());

                        output.accept(ModItems.ATTUNED_RUNE_SHARD.get());
                        output.accept(ModItems.STONE_CORE_FRAGMENT.get());
                        output.accept(ModItems.RADIANT_ESSENCE.get());

                        output.accept(ModItems.CRUMBLING_CODEX.get());
                        output.accept(ModItems.RADIANT_SWORD.get());

                        output.accept(ModItems.KRUMBLEGARD_SPAWN_EGG.get());
                    })
                    .build()
    );

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
