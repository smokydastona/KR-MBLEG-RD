package com.kruemblegard.world.feature;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

final class RockSchematicMapping {
    private RockSchematicMapping() {}

    static BlockState mapState(BlockState state, RandomSource random, ResourceLocation mainBlockId, float biomeTemp, RockSchematicConfiguration cfg) {
        if (state == null || state.isAir()) {
            return null;
        }

        // Standard schematic markers (match tree schematic conventions).
        if (state.is(Blocks.TINTED_GLASS)) {
            return null;
        }
        if (state.is(Blocks.TRIPWIRE)) {
            return null;
        }

        if (state.is(Blocks.RED_WOOL)) {
            Block main = mainBlockId != null ? net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(mainBlockId) : Blocks.STONE;
            state = main.defaultBlockState();
        }

        // Force any waterloggable blocks to be non-waterlogged.
        state = forceBooleanProperty(state, "waterlogged", false);

        Block block = state.getBlock();

        // If the schematic palette is all one primary rock block, treat it as the "base".
        Block main = mainBlockId != null ? net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(mainBlockId) : null;
        boolean isMain = (main != null && block == main);

        boolean isStoneLike = isMain
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || block == Blocks.COBBLESTONE
                || block == Blocks.STONE
                || block == Blocks.ANDESITE
                || block == Blocks.DIORITE
                || block == Blocks.GRANITE
                || block == Blocks.TUFF
                || block == Blocks.CALCITE
                || block == Blocks.DEEPSLATE
                || block == Blocks.COBBLED_DEEPSLATE
                || block == Blocks.STONE_BRICKS
                || block == Blocks.CRACKED_STONE_BRICKS
                || block == Blocks.MOSSY_STONE_BRICKS
                || block == Blocks.DEEPSLATE_BRICKS
                || block == Blocks.CRACKED_DEEPSLATE_BRICKS
                || block == Blocks.MOSSY_COBBLESTONE;

        if (!isStoneLike) {
            return state;
        }

        float mossyBias = cfg.mossyChance();
        float crackedBias = cfg.crackedChance();

        // Temperature band tweak:
        // - colder: slightly more cracking
        // - warm/temperate: slightly more moss
        if (biomeTemp < 0.35F) {
            crackedBias = Math.min(1.0F, crackedBias + 0.04F);
        } else if (biomeTemp < 1.10F) {
            mossyBias = Math.min(1.0F, mossyBias + 0.04F);
        }

        float r = random.nextFloat();

        // Crack first (keeps moss as a second-order effect).
        if (r < crackedBias) {
            if (block == Blocks.STONE_BRICKS) {
                return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            }
            if (block == Blocks.DEEPSLATE_BRICKS) {
                return Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
            }
            if (block == Blocks.STONE) {
                return Blocks.COBBLESTONE.defaultBlockState();
            }
            if (block == Blocks.DEEPSLATE) {
                return Blocks.COBBLED_DEEPSLATE.defaultBlockState();
            }
        }

        if (r < crackedBias + mossyBias) {
            if (block == Blocks.COBBLESTONE) {
                return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            }
            if (block == Blocks.STONE_BRICKS || block == Blocks.CRACKED_STONE_BRICKS) {
                return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            }
        }

        return state;
    }

    private static BlockState forceBooleanProperty(BlockState state, String name, boolean value) {
        Property<?> prop = state.getProperties().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
        if (prop == null) {
            return state;
        }

        if (!Boolean.class.equals(prop.getValueClass())) {
            return state;
        }

        @SuppressWarnings("unchecked")
        Property<Boolean> boolProp = (Property<Boolean>) prop;
        Optional<Boolean> parsed = boolProp.getValue(Boolean.toString(value));
        return parsed.map(v -> state.setValue(boolProp, v)).orElse(state);
    }
}
