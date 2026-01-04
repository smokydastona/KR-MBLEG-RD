package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SoulberryShrubBlock extends BerryBushBlock {
    public SoulberryShrubBlock(Properties properties) {
        super(properties, com.kruemblegard.Kruemblegard.MOD_ID, "soulberries", 0.0f);
    }

    @Override
    public void randomTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // "Can be corrupted by nearby hostile mobs": if monsters linger nearby, turn into ghoulberry.
        if (random.nextInt(12) != 0) return;

        AABB box = new AABB(pos).inflate(6.0);
        boolean hostileNearby = !level.getEntitiesOfClass(Mob.class, box, m -> m.getType().getCategory() == MobCategory.MONSTER).isEmpty();
        if (hostileNearby) {
            BlockState newState = ModBlocks.GHOULBERRY_SHRUB.get().defaultBlockState();
            if (newState.hasProperty(AGE)) {
                newState = newState.setValue(AGE, state.getValue(AGE));
            }
            level.setBlock(pos, newState, 2);
        }
    }
}
