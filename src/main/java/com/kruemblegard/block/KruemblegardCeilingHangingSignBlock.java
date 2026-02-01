package com.kruemblegard.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootParams;

import net.minecraftforge.registries.ForgeRegistries;

public class KruemblegardCeilingHangingSignBlock extends CeilingHangingSignBlock {
    private final ResourceLocation dropItemId;

    public KruemblegardCeilingHangingSignBlock(Properties properties, WoodType woodType, ResourceLocation dropItemId) {
        super(properties, woodType);
        this.dropItemId = dropItemId;
    }

    private ItemStack dropStack() {
        Item item = ForgeRegistries.ITEMS.getValue(dropItemId);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack drop = dropStack();
        if (!drop.isEmpty()) {
            return List.of(drop);
        }
        return super.getDrops(state, builder);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack drop = dropStack();
        return drop.isEmpty() ? super.getCloneItemStack(level, pos, state) : drop;
    }
}
