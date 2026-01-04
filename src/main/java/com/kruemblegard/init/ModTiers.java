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

    // A tier above Netherite.
    // Used for Runic tools so they're strictly better than Netherite.
    public static final Tier RUNIC;

    static {
        // No special block tag gating; ore drops/loot handle gating for now.
        TagKey<Block> emptyTag = TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                new ResourceLocation(Kruemblegard.MOD_ID, "mineable/none"));

        Supplier<Ingredient> repair = () -> Ingredient.of(ModItems.ATTUNED_RUNE_SHARD.get());

        RUNIC = TierSortingRegistry.registerTier(
                new ForgeTier(
                        5,      // harvest level (Netherite is 4)
                        3072,   // uses
                        10.0f,  // speed
                        5.0f,   // attack damage bonus
                        18,     // enchantment value
                        emptyTag,
                        repair
                ),
                new ResourceLocation(Kruemblegard.MOD_ID, "runic"),
                java.util.List.of(Tiers.NETHERITE),
                java.util.List.of()
        );
    }
}
