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
import net.minecraft.world.level.block.HugeMushroomBlock;
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

        ResourceManager rm;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            rm = serverLevel.getServer().getResourceManager();
        } else if (level instanceof net.minecraft.world.level.WorldGenLevel worldGenLevel) {
            // Feature worldgen runs in a WorldGenLevel/WorldGenRegion; get its backing ServerLevel.
            rm = worldGenLevel.getLevel().getServer().getResourceManager();
        } else {
            return false;
        }

        ResourceLocation chosen = cfg.schematics().get(random.nextInt(cfg.schematics().size()));
        SpongeSchematic schematic;
        try {
            schematic = SpongeSchematic.load(rm, chosen);
        } catch (IOException e) {
            return false;
        }

        int rotationSteps = random.nextInt(4);
        Rotation rotation = Rotation.values()[rotationSteps];

        // Respect the provided origin (works for both placed-feature worldgen and bonemeal growth).
        // If the origin is a solid block (e.g., heightmap-selected ground), nudge the start up one.
        BlockPos center = origin;
        BlockState originState = level.getBlockState(center);
        if (!originState.isAir() && !originState.canBeReplaced()) {
            center = center.above();
        }

        // Place the schematic so its X/Z center is aligned to the provided origin.
        BlockPos start = schematic.startForCenter(center, rotation);

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

        static SpongeSchematic load(ResourceManager rm, ResourceLocation id) throws IOException {
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

            // Vanilla huge mushroom blocks rely on their face booleans to decide whether a given face
            // renders as "cap" (true) or "inside" (false). When placing schematics we lose those
            // per-block face settings unless we rebuild them.
            updateMushroomCapFaces(level, start, w, h, l, rot, capBlock, stemBlock);

            return true;
        }

        private static void updateMushroomCapFaces(LevelAccessor level, BlockPos start, int w, int h, int l, int rot, Block capBlock, Block stemBlock) {
            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        BlockPos p = rotate(start, x, y, z, w, l, rot);
                        BlockState state = level.getBlockState(p);

                        if (!state.is(capBlock) || !(state.getBlock() instanceof HugeMushroomBlock)) {
                            continue;
                        }

                        // Show cap texture when exposed; show inside when connected.
                        boolean up = !isCapOrStem(level, p.above(), capBlock, stemBlock);
                        boolean north = !isCapOrStem(level, p.north(), capBlock, stemBlock);
                        boolean south = !isCapOrStem(level, p.south(), capBlock, stemBlock);
                        boolean west = !isCapOrStem(level, p.west(), capBlock, stemBlock);
                        boolean east = !isCapOrStem(level, p.east(), capBlock, stemBlock);

                        // Per request: underside should always use vanilla inside texture.
                        BlockState updated = state
                                .setValue(HugeMushroomBlock.UP, up)
                                .setValue(HugeMushroomBlock.NORTH, north)
                                .setValue(HugeMushroomBlock.SOUTH, south)
                                .setValue(HugeMushroomBlock.WEST, west)
                                .setValue(HugeMushroomBlock.EAST, east)
                                .setValue(HugeMushroomBlock.DOWN, false);

                        if (updated != state) {
                            level.setBlock(p, updated, 2);
                        }
                    }
                }
            }
        }

        private static boolean isCapOrStem(LevelAccessor level, BlockPos pos, Block capBlock, Block stemBlock) {
            BlockState neighbor = level.getBlockState(pos);
            return neighbor.is(capBlock) || neighbor.is(stemBlock);
        }

        private BlockPos startForCenter(BlockPos center, Rotation rotation) {
            int rot = rotation.ordinal() & 3;

            // Floor center for even sizes; deterministic and close to vanilla feel.
            int cx = this.width / 2;
            int cz = this.length / 2;

            int dx;
            int dz;
            // These offsets match the rotate() mapping, i.e. they compute the rotated position of the
            // local (cx, cz) center in world-space relative to the start corner.
            switch (rot) {
                case 1 -> {
                    dx = (this.length - 1 - cz);
                    dz = cx;
                }
                case 2 -> {
                    dx = (this.width - 1 - cx);
                    dz = (this.length - 1 - cz);
                }
                case 3 -> {
                    dx = cz;
                    dz = (this.width - 1 - cx);
                }
                default -> {
                    dx = cx;
                    dz = cz;
                }
            }

            return center.offset(-dx, 0, -dz);
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
