package com.kruemblegard.block;

import com.kruemblegard.util.PaleweftRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PaleweftGrassBlock extends BushBlock implements BonemealableBlock {

    private static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    public PaleweftGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        Block rubbleTilth = PaleweftRegistry.getBlock(PaleweftRegistry.RUBBLE_TILTH_ID);
        if (rubbleTilth != null && state.is(rubbleTilth)) {
            return true;
        }
        return state.is(PaleweftRegistry.RUBBLE_TILLABLE_TAG);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        BlockPos above = pos.above();
        Block tallGrass = PaleweftRegistry.getBlock(PaleweftRegistry.PALEWEFT_TALL_GRASS_ID);
        return tallGrass != null
                && level.getBlockState(above).isAir()
                && tallGrass.defaultBlockState().canSurvive(level, pos);
    }

    @Override
    public boolean isBonemealSuccess(net.minecraft.world.level.Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        Block tallGrass = PaleweftRegistry.getBlock(PaleweftRegistry.PALEWEFT_TALL_GRASS_ID);
        if (tallGrass == null) {
            return;
        }

        BlockState tall = tallGrass.defaultBlockState();
        if (!tall.canSurvive(level, pos) || !level.getBlockState(pos.above()).isAir()) {
            return;
        }
        DoublePlantBlock.placeAt(level, tall, pos, 2);
    }
}
