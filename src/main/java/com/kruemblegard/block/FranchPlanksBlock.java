package com.kruemblegard.block;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.fml.ModList;

public class FranchPlanksBlock extends Block {
    private static final Set<String> TREE_HARVESTER_MODIDS = Set.of(
            "treeharvester",
            "treechop",
            "fallingtree"
    );

    private final Supplier<? extends Block> planksDrop;

    public FranchPlanksBlock(Supplier<? extends Block> planksDrop, Properties properties) {
        super(properties);
        this.planksDrop = planksDrop;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        // Make it indistinguishable during pick-block.
        return new ItemStack(planksDrop.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (!shouldUseTreeHarvesterDrops()) {
            // Use normal loot table behavior (50/50 planks/stick defined in data).
            return super.getDrops(state, builder);
        }

        RandomSource random = RandomSource.create();
        int roll = random.nextInt(3);
        if (roll == 0) {
            return List.of(new ItemStack(planksDrop.get()));
        }
        if (roll == 1) {
            return List.of(new ItemStack(Items.STICK));
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
