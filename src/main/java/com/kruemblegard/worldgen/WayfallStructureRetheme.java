package com.kruemblegard.worldgen;

import com.kruemblegard.init.ModBlocks;

import java.util.Objects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class WayfallStructureRetheme {
    private WayfallStructureRetheme() {}

    public static final float DEFAULT_KEEP_VANILLA_WOOD_CHANCE = 0.25f;

    /**
     * Extremely hot path (chunk-load retheme): only run shipwreck retheme logic for blocks that can actually change.
     */
    public static boolean isVanillaShipwreckWoodCandidate(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!"minecraft".equals(id.getNamespace())) {
            return false;
        }

        return state.is(BlockTags.PLANKS)
                || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.WOODEN_FENCES)
                || state.is(BlockTags.FENCE_GATES)
                || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_TRAPDOORS)
                || state.is(BlockTags.WOODEN_BUTTONS)
                || state.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || state.is(BlockTags.LOGS);
    }

    /**
     * Extremely hot path (chunk-load retheme): only run jungle-temple retheme logic for blocks that can actually change.
     */
    public static boolean isJungleTempleStoneCandidate(BlockState state) {
        return state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.CHISELED_STONE_BRICKS)
                || state.is(Blocks.STONE_BRICKS)
                || state.is(Blocks.CRACKED_STONE_BRICKS)
                || state.is(Blocks.MOSSY_STONE_BRICKS)
                || state.is(Blocks.STONE_BRICK_STAIRS)
                || state.is(Blocks.STONE_BRICK_SLAB)
                || state.is(Blocks.STONE_BRICK_WALL)
                || state.is(Blocks.COBBLESTONE_STAIRS)
                || state.is(Blocks.COBBLESTONE_SLAB)
                || state.is(Blocks.COBBLESTONE_WALL);
    }

    /**
     * Deterministic pseudo-random decision for "keep vanilla wood" that avoids per-block RandomSource overhead.
     */
    public static boolean shouldKeepVanillaWood(long baseSeed, int x, int y, int z, float keepVanillaChance) {
        if (keepVanillaChance <= 0.0f) {
            return false;
        }
        if (keepVanillaChance >= 1.0f) {
            return true;
        }

        long h = mix64(baseSeed ^ packPos(x, y, z));
        // 24-bit fraction in [0, 1)
        float f = ((h >>> 40) & 0xFFFFFFL) / (float) 0x1000000;
        return f < keepVanillaChance;
    }

    public record WoodPalette(
            Block planks,
            Block stairs,
            Block slab,
            Block fence,
            Block fenceGate,
            Block door,
            Block trapdoor,
            Block button,
            Block pressurePlate,
            Block log,
            Block strippedLog
    ) {}

    public static WoodPalette woodPaletteForBiome(@SuppressWarnings("unused") ResourceKey<Biome> biomeKey) {
        Objects.requireNonNull(biomeKey, "biomeKey");

        if (biomeKey.equals(ModWorldgenKeys.Biomes.BETWEENLIGHT_VOID) || biomeKey.equals(ModWorldgenKeys.Biomes.HOLLOW_TRANSIT_PLAINS)) {
            return hollowwayTree();
        }

        if (biomeKey.equals(ModWorldgenKeys.Biomes.DRIFTWAY_CHASM) || biomeKey.equals(ModWorldgenKeys.Biomes.CRUMBLED_CROSSING)) {
            return driftwillow();
        }

        if (biomeKey.equals(ModWorldgenKeys.Biomes.RIVEN_CAUSEWAYS)) {
            return faultwood();
        }

        if (biomeKey.equals(ModWorldgenKeys.Biomes.UNDERWAY_FALLS)) {
            return waytorchTree();
        }

        // Fallback: a staple Wayfall palette.
        return driftwood();
    }

    public static BlockState rethemeShipwreckBlock(BlockState original, WoodPalette palette, boolean keepVanilla) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(original.getBlock());
        if (!"minecraft".equals(id.getNamespace())) {
            return original;
        }

        if (keepVanilla) {
            return original;
        }

        Block targetBlock;
        if (original.is(BlockTags.PLANKS)) {
            targetBlock = palette.planks();
        } else if (original.is(BlockTags.WOODEN_STAIRS)) {
            targetBlock = palette.stairs();
        } else if (original.is(BlockTags.WOODEN_SLABS)) {
            targetBlock = palette.slab();
        } else if (original.is(BlockTags.WOODEN_FENCES)) {
            targetBlock = palette.fence();
        } else if (original.is(BlockTags.FENCE_GATES)) {
            targetBlock = palette.fenceGate();
        } else if (original.is(BlockTags.WOODEN_DOORS)) {
            targetBlock = palette.door();
        } else if (original.is(BlockTags.WOODEN_TRAPDOORS)) {
            targetBlock = palette.trapdoor();
        } else if (original.is(BlockTags.WOODEN_BUTTONS)) {
            targetBlock = palette.button();
        } else if (original.is(BlockTags.WOODEN_PRESSURE_PLATES)) {
            targetBlock = palette.pressurePlate();
        } else if (original.is(BlockTags.LOGS)) {
            boolean isStrippedLog = id.getPath().startsWith("stripped_") && id.getPath().endsWith("_log");
            targetBlock = isStrippedLog ? palette.strippedLog() : palette.log();
        } else {
            return original;
        }

        return copyProperties(original, targetBlock.defaultBlockState());
    }

    public static BlockState rethemeJungleTempleStone(BlockState original) {
        Block targetBlock = null;
        if (original.is(Blocks.COBBLESTONE)) {
            targetBlock = ModBlocks.SCARSTONE.get();
        } else if (original.is(Blocks.MOSSY_COBBLESTONE)) {
            targetBlock = ModBlocks.CRACKED_SCARSTONE.get();
        } else if (original.is(Blocks.CHISELED_STONE_BRICKS)) {
            targetBlock = ModBlocks.CHISELED_SCARSTONE.get();
        } else if (original.is(Blocks.STONE_BRICKS)) {
            targetBlock = ModBlocks.POLISHED_SCARSTONE.get();
        } else if (original.is(Blocks.CRACKED_STONE_BRICKS)) {
            targetBlock = ModBlocks.CRACKED_SCARSTONE.get();
        } else if (original.is(Blocks.MOSSY_STONE_BRICKS)) {
            targetBlock = ModBlocks.CRACKED_SCARSTONE.get();
        } else if (original.is(Blocks.STONE_BRICK_STAIRS)) {
            targetBlock = ModBlocks.POLISHED_SCARSTONE_STAIRS.get();
        } else if (original.is(Blocks.STONE_BRICK_SLAB)) {
            targetBlock = ModBlocks.POLISHED_SCARSTONE_SLAB.get();
        } else if (original.is(Blocks.STONE_BRICK_WALL)) {
            targetBlock = ModBlocks.POLISHED_SCARSTONE_WALL.get();
        } else if (original.is(Blocks.COBBLESTONE_STAIRS)) {
            targetBlock = ModBlocks.SCARSTONE_STAIRS.get();
        } else if (original.is(Blocks.COBBLESTONE_SLAB)) {
            targetBlock = ModBlocks.SCARSTONE_SLAB.get();
        } else if (original.is(Blocks.COBBLESTONE_WALL)) {
            targetBlock = ModBlocks.SCARSTONE_WALL.get();
        }

        if (targetBlock == null) {
            return original;
        }

        return copyProperties(original, targetBlock.defaultBlockState());

    }

    private static WoodPalette driftwillow() {
        return new WoodPalette(
                ModBlocks.DRIFTWILLOW_PLANKS.get(),
                ModBlocks.DRIFTWILLOW_STAIRS.get(),
                ModBlocks.DRIFTWILLOW_SLAB.get(),
                ModBlocks.DRIFTWILLOW_FENCE.get(),
                ModBlocks.DRIFTWILLOW_FENCE_GATE.get(),
                ModBlocks.DRIFTWILLOW_DOOR.get(),
                ModBlocks.DRIFTWILLOW_TRAPDOOR.get(),
                ModBlocks.DRIFTWILLOW_BUTTON.get(),
                ModBlocks.DRIFTWILLOW_PRESSURE_PLATE.get(),
                ModBlocks.DRIFTWILLOW_LOG.get(),
                ModBlocks.STRIPPED_DRIFTWILLOW_LOG.get()
        );
    }

    private static WoodPalette driftwood() {
        return new WoodPalette(
                ModBlocks.DRIFTWOOD_PLANKS.get(),
                ModBlocks.DRIFTWOOD_STAIRS.get(),
                ModBlocks.DRIFTWOOD_SLAB.get(),
                ModBlocks.DRIFTWOOD_FENCE.get(),
                ModBlocks.DRIFTWOOD_FENCE_GATE.get(),
                ModBlocks.DRIFTWOOD_DOOR.get(),
                ModBlocks.DRIFTWOOD_TRAPDOOR.get(),
                ModBlocks.DRIFTWOOD_BUTTON.get(),
                ModBlocks.DRIFTWOOD_PRESSURE_PLATE.get(),
                ModBlocks.DRIFTWOOD_LOG.get(),
                ModBlocks.STRIPPED_DRIFTWOOD_LOG.get()
        );
    }

    private static WoodPalette faultwood() {
        return new WoodPalette(
                ModBlocks.FAULTWOOD_PLANKS.get(),
                ModBlocks.FAULTWOOD_STAIRS.get(),
                ModBlocks.FAULTWOOD_SLAB.get(),
                ModBlocks.FAULTWOOD_FENCE.get(),
                ModBlocks.FAULTWOOD_FENCE_GATE.get(),
                ModBlocks.FAULTWOOD_DOOR.get(),
                ModBlocks.FAULTWOOD_TRAPDOOR.get(),
                ModBlocks.FAULTWOOD_BUTTON.get(),
                ModBlocks.FAULTWOOD_PRESSURE_PLATE.get(),
                ModBlocks.FAULTWOOD_LOG.get(),
                ModBlocks.STRIPPED_FAULTWOOD_LOG.get()
        );
    }

    private static WoodPalette hollowwayTree() {
        return new WoodPalette(
                ModBlocks.HOLLOWWAY_TREE_PLANKS.get(),
                ModBlocks.HOLLOWWAY_TREE_STAIRS.get(),
                ModBlocks.HOLLOWWAY_TREE_SLAB.get(),
                ModBlocks.HOLLOWWAY_TREE_FENCE.get(),
                ModBlocks.HOLLOWWAY_TREE_FENCE_GATE.get(),
                ModBlocks.HOLLOWWAY_TREE_DOOR.get(),
                ModBlocks.HOLLOWWAY_TREE_TRAPDOOR.get(),
                ModBlocks.HOLLOWWAY_TREE_BUTTON.get(),
                ModBlocks.HOLLOWWAY_TREE_PRESSURE_PLATE.get(),
                ModBlocks.HOLLOWWAY_TREE_LOG.get(),
                ModBlocks.STRIPPED_HOLLOWWAY_TREE_LOG.get()
        );
    }

    private static WoodPalette waytorchTree() {
        return new WoodPalette(
                ModBlocks.WAYTORCH_TREE_PLANKS.get(),
                ModBlocks.WAYTORCH_TREE_STAIRS.get(),
                ModBlocks.WAYTORCH_TREE_SLAB.get(),
                ModBlocks.WAYTORCH_TREE_FENCE.get(),
                ModBlocks.WAYTORCH_TREE_FENCE_GATE.get(),
                ModBlocks.WAYTORCH_TREE_DOOR.get(),
                ModBlocks.WAYTORCH_TREE_TRAPDOOR.get(),
                ModBlocks.WAYTORCH_TREE_BUTTON.get(),
                ModBlocks.WAYTORCH_TREE_PRESSURE_PLATE.get(),
                ModBlocks.WAYTORCH_TREE_LOG.get(),
                ModBlocks.STRIPPED_WAYTORCH_TREE_LOG.get()
        );
    }

    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (Property<?> prop : from.getProperties()) {
            if (to.hasProperty(prop)) {
                to = copyPropertyUnchecked(from, to, prop);
            }
        }
        return to;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState copyPropertyUnchecked(BlockState from, BlockState to, Property prop) {
        return to.setValue(prop, from.getValue(prop));
    }

    private static long packPos(int x, int y, int z) {
        // Simple reversible-ish packing for hashing (not used for storage).
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) z & 0x3FFFFFFL) << 12 | ((long) y & 0xFFFL);
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }
}
