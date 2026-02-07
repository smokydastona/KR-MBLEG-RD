package com.kruemblegard.world.schematic;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Minimal reader for Sponge/WorldEdit .schem files (palette + varint BlockData).
 *
 * <p>This intentionally mirrors the per-feature schematic loaders already used by Wayfall
 * schematic features, but is reusable for structures.</p>
 */
public final class SpongeSchematic {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final java.util.concurrent.ConcurrentHashMap<ResourceLocation, SpongeSchematic> CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    private final int width;
    private final int height;
    private final int length;
    private final List<BlockState> palette;
    private final int[] blockIndices;

    private final BlockPos pivotRedWool;
    private final BlockPos highestRedStainedGlass;

    private SpongeSchematic(int width, int height, int length, List<BlockState> palette, int[] blockIndices) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.palette = palette;
        this.blockIndices = blockIndices;

        MarkerScan scan = scanMarkers(width, height, length, palette, blockIndices);
        this.pivotRedWool = scan.pivotRedWool;
        this.highestRedStainedGlass = scan.highestRedStainedGlass;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int length() {
        return length;
    }

    public int rotatedWidth(Rotation rotation) {
        int rot = rotation.ordinal() & 3;
        return (rot == 1 || rot == 3) ? this.length : this.width;
    }

    public int rotatedLength(Rotation rotation) {
        int rot = rotation.ordinal() & 3;
        return (rot == 1 || rot == 3) ? this.width : this.length;
    }

    public static SpongeSchematic load(ResourceManager rm, ResourceLocation id) throws IOException {
        Optional<Resource> resOpt = rm.getResource(id);
        if (resOpt.isEmpty()) {
            throw new IOException("missing schematic resource: " + id);
        }

        try (InputStream in = resOpt.get().open()) {
            CompoundTag root = NbtIo.readCompressed(in);
            if (root == null) {
                throw new IOException("empty schematic NBT: " + id);
            }

            int w = root.getShort("Width") & 0xFFFF;
            int h = root.getShort("Height") & 0xFFFF;
            int l = root.getShort("Length") & 0xFFFF;

            CompoundTag paletteTag = root.getCompound("Palette");
            int paletteSize = paletteTag.size();
            List<BlockState> palette = new ArrayList<>(paletteSize);
            for (int i = 0; i < paletteSize; i++) {
                palette.add(Blocks.AIR.defaultBlockState());
            }

            for (String key : paletteTag.getAllKeys()) {
                int idx = paletteTag.getInt(key);
                BlockState state = parseBlockState(key);
                if (idx >= 0 && idx < palette.size()) {
                    palette.set(idx, state);
                }
            }

            byte[] data = root.getByteArray("BlockData");
            int expected = w * h * l;
            int[] indices = decodeVarIntArray(data, expected);

            return new SpongeSchematic(w, h, l, palette, indices);
        }
    }

    /**
     * Cached version of {@link #load(ResourceManager, ResourceLocation)}.
     *
     * <p>Worldgen may call structures from chunk worker threads and can invoke the same structure
     * across many chunks. Decoding compressed NBT and scanning for markers is expensive; caching
     * avoids repeated work and reduces chunk-gen stalls.</p>
     */
    public static SpongeSchematic loadCached(ResourceManager rm, ResourceLocation id) throws IOException {
        SpongeSchematic cached = CACHE.get(id);
        if (cached != null) {
            return cached;
        }

        SpongeSchematic loaded = load(rm, id);
        SpongeSchematic existing = CACHE.putIfAbsent(id, loaded);
        return existing != null ? existing : loaded;
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public BlockState stateAt(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= length) {
            return Blocks.AIR.defaultBlockState();
        }
        int idx = ((y * this.length) + z) * this.width + x;
        int paletteIndex = this.blockIndices[idx];
        if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
            return Blocks.AIR.defaultBlockState();
        }
        return this.palette.get(paletteIndex);
    }

    public BlockPos toWorld(BlockPos start, int x, int y, int z, Rotation rotation) {
        int rot = rotation.ordinal() & 3;
        int rx;
        int rz;
        switch (rot) {
            case 1 -> {
                rx = (this.length - 1 - z);
                rz = x;
            }
            case 2 -> {
                rx = (this.width - 1 - x);
                rz = (this.length - 1 - z);
            }
            case 3 -> {
                rx = z;
                rz = (this.width - 1 - x);
            }
            default -> {
                rx = x;
                rz = z;
            }
        }
        return start.offset(rx, y, rz);
    }

    public BlockPos rotateLocalXZ(BlockPos local, Rotation rotation) {
        int x = local.getX();
        int z = local.getZ();
        int rot = rotation.ordinal() & 3;
        int rx;
        int rz;
        switch (rot) {
            case 1 -> {
                rx = (this.length - 1 - z);
                rz = x;
            }
            case 2 -> {
                rx = (this.width - 1 - x);
                rz = (this.length - 1 - z);
            }
            case 3 -> {
                rx = z;
                rz = (this.width - 1 - x);
            }
            default -> {
                rx = x;
                rz = z;
            }
        }
        return new BlockPos(rx, local.getY(), rz);
    }

    public BlockPos findPivotRedWool() {
        return this.pivotRedWool;
    }

    public BlockPos findHighestRedStainedGlass() {
        return this.highestRedStainedGlass;
    }

    private record MarkerScan(BlockPos pivotRedWool, BlockPos highestRedStainedGlass) {
    }

    private static MarkerScan scanMarkers(int width, int height, int length, List<BlockState> palette, int[] blockIndices) {
        int cx = width / 2;
        int cz = length / 2;

        BlockPos bestPivot = null;
        int bestPivotY = Integer.MAX_VALUE;
        int bestPivotDist2 = Integer.MAX_VALUE;

        BlockPos bestHighest = null;
        int bestHighestY = Integer.MIN_VALUE;

        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int paletteIndex = blockIndices[i++];
                    if (paletteIndex < 0 || paletteIndex >= palette.size()) {
                        continue;
                    }

                    BlockState st = palette.get(paletteIndex);
                    if (st == null) {
                        continue;
                    }

                    if (st.is(Blocks.RED_WOOL)) {
                        int dist2 = dist2(x, z, cx, cz);
                        if (y < bestPivotY || (y == bestPivotY && dist2 < bestPivotDist2)) {
                            bestPivotY = y;
                            bestPivotDist2 = dist2;
                            bestPivot = new BlockPos(x, y, z);
                        }
                    }

                    if (st.is(Blocks.RED_STAINED_GLASS)) {
                        if (y > bestHighestY) {
                            bestHighestY = y;
                            bestHighest = new BlockPos(x, y, z);
                        }
                    }
                }
            }
        }

        if (bestPivot == null) {
            bestPivot = new BlockPos(cx, 0, cz);
        }

        if (bestHighest == null) {
            bestHighest = new BlockPos(cx, Math.max(0, height - 1), cz);
        }

        return new MarkerScan(bestPivot, bestHighest);
    }

    private static int dist2(int ax, int az, int bx, int bz) {
        int dx = ax - bx;
        int dz = az - bz;
        return dx * dx + dz * dz;
    }

    public BlockState findAdjacentPlanksState(int x, int y, int z) {
        // Prefer horizontal adjacency ("beside it") so the marker matches the local build palette.
        int[][] offsets = new int[][]{
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 1},
                {0, 0, -1},
                {0, 1, 0},
                {0, -1, 0}
        };

        for (int[] d : offsets) {
            int nx = x + d[0];
            int ny = y + d[1];
            int nz = z + d[2];
            if (nx < 0 || ny < 0 || nz < 0 || nx >= this.width || ny >= this.height || nz >= this.length) {
                continue;
            }

            BlockState st = stateAt(nx, ny, nz);
            if (st != null && st.is(BlockTags.PLANKS)) {
                return st;
            }
        }

        return null;
    }

    private static BlockState parseBlockState(String paletteKey) {
        try {
            int bracket = paletteKey.indexOf('[');
            String blockId = bracket >= 0 ? paletteKey.substring(0, bracket) : paletteKey;

            ResourceLocation id = ResourceLocation.tryParse(blockId);
            if (id == null) {
                return Blocks.AIR.defaultBlockState();
            }

            Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(id);
            if (block == Blocks.AIR && !id.equals(Blocks.AIR.builtInRegistryHolder().key().location())) {
                return Blocks.AIR.defaultBlockState();
            }

            BlockState state = block.defaultBlockState();
            if (bracket < 0) {
                return state;
            }

            int end = paletteKey.indexOf(']');
            if (end < 0) {
                return state;
            }

            String props = paletteKey.substring(bracket + 1, end);
            if (props.isEmpty()) {
                return state;
            }

            for (String part : props.split(",")) {
                String[] kv = part.split("=", 2);
                if (kv.length != 2) {
                    continue;
                }
                String name = kv[0];
                String value = kv[1];

                Property<?> prop = state.getProperties().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
                if (prop == null) {
                    continue;
                }
                state = setPropertyFromString(state, prop, value);
            }

            return state;
        } catch (Exception e) {
            LOGGER.warn("Failed to parse schematic palette state: {}", paletteKey);
            return Blocks.AIR.defaultBlockState();
        }
    }

    private static <T extends Comparable<T>> BlockState setPropertyFromString(BlockState state, Property<T> prop, String value) {
        return prop.getValue(value).map(v -> state.setValue(prop, v)).orElse(state);
    }

    private static int[] decodeVarIntArray(byte[] data, int expectedCount) throws IOException {
        int[] out = new int[expectedCount];
        int outIndex = 0;
        int i = 0;

        while (i < data.length && outIndex < expectedCount) {
            int value = 0;
            int position = 0;

            while (true) {
                if (i >= data.length) {
                    throw new IOException("invalid varint block data");
                }

                int b = data[i++] & 0xFF;
                value |= (b & 0x7F) << position;

                if ((b & 0x80) == 0) {
                    break;
                }

                position += 7;
                if (position > 35) {
                    throw new IOException("varint too long");
                }
            }

            out[outIndex++] = value;
        }

        if (outIndex != expectedCount) {
            throw new IOException("unexpected blockdata length; expected " + expectedCount + " entries, got " + outIndex);
        }

        return out;
    }
}
