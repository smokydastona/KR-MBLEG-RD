package com.kruemblegard.world.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class AncientWayRuinsBlockProcessor {
    private AncientWayRuinsBlockProcessor() {}

    public static BlockState process(BlockState state, RandomSource random) {
        if (state == null || state.isAir() || state.is(Blocks.STRUCTURE_VOID)) {
            return state;
        }

        if (state.is(Blocks.RED_WOOL) || state.is(Blocks.RED_STAINED_GLASS)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (state.is(Blocks.SCULK_VEIN)) {
            float roll = random.nextFloat();
            if (roll < 0.58F) {
                return Blocks.AIR.defaultBlockState();
            }
            if (roll < 0.88F) {
                return Blocks.SCULK_SENSOR.defaultBlockState();
            }
            return Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, Boolean.TRUE);
        }

        if (state.is(Blocks.STONE_BRICKS)) {
            return ruinize(random, state, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.10F, 0.22F, 0.10F);
        }
        if (state.is(Blocks.STONE_BRICK_STAIRS)) {
            return ruinize(random, state, Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), null, 0.10F, 0.22F, 0.0F);
        }
        if (state.is(Blocks.STONE_BRICK_SLAB)) {
            return ruinize(random, state, Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState(), null, 0.08F, 0.22F, 0.0F);
        }
        if (state.is(Blocks.STONE_BRICK_WALL)) {
            return ruinize(random, state, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState(), null, 0.12F, 0.22F, 0.0F);
        }
        if (state.is(Blocks.COBBLESTONE)) {
            return ruinize(random, state, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), null, 0.06F, 0.18F, 0.0F);
        }
        if (state.is(Blocks.COBBLESTONE_STAIRS)) {
            return ruinize(random, state, Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), null, 0.08F, 0.18F, 0.0F);
        }
        if (state.is(Blocks.COBBLESTONE_SLAB)) {
            return ruinize(random, state, Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), null, 0.06F, 0.18F, 0.0F);
        }
        if (state.is(Blocks.COBBLESTONE_WALL)) {
            return ruinize(random, state, Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), null, 0.10F, 0.18F, 0.0F);
        }
        if (state.is(Blocks.DEEPSLATE_BRICKS)) {
            return ruinize(random, state, null, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState(), 0.05F, 0.0F, 0.18F);
        }
        if (state.is(Blocks.DEEPSLATE_TILES)) {
            return ruinize(random, state, null, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState(), 0.05F, 0.0F, 0.18F);
        }
        if (state.is(Blocks.POLISHED_DEEPSLATE) && random.nextFloat() < 0.05F) {
            return Blocks.COBBLED_DEEPSLATE.defaultBlockState();
        }

        return state;
    }

    private static BlockState ruinize(RandomSource random, BlockState original, BlockState mossy, BlockState cracked, float airChance, float mossyChance, float crackedChance) {
        float roll = random.nextFloat();
        if (roll < airChance) {
            return Blocks.AIR.defaultBlockState();
        }
        if (mossy != null && roll < airChance + mossyChance) {
            return copySharedProperties(original, mossy);
        }
        if (cracked != null && roll < airChance + mossyChance + crackedChance) {
            return copySharedProperties(original, cracked);
        }
        return original;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState copySharedProperties(BlockState from, BlockState to) {
        BlockState out = to;
        for (Property<?> property : from.getProperties()) {
            Property<?> match = out.getProperties().stream().filter(candidate -> candidate.getName().equals(property.getName())).findFirst().orElse(null);
            if (match == null) {
                continue;
            }
            out = out.setValue((Property) match, from.getValue((Property) property));
        }
        return out;
    }
}