package com.kruemblegard.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class EchocapBlock extends BushBlock implements BonemealableBlock {
    private final List<ResourceLocation> growFeatures;

    public EchocapBlock(Properties properties, List<ResourceLocation> growFeatures) {
        super(properties);
        this.growFeatures = List.copyOf(growFeatures);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (level.isClientSide && entity instanceof Player) {
            RandomSource random = level.getRandom();
            for (int i = 0; i < 6; i++) {
                level.addParticle(
                        ParticleTypes.NOTE,
                        pos.getX() + 0.2 + random.nextDouble() * 0.6,
                        pos.getY() + 0.3 + random.nextDouble() * 0.5,
                        pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                        0.0,
                        0.02,
                        0.0
                );
            }
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide) {
            popResource(level, pos, new ItemStack(this));
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return !growFeatures.isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return !growFeatures.isEmpty();
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (growFeatures.isEmpty()) return;

        ResourceLocation featureId = growFeatures.get(random.nextInt(growFeatures.size()));
        ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, featureId);

        BlockState oldState = level.getBlockState(pos);
        level.removeBlock(pos, false);

        boolean placed = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(featureKey)
                .map(holder -> holder.value().place(level, level.getChunkSource().getGenerator(), random, pos))
                .orElse(false);

        if (!placed) {
            level.setBlock(pos, oldState, 3);
        }
    }
}
