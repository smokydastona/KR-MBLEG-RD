package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RunebloomBlock extends BushBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 5);

    public RunebloomBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(12) != 0) return;

        // "Blooms change color based on nearby blocks or biome".
        // Keep it deterministic-ish: hash biome + whether it’s near attuned/standing stones.
        int nearbySig = 0;
        if (level.getBlockState(pos.below()).is(ModBlocks.ATTUNED_STONE.get())) nearbySig += 17;
            if (level.getBlockState(pos.below()).is(ModTags.Blocks.WAYFALL_GROUND)) nearbySig += 11;
        if (level.getBlockState(pos.relative(net.minecraft.core.Direction.NORTH)).is(ModBlocks.STANDING_STONE.get())) nearbySig += 7;

        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeKey = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(biome);
        int biomeHash = biomeKey == null ? 0 : biomeKey.toString().hashCode();

        int variant = Math.floorMod(biomeHash + nearbySig, 6);
        if (state.getValue(VARIANT) != variant) {
            level.setBlock(pos, state.setValue(VARIANT, variant), 2);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide) {
            boolean silkTouch = tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0;
            if (silkTouch) {
                popResource(level, pos, new ItemStack(this));
            } else {
                int count = 1 + level.getRandom().nextInt(2);
                popResource(level, pos, new ItemStack(ModItems.RUNE_PETALS.get(), count));
            }
        }
    }
}
