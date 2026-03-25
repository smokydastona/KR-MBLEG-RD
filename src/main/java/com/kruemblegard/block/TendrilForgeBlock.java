package com.kruemblegard.block;

import com.kruemblegard.blockentity.TendrilForgeBlockEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.util.BlockEntityTickerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class TendrilForgeBlock extends CephalariWorkstationBlock {
    public TendrilForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TendrilForgeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.TENDRIL_FORGE.get(), TendrilForgeBlockEntity::serverTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE) || random.nextInt(4) != 0) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.95D;
        double centerZ = pos.getZ() + 0.5D;
        level.addParticle(ParticleTypes.END_ROD,
                centerX + (random.nextDouble() - 0.5D) * 0.5D,
                centerY,
                centerZ + (random.nextDouble() - 0.5D) * 0.5D,
                0.0D,
                0.015D,
                0.0D);
        if (random.nextBoolean()) {
            level.addParticle(ParticleTypes.WAX_ON,
                    centerX + (random.nextDouble() - 0.5D) * 0.4D,
                    centerY - 0.1D,
                    centerZ + (random.nextDouble() - 0.5D) * 0.4D,
                    0.0D,
                    0.02D,
                    0.0D);
        }
    }
}