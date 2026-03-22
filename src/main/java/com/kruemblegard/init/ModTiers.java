package com.kruemblegard.init;

import java.util.function.Supplier;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

public final class ModTiers {
    private ModTiers() {}

        // Netherite-equivalent tier.
        // Used for Runic tools so they're not strictly stronger than Netherite,
        // while still allowing a custom repair ingredient.
    public static final Tier RUNIC;
    public static final Tier SCARSTEEL;

    static {
        // No special block tag gating; ore drops/loot handle gating for now.
        TagKey<Block> emptyTag = TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                new ResourceLocation(Kruemblegard.MOD_ID, "mineable/none"));

        Supplier<Ingredient> runicRepair = () -> Ingredient.of(ModItems.RUNIC_INGOT.get());
        Supplier<Ingredient> scarsteelRepair = () -> Ingredient.of(ModItems.SCARSTEEL_INGOT.get());

        RUNIC = TierSortingRegistry.registerTier(
                new ForgeTier(
                        4,      // harvest level (matches Netherite)
                        2031,   // uses (matches Netherite)
                        9.0f,   // speed (matches Netherite)
                        4.0f,   // attack damage bonus (matches Netherite)
                        15,     // enchantment value (matches Netherite)
                        emptyTag,
                runicRepair
                ),
                new ResourceLocation(Kruemblegard.MOD_ID, "runic"),
                java.util.List.of(Tiers.NETHERITE),
                java.util.List.of()
        );

        // Diamond-equivalent mid-tier with custom repair ingredient.
        SCARSTEEL = TierSortingRegistry.registerTier(
            new ForgeTier(
                3,      // harvest level (matches Diamond)
                1561,   // uses (matches Diamond)
                8.0f,   // speed (matches Diamond)
                3.0f,   // attack damage bonus (matches Diamond)
                10,     // enchantment value (matches Diamond)
                emptyTag,
                scarsteelRepair
            ),
            new ResourceLocation(Kruemblegard.MOD_ID, "scarsteel"),
            java.util.List.of(Tiers.DIAMOND),
            java.util.List.of(Tiers.NETHERITE)
        );
    }
}
