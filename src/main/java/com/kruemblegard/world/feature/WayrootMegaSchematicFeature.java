package com.kruemblegard.world.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WayrootMegaSchematicFeature extends Feature<NoneFeatureConfiguration> {
    private final ResourceLocation schematicResource;

    public WayrootMegaSchematicFeature(Codec<NoneFeatureConfiguration> codec, ResourceLocation schematicResource) {
        super(codec);
        this.schematicResource = schematicResource;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource random = ctx.random();

        MinecraftServer server = level.getLevel().getServer();
        if (server == null) {
            return false;
        }

        CompoundTag root;
        try {
            root = loadSchematic(server.getResourceManager(), schematicResource);
        } catch (IOException e) {
            return false;
        }

        SpongeSchematic schematic;
        try {
            schematic = SpongeSchematic.from(root);
        } catch (RuntimeException e) {
            return false;
        }

        // Place at the surface (not underground, not on top of leaves).
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        BlockPos start = new BlockPos(origin.getX(), surfaceY, origin.getZ());

        // Quick sanity: don’t generate submerged mega trees.
        if (!level.getFluidState(start).isEmpty()) {
            return false;
        }

        // Random rotation around Y for variety.
        int rot = random.nextInt(4);

        return schematic.place(level, start, rot, random);
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

            int paletteMax = root.contains("PaletteMax") ? root.getInt("PaletteMax") : (idToState.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1);
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

        boolean place(LevelAccessor level, BlockPos start, int rot, RandomSource random) {
            boolean placedAny = false;

            // Track the lowest placed trunk/log per local X/Z column so we can build a support “beard”
            // under the schematic footprint and avoid overhangs.
            int[] minPlacedLogY = new int[width * length];
            java.util.Arrays.fill(minPlacedLogY, Integer.MAX_VALUE);

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

                        BlockState mapped = WayrootSchematicMapping.mapState(state, random);
                        if (mapped == null || mapped.isAir() || mapped.is(net.minecraft.world.level.block.Blocks.STRUCTURE_VOID)) {
                            continue;
                        }

                        BlockPos p = applyRotation(start, x, y, z, rot, pivotX, pivotZ);
                        if (level.isOutsideBuildHeight(p)) {
                            continue;
                        }

                        if (!canReplace(level, p, mapped)) {
                            continue;
                        }

                        level.setBlock(p, mapped, 2);
                        placedAny = true;

                        if (mapped.is(BlockTags.LOGS)) {
                            int col = z * width + x;
                            if (y < minPlacedLogY[col]) {
                                minPlacedLogY[col] = y;
                            }
                        }
                    }
                }
            }

            if (placedAny) {
                applySupportBeard(level, start, rot, pivotX, pivotZ, minPlacedLogY);
            }

            return placedAny;
        }

        private void applySupportBeard(LevelAccessor level, BlockPos center, int rot, int pivotX, int pivotZ, int[] minPlacedLogY) {
            final int maxDepth = 16;

            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int minY = minPlacedLogY[z * width + x];
                    if (minY == Integer.MAX_VALUE) {
                        continue;
                    }

                    BlockPos base = applyRotation(center, x, minY, z, rot, pivotX, pivotZ);
                    BlockPos cursor = base.below();
                    if (level.isOutsideBuildHeight(cursor)) {
                        continue;
                    }

                    int fillable = 0;
                    BlockState anchor = null;
                    for (int d = 0; d < maxDepth; d++) {
                        BlockPos p = cursor.below(d);
                        if (level.isOutsideBuildHeight(p)) {
                            break;
                        }

                        if (isFillableForSupport(level, p)) {
                            fillable++;
                            continue;
                        }

                        anchor = level.getBlockState(p);
                        break;
                    }

                    if (fillable <= 0) {
                        continue;
                    }

                    BlockState fillState = pickLocalFill(level, cursor, anchor);
                    for (int d = 0; d < fillable; d++) {
                        BlockPos p = cursor.below(d);
                        if (!isFillableForSupport(level, p)) {
                            break;
                        }
                        level.setBlock(p, fillState, 2);
                    }
                }
            }
        }

        private static boolean isFillableForSupport(LevelAccessor level, BlockPos pos) {
            if (!level.getFluidState(pos).isEmpty()) {
                return false;
            }

            BlockState existing = level.getBlockState(pos);
            if (existing.isAir()) return true;
            if (existing.canBeReplaced()) return true;
            if (existing.getBlock() instanceof LeavesBlock) return true;
            if (existing.getBlock() instanceof BushBlock) return true;

            return false;
        }

        private static BlockState pickLocalFill(LevelAccessor level, BlockPos reference, BlockState anchor) {
            BlockState fromAnchor = normalizeTerrainFill(anchor);
            if (fromAnchor != null) {
                return fromAnchor;
            }

            final int radius = 5;
            final int downScan = 8;

            for (int r = 0; r <= radius; r++) {
                for (int dz = -r; dz <= r; dz++) {
                    for (int dx = -r; dx <= r; dx++) {
                        BlockPos column = reference.offset(dx, 0, dz);
                        for (int dy = 0; dy <= downScan; dy++) {
                            BlockPos p = column.below(dy);
                            if (level.isOutsideBuildHeight(p)) {
                                break;
                            }
                            if (!level.getFluidState(p).isEmpty()) {
                                continue;
                            }

                            BlockState found = normalizeTerrainFill(level.getBlockState(p));
                            if (found != null) {
                                return found;
                            }
                        }
                    }
                }
            }

            return net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState();
        }

        private static BlockState normalizeTerrainFill(BlockState state) {
            if (state == null || state.isAir()) {
                return null;
            }
            if (state.canBeReplaced()) {
                return null;
            }
            if (state.getBlock() instanceof LeavesBlock) {
                return null;
            }
            if (state.getBlock() instanceof BushBlock) {
                return null;
            }
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
                return null;
            }

            if (state.is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK)
                    || state.is(net.minecraft.world.level.block.Blocks.MYCELIUM)
                    || state.is(net.minecraft.world.level.block.Blocks.PODZOL)) {
                return net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState();
            }

            return state;
        }

        private int index(int x, int y, int z) {
            // Sponge schematic v2: X changes fastest, then Z, then Y.
            return (y * length + z) * width + x;
        }

        private static BlockPos applyRotation(BlockPos center, int x, int y, int z, int rot, int pivotX, int pivotZ) {
            // Rotate around the schematic center on the X/Z plane.
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

            // Allow trunk blocks to punch into soil at the base, otherwise schematics end up missing most of their trunk.
            boolean isWayrootTrunk = toPlace.is(ModBlocks.WAYROOT_LOG.get()) || toPlace.is(ModBlocks.WAYROOT_WOOD.get());
            if (isWayrootTrunk && (existing.is(BlockTags.DIRT) || existing.is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK))) {
                return true;
            }

            // Avoid nuking terrain.
            return false;
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
            // Example: minecraft:oak_log[axis=y]
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
                return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }

            var block = BuiltInRegistries.BLOCK.get(id);
            if (block == net.minecraft.world.level.block.Blocks.AIR) {
                return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
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

        private static <T extends Comparable<T>> BlockState setPropertyFromString(BlockState state, net.minecraft.world.level.block.state.properties.Property<T> prop, String value) {
            return prop.getValue(value).map(v -> state.setValue(prop, v)).orElse(state);
        }
    }
}
