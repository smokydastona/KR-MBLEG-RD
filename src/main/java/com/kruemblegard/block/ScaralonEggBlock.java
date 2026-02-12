package com.kruemblegard.block;

import com.kruemblegard.entity.ScaralonBeetleEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Scaralon Beetle eggs (turtle-style): placed by breeding, hatches into larva (baby Scaralon Beetle).
 */
public class ScaralonEggBlock extends Block {

    public static final IntegerProperty EGGS = IntegerProperty.create("eggs", 1, 4);
    public static final IntegerProperty HATCH = IntegerProperty.create("hatch", 0, 2);

    private static final VoxelShape ONE_EGG = Block.box(3, 0, 3, 13, 7, 13);
    private static final VoxelShape MULTI_EGG = Block.box(2, 0, 2, 14, 7, 14);

    public ScaralonEggBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(EGGS, 1)
                .setValue(HATCH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EGGS, HATCH);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(EGGS) == 1 ? ONE_EGG : MULTI_EGG;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(HATCH) < 2;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HATCH) >= 2) {
            return;
        }

        // Turtle-like: tends to hatch at night, but will still eventually hatch.
        int chance = level.isNight() ? 6 : 18;
        if (random.nextInt(chance) != 0) {
            return;
        }

        int hatch = state.getValue(HATCH);
        if (hatch < 2) {
            level.setBlock(pos, state.setValue(HATCH, hatch + 1), Block.UPDATE_CLIENTS);
            return;
        }

        hatchEggs(level, pos, state, random);
    }

    private void hatchEggs(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int eggs = state.getValue(EGGS);

        level.removeBlock(pos, false);

        for (int i = 0; i < eggs; i++) {
            ScaralonBeetleEntity larva = ModEntities.SCARALON_BEETLE.get().create(level);
            if (larva == null) {
                continue;
            }

            larva.setBaby(true);
            larva.setAge(-24000);

            double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
            double y = pos.getY() + 0.10D;
            double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;

            larva.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(larva);
        }

        // Small bit of fluff: slight "hatch" particles would be nice later.
    }
}
