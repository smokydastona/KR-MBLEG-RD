package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VoidfeltBlock extends Block {

    public VoidfeltBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean inVoidBiome = level.getBiome(pos).is(ModTags.Biomes.VOID_BIOME);

        // Voidfelt is void-biome ground; outside void-biomes it slowly collapses back into fault dust.
        if (!inVoidBiome && random.nextInt(20) == 0) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return;
        }

        // Grass/mycelium-like survival check.
        if (!canRemainVoidfelt(level, pos)) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return;
        }

        // Spread only within void biomes; allow spreading onto fault dust.
        if (inVoidBiome && level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState spreadState = this.defaultBlockState();
            for (int i = 0; i < 4; i++) {
                BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                BlockState targetState = level.getBlockState(targetPos);
                if (targetState.is(ModBlocks.FAULT_DUST.get())
                        && level.getBiome(targetPos).is(ModTags.Biomes.VOID_BIOME)
                        && canRemainVoidfelt(level, targetPos)) {
                    level.setBlock(targetPos, spreadState, 2);
                }
            }
        }
    }

    private static boolean canRemainVoidfelt(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState above = level.getBlockState(abovePos);

        // If there's a source fluid above (e.g., water), it should die.
        if (!above.getFluidState().isEmpty() && above.getFluidState().isSource()) {
            return false;
        }

        int light = level.getMaxLocalRawBrightness(abovePos);
        int opacity = above.getLightBlock(level, abovePos);
        return !(light < 4 && opacity > 2);
    }
}
