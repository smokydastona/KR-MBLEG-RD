package com.kruemblegard.world.feature;

import com.kruemblegard.block.FranchDecay;
import com.kruemblegard.init.ModBlocks;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class WayrootSchematicFeature extends Feature<WayrootSchematicConfiguration> {
    public WayrootSchematicFeature(Codec<WayrootSchematicConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WayrootSchematicConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource random = ctx.random();
        WayrootSchematicConfiguration cfg = ctx.config();

        MinecraftServer server = level.getLevel().getServer();
        if (server == null) {
            return false;
        }

        CompoundTag root;
        try {
            root = loadSchematic(server.getResourceManager(), cfg.schematic());
        } catch (IOException e) {
            return false;
        }

        SpongeSchematic schematic;
        try {
            schematic = SpongeSchematic.from(root);
        } catch (RuntimeException e) {
            return false;
        }

        BlockPos center = origin;
        BlockState originState = level.getBlockState(center);
        if (!originState.isAir() && !originState.canBeReplaced()) {
            center = center.above();
        }

        // Don't generate submerged trees.
        if (!level.getFluidState(center).isEmpty()) {
            return false;
        }

        int rot = random.nextInt(4);
        return schematic.place(level, center, rot);
    }

    private static CompoundTag loadSchematic(ResourceManager resourceManager, ResourceLocation location) throws IOException {
        Resource resource = resourceManager.getResource(location).orElseThrow();
        try (InputStream in = resource.open()) {
            return NbtIo.readCompressed(in);
        }
    }

    private static final class SpongeSchematic {
        private final int width;
        private final int height;
        private final int length;
        private final int[] paletteIndices;
        private final BlockState[] palette;

        private SpongeSchematic(int width, int height, int length, int[] paletteIndices, BlockState[] palette) {
            this.width = width;
            this.height = height;
            this.length = length;
            this.paletteIndices = paletteIndices;
            this.palette = palette;
        }

        static SpongeSchematic from(CompoundTag root) {
            int width = root.getInt("Width");
            int height = root.getInt("Height");
            int length = root.getInt("Length");
            if (width <= 0 || height <= 0 || length <= 0) {
                throw new IllegalArgumentException("Invalid schematic size");
            }

            CompoundTag paletteTag = root.getCompound("Palette");
            if (paletteTag.isEmpty()) {
                throw new IllegalArgumentException("Missing/empty Palette");
            }

            Map<Integer, BlockState> idToState = new HashMap<>();
            for (String key : paletteTag.getAllKeys()) {
                int id = paletteTag.getInt(key);
                idToState.put(id, parseBlockStateString(key));
            }

            int paletteMax = root.contains("PaletteMax")
                    ? root.getInt("PaletteMax")
                    : (idToState.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1);

            BlockState[] palette = new BlockState[paletteMax];
            for (var entry : idToState.entrySet()) {
                int id = entry.getKey();
                if (id < 0 || id >= palette.length) continue;
                palette[id] = entry.getValue();
            }

            int volume = width * height * length;
            byte[] blockData = root.getByteArray("BlockData");
            int[] indices = decodeVarints(blockData, volume);

            return new SpongeSchematic(width, height, length, indices, palette);
        }

        boolean place(LevelAccessor level, BlockPos center, int rot) {
            boolean placedAny = false;

            int pivotX = width / 2;
            int pivotZ = length / 2;

            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        int idx = index(x, y, z);
                        int paletteId = paletteIndices[idx];
                        if (paletteId < 0 || paletteId >= palette.length) continue;

                        BlockState state = palette[paletteId];
                        if (state == null || state.isAir()) continue;

                        BlockState mapped = mapState(state);
                        if (mapped == null || mapped.isAir() || mapped.is(Blocks.STRUCTURE_VOID)) {
                            continue;
                        }

                        BlockPos p = applyRotation(center, x, y, z, rot, pivotX, pivotZ);
                        if (level.isOutsideBuildHeight(p)) {
                            continue;
                        }

                        if (!canReplace(level, p, mapped)) {
                            continue;
                        }

                        level.setBlock(p, mapped, 2);
                        placedAny = true;
                    }
                }
            }

            return placedAny;
        }

        private int index(int x, int y, int z) {
            // Sponge schematic v2: X changes fastest, then Z, then Y.
            return (y * length + z) * width + x;
        }

        private static BlockPos applyRotation(BlockPos center, int x, int y, int z, int rot, int pivotX, int pivotZ) {
            int dx = x - pivotX;
            int dz = z - pivotZ;
            return switch (rot & 3) {
                case 1 -> center.offset(dz, y, -dx);
                case 2 -> center.offset(-dx, y, -dz);
                case 3 -> center.offset(-dz, y, dx);
                default -> center.offset(dx, y, dz);
            };
        }

        private static boolean canReplace(LevelAccessor level, BlockPos p, BlockState toPlace) {
            BlockState existing = level.getBlockState(p);
            if (existing.isAir()) return true;
            if (existing.canBeReplaced()) return true;
            if (existing.getBlock() instanceof LeavesBlock) return true;
            if (existing.getBlock() instanceof BushBlock) return true;

            boolean isWayrootTrunk = toPlace.is(ModBlocks.WAYROOT_LOG.get()) || toPlace.is(ModBlocks.WAYROOT_WOOD.get());
            if (isWayrootTrunk && (existing.is(BlockTags.DIRT) || existing.is(Blocks.GRASS_BLOCK))) {
                return true;
            }

            return false;
        }

        private static BlockState mapState(BlockState original) {
            // Placeholder: tinted glass in schems means "structure void" (skip placement).
            if (original.is(Blocks.TINTED_GLASS)) {
                return Blocks.STRUCTURE_VOID.defaultBlockState();
            }

            // Force wayroot leaves for any leaf-like block, and guarantee they are non-persistent.
            if (original.is(BlockTags.LEAVES) || original.getBlock() instanceof LeavesBlock) {
                return safeWayrootLeafState();
            }

            // Normalize stripped Wayroot trunk blocks back to their non-stripped forms.
            original = normalizeWayrootTrunkState(original);

            // Convert any wood/log to Wayroot trunk.
            if (original.is(BlockTags.LOGS) || original.getBlock() instanceof RotatedPillarBlock) {
                Block target = shouldMapAsWood(original) ? ModBlocks.WAYROOT_WOOD.get() : ModBlocks.WAYROOT_LOG.get();
                return copySharedProperties(original, target.defaultBlockState());
            }

            // Convert wood construction pieces into Wayroot franch variants.
            if (original.getBlock() instanceof FenceGateBlock) {
                return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_GATE.get().defaultBlockState());
            }
            if (original.getBlock() instanceof FenceBlock) {
                return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH.get().defaultBlockState());
            }
            if (original.getBlock() instanceof StairBlock) {
                return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_STAIRS.get().defaultBlockState());
            }
            if (original.getBlock() instanceof SlabBlock) {
                return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_SLAB.get().defaultBlockState());
            }
            if (original.getBlock() instanceof TrapDoorBlock) {
                return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_TRAPDOOR.get().defaultBlockState());
            }

            if (original.is(BlockTags.PLANKS)) {
                return copySharedProperties(original, ModBlocks.WAYROOT_FRANCH_PLANKS.get().defaultBlockState());
            }

            // Leave everything else as-is.
            return original;
        }

        private static boolean shouldMapAsWood(BlockState state) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            String path = id == null ? "" : id.getPath();
            return path.endsWith("_wood") || path.contains("stripped_") && path.endsWith("_wood");
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

        private static BlockState normalizeWayrootTrunkState(BlockState state) {
            if (state.is(ModBlocks.STRIPPED_WAYROOT_LOG.get())) {
                return copyAxisIfPresent(state, ModBlocks.WAYROOT_LOG.get().defaultBlockState());
            }
            if (state.is(ModBlocks.STRIPPED_WAYROOT_WOOD.get())) {
                return copyAxisIfPresent(state, ModBlocks.WAYROOT_WOOD.get().defaultBlockState());
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

        private static int[] decodeVarints(byte[] data, int expectedCount) {
            int[] out = new int[expectedCount];
            int outIdx = 0;
            int i = 0;
            while (i < data.length && outIdx < expectedCount) {
                int value = 0;
                int shift = 0;
                while (true) {
                    if (i >= data.length) {
                        throw new IllegalArgumentException("Unexpected end of BlockData while decoding varint");
                    }
                    int b = data[i++] & 0xFF;
                    value |= (b & 0x7F) << shift;
                    if ((b & 0x80) == 0) {
                        break;
                    }
                    shift += 7;
                    if (shift > 35) {
                        throw new IllegalArgumentException("Varint too long");
                    }
                }
                out[outIdx++] = value;
            }
            if (outIdx != expectedCount) {
                throw new IllegalArgumentException("Decoded " + outIdx + " palette indices; expected " + expectedCount);
            }
            return out;
        }

        private static BlockState parseBlockStateString(String stateString) {
            String name;
            Map<String, String> props = new HashMap<>();

            int bracket = stateString.indexOf('[');
            if (bracket >= 0 && stateString.endsWith("]")) {
                name = stateString.substring(0, bracket);
                String inner = stateString.substring(bracket + 1, stateString.length() - 1);
                if (!inner.isBlank()) {
                    for (String part : inner.split(",")) {
                        int eq = part.indexOf('=');
                        if (eq <= 0) continue;
                        props.put(part.substring(0, eq).trim(), part.substring(eq + 1).trim());
                    }
                }
            } else {
                name = stateString;
            }

            ResourceLocation id;
            try {
                id = new ResourceLocation(name);
            } catch (Exception e) {
                return Blocks.AIR.defaultBlockState();
            }

            var block = BuiltInRegistries.BLOCK.get(id);
            if (block == Blocks.AIR) {
                return Blocks.AIR.defaultBlockState();
            }

            BlockState state = block.defaultBlockState();
            var def = block.getStateDefinition();
            for (var entry : props.entrySet()) {
                var prop = def.getProperty(entry.getKey());
                if (prop == null) continue;
                state = setPropertyFromString(state, prop, entry.getValue());
            }

            return state;
        }
    }
}
