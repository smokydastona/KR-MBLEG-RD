package com.kruemblegard.block;

import com.kruemblegard.blockentity.BrineGardenBasinBlockEntity;
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

public final class BrineGardenBasinBlock extends CephalariWorkstationBlock {
    public BrineGardenBasinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrineGardenBasinBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BlockEntityTickerUtil.createTickerHelper(type, ModBlockEntities.BRINE_GARDEN_BASIN.get(), BrineGardenBasinBlockEntity::serverTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE) || random.nextInt(5) != 0) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.85D;
        double centerZ = pos.getZ() + 0.5D;
        level.addParticle(ParticleTypes.BUBBLE_POP,
                centerX + (random.nextDouble() - 0.5D) * 0.45D,
                centerY,
                centerZ + (random.nextDouble() - 0.5D) * 0.45D,
                0.0D,
                0.02D,
                0.0D);
        if (random.nextBoolean()) {
            level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    centerX + (random.nextDouble() - 0.5D) * 0.65D,
                    centerY + 0.15D,
                    centerZ + (random.nextDouble() - 0.5D) * 0.65D,
                    0.0D,
                    0.04D,
                    0.0D);
        }
    }
}