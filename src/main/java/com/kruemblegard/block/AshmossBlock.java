package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;
import com.kruemblegard.world.PaleweftBloom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AshmossBlock extends Block implements BonemealableBlock {

    public AshmossBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Ashmoss is a moss-like surface. It should be stable when player-placed.
        // It only spreads naturally in ash-heavy biomes.
        if (!level.getBiome(pos).is(ModTags.Biomes.ASH_HEAVY)) {
            return;
        }

        if (random.nextInt(12) != 0) {
            return;
        }

        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos target = pos.relative(dir);
        BlockState targetState = level.getBlockState(target);
        if (targetState.is(ModBlocks.FAULT_DUST.get())) {
            level.setBlock(target, ModBlocks.ASHMOSS.get().defaultBlockState(), 2);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // Moss-like bonemeal: spread Ashmoss onto nearby Fault Dust, sprinkle carpet,
        // and grow pale grasses (Paleweft) like a moss bed.
        for (int i = 0; i < 64; i++) {
            int dx = Mth.nextInt(random, -4, 4);
            int dy = Mth.nextInt(random, -1, 1);
            int dz = Mth.nextInt(random, -4, 4);
            BlockPos target = pos.offset(dx, dy, dz);

            if (!level.isLoaded(target)) {
                continue;
            }

            if (!level.getBlockState(target).is(ModBlocks.FAULT_DUST.get())) {
                continue;
            }

            level.setBlock(target, this.defaultBlockState(), 2);

            BlockPos above = target.above();
            if (level.getBlockState(above).isAir() && random.nextFloat() < 0.35f) {
                level.setBlock(above, ModBlocks.ASHMOSS_CARPET.get().defaultBlockState(), 2);
            }
        }

        PaleweftBloom.bloom(level, pos, random, 24);
    }
}
