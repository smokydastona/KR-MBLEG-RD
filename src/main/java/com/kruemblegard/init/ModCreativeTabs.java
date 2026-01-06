package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.item.CrumblingCodexItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kruemblegard.MODID);

    public static final RegistryObject<CreativeModeTab> KRUEMBLEGARD = TABS.register(
            "kruemblegard",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.kruemblegard"))
                    .icon(() -> new ItemStack(ModItems.MENU_TAB.get()))
                    .displayItems((parameters, output) -> {
                        // Put *everything* registered by the mod into one tab.
                        // NOTE: Some items (e.g., auto-generated BlockItems) are registered outside ModItems.ITEMS,
                        // so we scan the actual item registry by namespace.
                        for (Item item : ForgeRegistries.ITEMS.getValues()) {
                            var key = ForgeRegistries.ITEMS.getKey(item);
                            if (key == null || !Kruemblegard.MODID.equals(key.getNamespace())) {
                                continue;
                            }

                            if (item == ModItems.MENU_TAB.get()) {
                                continue;
                            }

                            output.accept(item);
                        }

                        // Also include the filled codex variant for easy testing.
                        output.accept(CrumblingCodexItem.createFilledStack(ModItems.CRUMBLING_CODEX.get()));
                    })
                    .build()
    );

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
