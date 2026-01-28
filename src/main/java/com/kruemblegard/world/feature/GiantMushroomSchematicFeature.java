package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GiantMushroomSchematicFeature extends Feature<GiantMushroomSchematicConfiguration> {
    public GiantMushroomSchematicFeature(Codec<GiantMushroomSchematicConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GiantMushroomSchematicConfiguration> ctx) {
        LevelAccessor level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos origin = ctx.origin();
        GiantMushroomSchematicConfiguration cfg = ctx.config();

        if (cfg.schematics().isEmpty()) {
            return false;
        }

        ResourceLocation chosen = cfg.schematics().get(random.nextInt(cfg.schematics().size()));
        SpongeSchematic schematic;
        try {
            schematic = SpongeSchematic.load(level, chosen);
        } catch (IOException e) {
            return false;
        }

        int rotationSteps = random.nextInt(4);
        Rotation rotation = Rotation.values()[rotationSteps];

        // Respect the provided origin (works for both placed-feature worldgen and bonemeal growth).
        // If the origin is a solid block (e.g., heightmap-selected ground), nudge the start up one.
        BlockPos start = origin;
        BlockState originState = level.getBlockState(start);
        if (!originState.isAir() && !originState.canBeReplaced()) {
            start = start.above();
        }

        return schematic.place(level, start, rotation, cfg.capBlock(), cfg.stemBlock(), cfg.slabBlock());
    }

    private static final class SpongeSchematic {
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

        static SpongeSchematic load(LevelAccessor level, ResourceLocation id) throws IOException {
            if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                throw new IOException("schematic placement requires server level");
            }

            ResourceManager rm = serverLevel.getServer().getResourceManager();
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

        boolean place(LevelAccessor level, BlockPos start, Rotation rotation, Block capBlock, Block stemBlock, Block slabBlock) {
            int rot = rotation.ordinal();
            int w = this.width;
            int h = this.height;
            int l = this.length;

            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        int index = ((y * l) + z) * w + x;
                        int paletteIndex = this.blockIndices[index];
                        if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                            continue;
                        }

                        BlockState state = this.palette.get(paletteIndex);
                        BlockState mapped = mapState(state, capBlock, stemBlock, slabBlock);
                        if (mapped == null || mapped.isAir() || mapped.is(Blocks.STRUCTURE_VOID)) {
                            continue;
                        }

                        BlockPos p = rotate(start, x, y, z, w, l, rot);
                        if (!canReplace(level, p, mapped, stemBlock)) {
                            return false;
                        }
                    }
                }
            }

            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        int index = ((y * l) + z) * w + x;
                        int paletteIndex = this.blockIndices[index];
                        if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                            continue;
                        }

                        BlockState state = this.palette.get(paletteIndex);
                        BlockState mapped = mapState(state, capBlock, stemBlock, slabBlock);
                        if (mapped == null || mapped.isAir() || mapped.is(Blocks.STRUCTURE_VOID)) {
                            continue;
                        }

                        BlockPos p = rotate(start, x, y, z, w, l, rot);
                        level.setBlock(p, mapped, 3);
                    }
                }
            }

            return true;
        }

        private static BlockPos rotate(BlockPos start, int x, int y, int z, int w, int l, int rot) {
            return switch (rot & 3) {
                case 1 -> start.offset(l - 1 - z, y, x);
                case 2 -> start.offset(w - 1 - x, y, l - 1 - z);
                case 3 -> start.offset(z, y, w - 1 - x);
                default -> start.offset(x, y, z);
            };
        }

        private static BlockState mapState(BlockState state, Block capBlock, Block stemBlock, Block slabBlock) {
            if (state.is(Blocks.TINTED_GLASS)) {
                return null;
            }

            if (state.is(Blocks.QUARTZ_BLOCK)) {
                return stemBlock.defaultBlockState();
            }

            if (state.is(Blocks.QUARTZ_SLAB)) {
                return copyMatchingProperties(state, slabBlock.defaultBlockState());
            }

            if (state.is(Blocks.BROWN_MUSHROOM_BLOCK)) {
                return copyMatchingProperties(state, capBlock.defaultBlockState());
            }

            return state;
        }

        private static boolean canReplace(LevelAccessor level, BlockPos p, BlockState toPlace, Block stemBlock) {
            BlockState existing = level.getBlockState(p);
            if (existing.isAir() || existing.canBeReplaced()) {
                return true;
            }

            if (existing.is(BlockTags.LEAVES) || existing.is(BlockTags.REPLACEABLE_BY_TREES)) {
                return true;
            }

            if (toPlace.is(stemBlock) && (existing.is(BlockTags.DIRT) || existing.is(Blocks.GRASS_BLOCK) || existing.is(Blocks.MYCELIUM))) {
                return true;
            }

            return false;
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
                if (block == Blocks.AIR && !id.equals(net.minecraft.world.level.block.Blocks.AIR.builtInRegistryHolder().key().location())) {
                    // Unknown block ID
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
                return Blocks.AIR.defaultBlockState();
            }
        }

        private static <T extends Comparable<T>> BlockState setPropertyFromString(BlockState state, Property<T> prop, String value) {
            Optional<T> parsed = prop.getValue(value);
            return parsed.map(v -> state.setValue(prop, v)).orElse(state);
        }

        private static BlockState copyMatchingProperties(BlockState from, BlockState to) {
            for (Property<?> prop : from.getProperties()) {
                Property<?> targetProp = to.getBlock().getStateDefinition().getProperty(prop.getName());
                if (targetProp == null) {
                    continue;
                }
                to = copyPropertyValue(from, to, prop, targetProp);
            }
            return to;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static BlockState copyPropertyValue(BlockState from, BlockState to, Property fromProp, Property toProp) {
            if (!fromProp.getValueClass().equals(toProp.getValueClass())) {
                return to;
            }
            Comparable val = (Comparable) from.getValue(fromProp);
            return to.setValue(toProp, val);
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
}
