package com.kruemblegard.block;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.fml.ModList;

/**
 * Schematic-only "franch wood". Visually identical to the wood block of its family, but:
 * - Drops are randomized (default: 50/50 wood vs log)
 * - When common tree-harvester mods are present, drops are 1/3 wood, 1/3 log, 1/3 nothing
 * - Clone/pick-block returns the normal wood block so it isn't obvious to players
 */
public final class FranchWoodBlock extends StrippableRotatedPillarBlock {
    private static final Set<String> TREE_HARVESTER_MODIDS = Set.of(
            "treeharvester",
            "treechop",
            "fallingtree"
    );

    private final Supplier<? extends Block> woodDrop;
    private final Supplier<? extends Block> logDrop;

    public FranchWoodBlock(
            Supplier<? extends Block> stripped,
            Supplier<? extends Block> woodDrop,
            Supplier<? extends Block> logDrop,
            Properties properties
    ) {
        super(stripped, properties);
        this.woodDrop = woodDrop;
        this.logDrop = logDrop;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        // Make it indistinguishable during pick-block.
        return new ItemStack(woodDrop.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (!shouldUseTreeHarvesterDrops()) {
            // Use normal loot table behavior (50/50 wood/log defined in data).
            return super.getDrops(state, builder);
        }

        RandomSource random = RandomSource.create();
        int roll = random.nextInt(3);
        if (roll == 0) {
            return List.of(new ItemStack(woodDrop.get()));
        }
        if (roll == 1) {
            return List.of(new ItemStack(logDrop.get()));
        }
        return List.of();
    }

    private static boolean shouldUseTreeHarvesterDrops() {
        for (String modid : TREE_HARVESTER_MODIDS) {
            if (ModList.get().isLoaded(modid)) {
                return true;
            }
        }
        return false;
    }
}
