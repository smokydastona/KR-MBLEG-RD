package com.kruemblegard.block;

import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WispstalkBlock extends BushBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public WispstalkBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(ModTags.Blocks.WAYFALL_GROUND);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        int age = state.getValue(AGE);
        if (age >= 3) return;

        boolean nearEnchanted = isNearEnchantedBlocks(level, pos);
        boolean moonlit = level.dimensionType().hasSkyLight()
                && level.isNight()
                && level.getMoonBrightness() > 0.6f;

        if ((nearEnchanted || moonlit) && random.nextInt(6) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) != 0) return;

        level.addParticle(
                ParticleTypes.END_ROD,
                pos.getX() + 0.2 + random.nextDouble() * 0.6,
                pos.getY() + 0.4 + random.nextDouble() * 0.6,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                0.0,
                0.01,
                0.0
        );
    }

    private static boolean isNearEnchantedBlocks(Level level, BlockPos origin) {
        int radius = 4;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            BlockState st = level.getBlockState(p);
            if (st.is(net.minecraft.world.level.block.Blocks.ENCHANTING_TABLE)
                    || st.is(net.minecraft.world.level.block.Blocks.CHISELED_BOOKSHELF)
                    || st.is(net.minecraft.world.level.block.Blocks.LECTERN)
                    || st.is(net.minecraft.world.level.block.Blocks.BOOKSHELF)) {
                return true;
            }
        }
        return false;
    }
}
