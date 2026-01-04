package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBrewing {
    private ModBrewing() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ItemStack awkwardPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);
            ItemStack visibilityPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.VISIBILITY.get());

            BrewingRecipeRegistry.addRecipe(
                    new BrewingRecipe(
                            Ingredient.of(awkwardPotion),
                            Ingredient.of(ModItems.WISPSTALK_ITEM.get()),
                            visibilityPotion
                    )
            );
        });
    }
}
