package com.kruemblegard.world.feature;

import com.kruemblegard.block.FranchDecay;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

final class EchowoodSchematicMapping {
    private EchowoodSchematicMapping() {
    }

    static BlockState mapState(BlockState original, RandomSource random) {
        // Placeholder: tinted glass in schems means "structure void" (skip placement).
        if (original.is(Blocks.TINTED_GLASS)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Marker: red wool is used to mark the schematic center/pivot; it should never be placed.
        if (original.is(Blocks.RED_WOOL)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Placeholder: use tripwire (string) in schematics to place schematic-only string franch.
        if (original.is(Blocks.TRIPWIRE)) {
            return ModBlocks.STRING_FRANCH.get().defaultBlockState();
        }

        // Force Echowood leaves for any leaf-like block, and guarantee they are non-persistent.
        if (original.is(BlockTags.LEAVES)
                || original.getBlock() instanceof LeavesBlock
                || original.hasProperty(LeavesBlock.PERSISTENT)
                || original.hasProperty(BlockStateProperties.DISTANCE)
                || original.hasProperty(FranchDecay.DISTANCE)) {
            return safeEchowoodLeafState();
        }

        // Normalize stripped Echowood trunk blocks back to their non-stripped forms.
        original = normalizeEchowoodTrunkState(original);

        // Convert any wood/log to Echowood trunk.
        if (original.is(BlockTags.LOGS) || original.getBlock() instanceof RotatedPillarBlock) {
            // Schematics can be authored with any wood/log; we always place franch wood.
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_WOOD.get().defaultBlockState());
        }

        // Convert wood construction pieces into Echowood franch variants.
        if (original.is(BlockTags.FENCE_GATES)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_GATE.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_FENCES)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_STAIRS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_STAIRS.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_SLABS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_SLAB.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_TRAPDOORS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_TRAPDOOR.get().defaultBlockState());
        }
        if (original.is(BlockTags.PLANKS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_PLANKS.get().defaultBlockState());
        }

        return original;
    }

    static BlockState mapStateForMega(BlockState original, RandomSource random) {
        if (original.is(Blocks.TINTED_GLASS)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        if (original.is(Blocks.RED_WOOL)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        if (original.is(Blocks.TRIPWIRE)) {
            return ModBlocks.STRING_FRANCH.get().defaultBlockState();
        }

        // Mega schematics use a special schematic-only leaves variant with reduced drop rates.
        if (original.is(BlockTags.LEAVES)
                || original.getBlock() instanceof LeavesBlock
                || original.hasProperty(LeavesBlock.PERSISTENT)
                || original.hasProperty(BlockStateProperties.DISTANCE)
                || original.hasProperty(FranchDecay.DISTANCE)) {
            return safeEchowoodMegaFranchLeafState();
        }

        original = normalizeEchowoodTrunkState(original);

        if (original.is(BlockTags.LOGS) || original.getBlock() instanceof RotatedPillarBlock) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_WOOD.get().defaultBlockState());
        }

        if (original.is(BlockTags.FENCE_GATES)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_GATE.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_FENCES)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_STAIRS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_STAIRS.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_SLABS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_SLAB.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_TRAPDOORS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_TRAPDOOR.get().defaultBlockState());
        }
        if (original.is(BlockTags.PLANKS)) {
            return copySharedProperties(original, ModBlocks.ECHOWOOD_FRANCH_PLANKS.get().defaultBlockState());
        }

        return original;
    }

    private static BlockState safeEchowoodLeafState() {
        BlockState state = ModBlocks.ECHOWOOD_LEAVES.get().defaultBlockState();

        if (state.hasProperty(LeavesBlock.PERSISTENT)) {
            state = state.setValue(LeavesBlock.PERSISTENT, false);
        }

        if (state.hasProperty(FranchDecay.DISTANCE)) {
            state = state.setValue(FranchDecay.DISTANCE, 1);
        } else if (state.hasProperty(BlockStateProperties.DISTANCE)) {
            state = state.setValue(BlockStateProperties.DISTANCE, 1);
        }

        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }

        return state;
    }

    private static BlockState safeEchowoodMegaFranchLeafState() {
        BlockState state = ModBlocks.ECHOWOOD_MEGA_FRANCH_LEAVES.get().defaultBlockState();

        if (state.hasProperty(LeavesBlock.PERSISTENT)) {
            state = state.setValue(LeavesBlock.PERSISTENT, false);
        }

        if (state.hasProperty(FranchDecay.DISTANCE)) {
            state = state.setValue(FranchDecay.DISTANCE, 1);
        } else if (state.hasProperty(BlockStateProperties.DISTANCE)) {
            state = state.setValue(BlockStateProperties.DISTANCE, 1);
        }

        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }

        return state;
    }

    private static BlockState normalizeEchowoodTrunkState(BlockState state) {
        if (state.is(ModBlocks.STRIPPED_ECHOWOOD_LOG.get())) {
            return copyAxisIfPresent(state, ModBlocks.ECHOWOOD_LOG.get().defaultBlockState());
        }
        if (state.is(ModBlocks.STRIPPED_ECHOWOOD_WOOD.get())) {
            return copyAxisIfPresent(state, ModBlocks.ECHOWOOD_WOOD.get().defaultBlockState());
        }
        return state;
    }

    private static BlockState copyAxisIfPresent(BlockState from, BlockState to) {
        if (from.hasProperty(BlockStateProperties.AXIS) && to.hasProperty(BlockStateProperties.AXIS)) {
            return to.setValue(BlockStateProperties.AXIS, from.getValue(BlockStateProperties.AXIS));
        }
        return to;
    }

    private static BlockState copySharedProperties(BlockState from, BlockState to) {
        var toDef = to.getBlock().getStateDefinition();
        for (Property<?> fromProp : from.getProperties()) {
            Property<?> toProp = toDef.getProperty(fromProp.getName());
            if (toProp == null) continue;
            String value = from.getValue(fromProp).toString();
            to = setPropertyFromString(to, toProp, value);
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState setPropertyFromString(BlockState state, Property<T> prop, String value) {
        return prop.getValue(value).map(v -> state.setValue(prop, v)).orElse(state);
    }
}
