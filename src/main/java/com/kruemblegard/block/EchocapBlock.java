package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EchocapBlock extends BushBlock {
    public EchocapBlock(Properties properties) {
        super(properties);
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
}
