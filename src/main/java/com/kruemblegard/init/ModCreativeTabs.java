package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.item.CrumblingCodexItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kruemblegard.MODID);

    public static final RegistryObject<CreativeModeTab> KRUEMBLEGARD = TABS.register(
            "kruemblegard",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.kruemblegard"))
                    .icon(() -> new ItemStack(ModItems.RUNIC_CORE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ANCIENT_WAYSTONE_ITEM.get());

                        output.accept(ModItems.STANDING_STONE_ITEM.get());
                        output.accept(ModItems.ATTUNED_STONE_ITEM.get());
                        output.accept(ModItems.ATTUNED_ORE_ITEM.get());

                        output.accept(ModItems.ATTUNED_RUNE_SHARD.get());
                        output.accept(ModItems.RUNIC_CORE.get());

                        output.accept(ModItems.RUNIC_SWORD.get());
                        output.accept(ModItems.RUNIC_PICKAXE.get());
                        output.accept(ModItems.RUNIC_AXE.get());
                        output.accept(ModItems.RUNIC_SHOVEL.get());
                        output.accept(ModItems.RUNIC_HOE.get());

                        output.accept(CrumblingCodexItem.createFilledStack(ModItems.CRUMBLING_CODEX.get()));

                        output.accept(ModItems.KRUEMBLEGARD_SPAWN_EGG.get());
                        output.accept(ModItems.TRAPROCK_SPAWN_EGG.get());
                        output.accept(ModItems.PEBBLIT_SPAWN_EGG.get());
                    })
                    .build()
    );

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
