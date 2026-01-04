package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GravevineBlock extends BushBlock {
    public GravevineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(net.minecraft.world.level.block.Blocks.END_STONE)
                || state.is(ModBlocks.ATTUNED_STONE.get());
    }

    @Override
    public void randomTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // "Grows faster near skull blocks or Crumbling Codex structures".
        // We interpret "Codex structures" as the mod's ancient/standing stone cluster pieces.
        int bonus = 0;
        bonus += countNearby(level, pos, 4, BlockTags.SKULLS) > 0 ? 2 : 0;
        bonus += countNearby(level, pos, 6, ModBlocks.ANCIENT_WAYSTONE.get()) > 0 ? 2 : 0;
        bonus += countNearby(level, pos, 6, ModBlocks.STANDING_STONE.get()) > 0 ? 1 : 0;

        if (bonus > 0 && random.nextInt(8) == 0) {
            // Simple visible feedback: faint spores.
            level.addParticle(net.minecraft.core.particles.ParticleTypes.ASH,
                    pos.getX() + 0.5,
                    pos.getY() + 0.8,
                    pos.getZ() + 0.5,
                    0.0,
                    0.01,
                    0.0);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide) {
            boolean silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
            ItemStack drop = silkTouch ? new ItemStack(this) : new ItemStack(ModItems.REMNANT_SEEDS.get());
            popResource(level, pos, drop);
        }
    }

    private static int countNearby(Level level, BlockPos origin, int radius, net.minecraft.tags.TagKey<Block> tag) {
        int found = 0;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            if (level.getBlockState(p).is(tag)) {
                found++;
                if (found >= 1) return found;
            }
        }
        return found;
    }

    private static int countNearby(Level level, BlockPos origin, int radius, Block block) {
        int found = 0;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            if (level.getBlockState(p).is(block)) {
                found++;
                if (found >= 1) return found;
            }
        }
        return found;
    }
}
