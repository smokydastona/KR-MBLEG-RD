package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WayfallReactivePlantBlock extends WayfallPlantBlock {
    private final ParticleOptions idleParticle;
    private final ParticleOptions activeParticle;
    private final int playerRadius;
    private final int energyRadius;

    public WayfallReactivePlantBlock(
            Properties properties,
            ParticleOptions idleParticle,
            ParticleOptions activeParticle,
            int playerRadius,
            int energyRadius
    ) {
        super(properties);
        this.idleParticle = idleParticle;
        this.activeParticle = activeParticle;
        this.playerRadius = playerRadius;
        this.energyRadius = energyRadius;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        boolean active = false;

        if (playerRadius > 0) {
            active = level.getNearestPlayer(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    playerRadius,
                    false
            ) != null;
        }

        if (!active && energyRadius > 0) {
            // Avoid scanning every tick.
            if (random.nextInt(8) == 0 && isNearWaystoneEnergy(level, pos, energyRadius)) {
                active = true;
            }
        }

        int chance = active ? 3 : 6;
        if (random.nextInt(chance) != 0) return;

        ParticleOptions particle = active ? activeParticle : idleParticle;
        level.addParticle(
                particle,
                pos.getX() + 0.2 + random.nextDouble() * 0.6,
                pos.getY() + 0.35 + random.nextDouble() * 0.5,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                0.0,
                0.01,
                0.0
        );
    }

    private static boolean isNearWaystoneEnergy(Level level, BlockPos origin, int radius) {
        TagKey<Block> tag = com.kruemblegard.registry.ModTags.Blocks.WAYSTONE_ENERGY_SOURCES;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            if (level.getBlockState(p).is(tag)) {
                return true;
            }
        }
        return false;
    }
}
