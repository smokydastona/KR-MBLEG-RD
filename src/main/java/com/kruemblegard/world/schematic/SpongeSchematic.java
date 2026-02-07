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
import java.util.Comparator;
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

    private final int width;
    private final int height;
    private final int length;
    private final List<BlockState> palette;
    private final int[] blockIndices;

    private SpongeSchematic(int width, int height, int length, List<BlockState> palette, int[] blockIndices) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.palette = palette;
        this.blockIndices = blockIndices;
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
        List<BlockPos> found = findAll(Blocks.RED_WOOL);
        if (found.isEmpty()) {
            return new BlockPos(this.width / 2, 0, this.length / 2);
        }

        int cx = this.width / 2;
        int cz = this.length / 2;
        return found.stream()
            .min(Comparator
                .<BlockPos>comparingInt(p -> p.getY())
                .thenComparingInt(p -> dist2(p.getX(), p.getZ(), cx, cz)))
                .orElse(found.get(0));
    }

    public BlockPos findHighestRedStainedGlass() {
        List<BlockPos> found = findAll(Blocks.RED_STAINED_GLASS);
        if (found.isEmpty()) {
            // Fallback: treat the top layer as the "ground" reference.
            return new BlockPos(this.width / 2, Math.max(0, this.height - 1), this.length / 2);
        }
        return found.stream().max(Comparator.<BlockPos>comparingInt(p -> p.getY())).orElse(found.get(0));
    }

    private List<BlockPos> findAll(Block block) {
        List<BlockPos> out = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < this.length; z++) {
                for (int x = 0; x < this.width; x++) {
                    BlockState st = stateAt(x, y, z);
                    if (st != null && st.is(block)) {
                        out.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return out;
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
