package com.kruemblegard.block;

import java.util.List;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.core.particles.ParticleTypes;

public class ScarstoneBlock extends Block {

    private static final int IRON_TIER_LEVEL = 2;

    public ScarstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float base = super.getDestroyProgress(state, player, level, pos);
        int toolTier = getToolTierLevel(player);
        if (toolTier >= 0 && toolTier < IRON_TIER_LEVEL) {
            return base * 0.2F;
        }
        return base;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            return;
        }

        int toolTier = getToolTierLevel(player);
        if (toolTier >= 0 && toolTier < IRON_TIER_LEVEL) {
            RandomSource random = level.getRandom();
            if (random.nextFloat() < 0.35F) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 0.7 + random.nextDouble() * 0.3;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                level.addParticle(ParticleTypes.ASH, x, y, z, 0.0, 0.02, 0.0);
                if (random.nextFloat() < 0.25F) {
                    level.addParticle(ParticleTypes.CRIT, x, y, z, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        int toolTier = getToolTierLevel(tool);
        if (toolTier >= 0 && toolTier < IRON_TIER_LEVEL) {
            tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            if (level instanceof ServerLevel serverLevel && serverLevel.random.nextFloat() < 0.25F) {
                serverLevel.sendParticles(ParticleTypes.ASH,
                        pos.getX() + 0.5,
                        pos.getY() + 0.8,
                        pos.getZ() + 0.5,
                        6,
                        0.35,
                        0.2,
                        0.35,
                        0.01);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        int toolTier = getToolTierLevel(tool);
        if (toolTier >= 0 && toolTier < IRON_TIER_LEVEL) {
            return List.of(new ItemStack(ModBlocks.CRACKED_SCARSTONE.get()));
        }
        return super.getDrops(state, builder);
    }

    private static int getToolTierLevel(Player player) {
        if (player == null) {
            return -1;
        }
        return getToolTierLevel(player.getMainHandItem());
    }

    @SuppressWarnings("deprecation")
    private static int getToolTierLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof TieredItem tiered) {
            return tiered.getTier().getLevel();
        }
        return -1;
    }
}
