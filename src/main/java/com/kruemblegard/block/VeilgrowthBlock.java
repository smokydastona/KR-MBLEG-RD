package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class VeilgrowthBlock extends WayfallSurfaceBlock {

    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    public VeilgrowthBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean nearEnergy = isNearWaystoneEnergy(level, pos, 6);
        boolean charged = state.getValue(CHARGED);

        if (nearEnergy) {
            if (!charged) {
                level.setBlock(pos, state.setValue(CHARGED, Boolean.TRUE), 2);
                return;
            }
            if (random.nextInt(10) == 0) {
                level.setBlock(pos, ModBlocks.RUNEGROWTH.get().defaultBlockState(), 2);
            }
        } else if (charged) {
            level.setBlock(pos, state.setValue(CHARGED, Boolean.FALSE), 2);
        }
    }

    static boolean isNearWaystoneEnergy(ServerLevel level, BlockPos pos, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (level.getBlockState(cursor).is(ModTags.Blocks.WAYSTONE_ENERGY_SOURCES)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
