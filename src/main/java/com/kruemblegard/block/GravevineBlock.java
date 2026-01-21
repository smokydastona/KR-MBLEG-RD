package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GravevineBlock extends VineBlock {
    public GravevineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // "Grows faster near skull blocks or Crumbling Codex structures".
        // We interpret "Codex structures" as the mod's ancient/standing stone cluster pieces.
        int bonus = 0;
        bonus += countNearbySkulls(level, pos, 4) > 0 ? 2 : 0;
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

    private static int countNearbySkulls(Level level, BlockPos origin, int radius) {
        int found = 0;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            if (level.getBlockState(p).getBlock() instanceof SkullBlock) {
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
