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

final class WayrootSchematicMapping {
    private static final float ASHMOSS_CARPET_PLACE_CHANCE = 0.60f;

    private WayrootSchematicMapping() {
    }

    static BlockState mapState(BlockState original, RandomSource random) {
        // Placeholder: tinted glass in schems means "structure void" (skip placement).
        if (original.is(Blocks.TINTED_GLASS)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Marker: red wool is used to mark the schematic center/pivot; it should never be placed.
        if (original.is(Blocks.RED_WOOL)) {
            return ModBlocks.WAYROOT_FRANCH_WOOD.get().defaultBlockState();
        }

        // Placeholder: use tripwire (string) in schematics to place schematic-only string franch.
        if (original.is(Blocks.TRIPWIRE)) {
            return ModBlocks.STRING_FRANCH.get().defaultBlockState();
        }

        // Optional garnish: allow ashmoss carpet in schematics, but randomly skip it to add variation.
        if (original.is(ModBlocks.ASHMOSS_CARPET.get())) {
            return (random.nextFloat() < ASHMOSS_CARPET_PLACE_CHANCE)
                    ? ModBlocks.ASHMOSS_CARPET.get().defaultBlockState()
                    : Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Force wayroot leaves for any leaf-like block, and guarantee they are non-persistent.
        // Note: some of our leaf-like blocks do not extend vanilla LeavesBlock, so also key off
        // leaf-specific properties.
        if (original.is(BlockTags.LEAVES)
                || original.getBlock() instanceof LeavesBlock
                || original.hasProperty(LeavesBlock.PERSISTENT)
                || original.hasProperty(BlockStateProperties.DISTANCE)
                || original.hasProperty(FranchDecay.DISTANCE)) {
            return safeWayrootLeafState();
        }

        // Normalize stripped Wayroot trunk blocks back to their non-stripped forms.
        original = normalizeWayrootTrunkState(original);

        // Convert any wood/log to Wayroot trunk.
        if (original.is(BlockTags.LOGS) || original.getBlock() instanceof RotatedPillarBlock) {
            BlockState chosen = chooseWayrootTrunkState(random);
            return copySharedProperties(original, chosen);
        }

        // Convert wood construction pieces into Wayroot franch variants.
        if (original.is(BlockTags.FENCE_GATES)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_GATE.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_FENCES)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_STAIRS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_STAIRS.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_SLABS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_SLAB.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_TRAPDOORS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_TRAPDOOR.get().defaultBlockState());
        }
        if (original.is(BlockTags.PLANKS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_PLANKS.get().defaultBlockState());
        }

        // Leave everything else as-is.
        return original;
    }

    static BlockState mapStateForMega(BlockState original, RandomSource random) {
        // Placeholder: tinted glass in schems means "structure void" (skip placement).
        if (original.is(Blocks.TINTED_GLASS)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Marker: red wool is used to mark the schematic center/pivot; it should never be placed.
        if (original.is(Blocks.RED_WOOL)) {
            return ModBlocks.WAYROOT_FRANCH_WOOD.get().defaultBlockState();
        }

        // Placeholder: use tripwire (string) in schematics to place schematic-only string franch.
        if (original.is(Blocks.TRIPWIRE)) {
            return ModBlocks.STRING_FRANCH.get().defaultBlockState();
        }

        // Optional garnish: allow ashmoss carpet in schematics, but randomly skip it to add variation.
        if (original.is(ModBlocks.ASHMOSS_CARPET.get())) {
            return (random.nextFloat() < ASHMOSS_CARPET_PLACE_CHANCE)
                    ? ModBlocks.ASHMOSS_CARPET.get().defaultBlockState()
                    : Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Mega schematics use a special schematic-only leaves variant with reduced drop rates.
        if (original.is(BlockTags.LEAVES)
                || original.getBlock() instanceof LeavesBlock
                || original.hasProperty(LeavesBlock.PERSISTENT)
                || original.hasProperty(BlockStateProperties.DISTANCE)
                || original.hasProperty(FranchDecay.DISTANCE)) {
            return safeWayrootMegaFranchLeafState();
        }

        // Normalize stripped Wayroot trunk blocks back to their non-stripped forms.
        original = normalizeWayrootTrunkState(original);

        // Convert any wood/log to Wayroot trunk.
        if (original.is(BlockTags.LOGS) || original.getBlock() instanceof RotatedPillarBlock) {
            BlockState chosen = chooseWayrootTrunkState(random);
            return copySharedProperties(original, chosen);
        }

        // Convert wood construction pieces into Wayroot franch variants.
        if (original.is(BlockTags.FENCE_GATES)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_GATE.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_FENCES)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_STAIRS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_STAIRS.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_SLABS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_SLAB.get().defaultBlockState());
        }
        if (original.is(BlockTags.WOODEN_TRAPDOORS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_TRAPDOOR.get().defaultBlockState());
        }
        if (original.is(BlockTags.PLANKS)) {
            return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_PLANKS.get().defaultBlockState());
        }

        return original;
    }

    private static BlockState safeWayrootLeafState() {
        BlockState state = ModBlocks.WAYROOT_LEAVES.get().defaultBlockState();

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

    private static BlockState safeWayrootMegaFranchLeafState() {
        BlockState state = ModBlocks.WAYROOT_MEGA_FRANCH_LEAVES.get().defaultBlockState();

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

    private static BlockState normalizeWayrootTrunkState(BlockState state) {
        if (state.is(ModBlocks.STRIPPED_WAYROOT_LOG.get())) {
            return copyAxisIfPresent(state, ModBlocks.WAYROOT_LOG.get().defaultBlockState());
        }
        if (state.is(ModBlocks.STRIPPED_WAYROOT_WOOD.get())) {
            return copyAxisIfPresent(state, ModBlocks.WAYROOT_WOOD.get().defaultBlockState());
        }
        return state;
    }

    private static BlockState chooseWayrootTrunkState(RandomSource random) {
        // Schematics can be authored with any wood/log. We mostly place franch wood,
        // but sprinkle in a small amount of franch planks for visual variety.
        return (random.nextFloat() < 0.10f)
                ? ModBlocks.WAYROOT_FRANCH_PLANKS.get().defaultBlockState()
                : ModBlocks.WAYROOT_FRANCH_WOOD.get().defaultBlockState();
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
