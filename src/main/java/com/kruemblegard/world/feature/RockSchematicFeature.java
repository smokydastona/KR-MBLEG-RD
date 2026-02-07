package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class RockSchematicFeature extends Feature<RockSchematicConfiguration> {
    private static final ConcurrentHashMap<ResourceLocation, List<ResourceLocation>> SCHEM_CACHE = new ConcurrentHashMap<>();

    private static final TagKey<Block> ROCK_BURY_REPLACEABLE = TagKey.create(
            Registries.BLOCK,
            new ResourceLocation("kruemblegard", "rock_bury_replaceable")
    );

    public RockSchematicFeature(Codec<RockSchematicConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RockSchematicConfiguration> ctx) {
        LevelAccessor level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos origin = ctx.origin();
        RockSchematicConfiguration cfg = ctx.config();

        ResourceManager rm = getResourceManager(level);
        if (rm == null) {
            return false;
        }

        List<ResourceLocation> choices = SCHEM_CACHE.computeIfAbsent(cfg.schematicRoot(), root -> listSchematics(rm, root));
        if (choices.isEmpty()) {
            return false;
        }

        var biomeHolder = level.getBiome(origin);
        Biome biome = biomeHolder.value();
        ResourceLocation biomeId = biomeHolder.unwrapKey().map(ResourceKey::location).orElse(null);

        int rarity = computeBiomeRarity(cfg.baseRarity(), cfg.biomeRaritySpread(), biomeId, biome.getBaseTemperature());
        if (random.nextInt(rarity) != 0) {
            return false;
        }

        ResourceLocation chosen = choices.get(random.nextInt(choices.size()));
        ResourceLocation mainBlockId = inferMainBlockId(cfg.schematicRoot(), chosen);

        SpongeSchematic schematic;
        try {
            schematic = SpongeSchematic.load(rm, chosen);
        } catch (IOException e) {
            return false;
        }

        int bury = chooseBurial(random, cfg.maxBurial(), schematic.nonAirHeight());

        BlockPos center = origin;
        BlockState originState = level.getBlockState(center);
        if (!originState.isAir() && !originState.canBeReplaced()) {
            center = center.above();
        }
        if (bury > 0) {
            center = center.below(bury);
        }

        if (!level.getFluidState(center).isEmpty()) {
            return false;
        }

        Rotation rotation = Rotation.values()[random.nextInt(4)];
        BlockPos start = schematic.startForCenter(center, rotation);

        return schematic.place(level, start, rotation, random, mainBlockId, biome.getBaseTemperature(), cfg);
    }

    private static int chooseBurial(RandomSource random, int maxBurial, int nonAirHeight) {
        if (maxBurial <= 0) {
            return 0;
        }

        // If we bury by < nonAirHeight, at least one schematic layer remains above ground.
        int exposedMax = Math.min(maxBurial, Math.max(0, nonAirHeight - 1));

        // If we bury by <= nonAirHeight-2, at least two schematic layers remain above ground.
        int exposed2PlusMax = Math.min(maxBurial, Math.max(0, nonAirHeight - 2));
        int fullMin = Math.min(maxBurial, Math.max(0, nonAirHeight));

        // Distribution goals:
        // - Sometimes sit cleanly on the surface.
        // - Most of the time, if buried, keep at least two layers exposed.
        // - Sometimes allow only one layer exposed.
        // - Occasionally allow fully-buried rocks (when possible).
        int roll = random.nextInt(100);

        // 0..14%: clean surface placement
        if (roll < 15) {
            return 0;
        }

        // 15..79%: buried but keep 2+ layers exposed (if possible)
        if (exposed2PlusMax >= 1 && roll < 80) {
            return 1 + random.nextInt(exposed2PlusMax);
        }

        // 80..94%: buried but only 1 layer exposed (if possible)
        if (exposedMax >= 1 && roll < 95) {
            int min = Math.max(1, exposed2PlusMax + 1);
            int max = exposedMax;
            if (min <= max) {
                return min + random.nextInt(max - min + 1);
            }
            return 1 + random.nextInt(exposedMax);
        }

        // 95..99%: fully buried (only if possible)
        if (fullMin > 0 && fullMin <= maxBurial) {
            return fullMin + random.nextInt(maxBurial - fullMin + 1);
        }

        // Fallback: no meaningful schematic height info; keep old behavior.
        return random.nextInt(maxBurial + 1);
    }

    private static ResourceManager getResourceManager(LevelAccessor level) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return serverLevel.getServer().getResourceManager();
        }
        if (level instanceof WorldGenLevel worldGenLevel) {
            return worldGenLevel.getLevel().getServer().getResourceManager();
        }
        return null;
    }

    private static List<ResourceLocation> listSchematics(ResourceManager rm, ResourceLocation root) {
        try {
            Map<ResourceLocation, Resource> found = rm.listResources(
                    root.getPath(),
                    id -> id.getNamespace().equals(root.getNamespace()) && id.getPath().endsWith(".schem")
            );

            if (found.isEmpty()) {
                return List.of();
            }

            List<ResourceLocation> out = new ArrayList<>(found.keySet());
            Collections.sort(out);
            return out;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private static int computeBiomeRarity(int base, int spread, ResourceLocation biomeId, float biomeTemp) {
        if (spread <= 0) {
            return Math.max(1, base);
        }

        int hash = biomeId != null ? biomeId.hashCode() : Float.floatToIntBits(biomeTemp);
        int delta = Math.floorMod(hash, (spread * 2) + 1) - spread;
        return Math.max(1, base + delta);
    }

    private static ResourceLocation inferMainBlockId(ResourceLocation root, ResourceLocation schematicId) {
        String rootPath = root.getPath();
        String path = schematicId.getPath();

        // Expected: <rootPath>/<group>/<file>.schem
        if (!path.startsWith(rootPath + "/")) {
            return null;
        }

        String rel = path.substring((rootPath + "/").length());
        int slash = rel.indexOf('/');
        if (slash <= 0) {
            return null;
        }

        String group = rel.substring(0, slash);

        // Convention used by the examples: "minecraft_granite" => minecraft:granite
        int underscore = group.indexOf('_');
        if (underscore > 0) {
            String ns = group.substring(0, underscore);
            String idPath = group.substring(underscore + 1);
            ResourceLocation parsed = ResourceLocation.tryParse(ns + ":" + idPath);
            if (parsed != null) {
                return parsed;
            }
        }

        // Fallback: treat it as a vanilla ID path.
        return ResourceLocation.tryParse("minecraft:" + group);
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

        int nonAirHeight() {
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (int y = 0; y < this.height; y++) {
                for (int z = 0; z < this.length; z++) {
                    for (int x = 0; x < this.width; x++) {
                        int idx = ((y * this.length) + z) * this.width + x;
                        int paletteIndex = this.blockIndices[idx];
                        if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                            continue;
                        }

                        BlockState state = this.palette.get(paletteIndex);
                        if (!isPlaceableSchematicState(state)) {
                            continue;
                        }

                        if (y < minY) {
                            minY = y;
                        }
                        if (y > maxY) {
                            maxY = y;
                        }
                    }
                }
            }

            if (minY == Integer.MAX_VALUE) {
                return this.height;
            }

            return (maxY - minY) + 1;
        }

        private static boolean isPlaceableSchematicState(BlockState state) {
            if (state == null || state.isAir()) {
                return false;
            }
            return !(state.is(Blocks.TINTED_GLASS) || state.is(Blocks.STRUCTURE_VOID));
        }

        BlockPos startForCenter(BlockPos center, Rotation rotation) {
            int rot = rotation.ordinal() & 3;

            int cx = this.width / 2;
            int cz = this.length / 2;

            int dx;
            int dz;
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

        boolean place(LevelAccessor level, BlockPos start, Rotation rotation, RandomSource random, ResourceLocation mainBlockId, float biomeTemp, RockSchematicConfiguration cfg) {
            int rot = rotation.ordinal();
            int w = this.width;
            int h = this.height;
            int l = this.length;

            if (!hasValidSurfaceUnderBottomFootprint(level, start, w, l, rot)) {
                return false;
            }

            // Pre-pass replacement safety (and avoid half-placing)
            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        int index = ((y * l) + z) * w + x;
                        int paletteIndex = this.blockIndices[index];
                        if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                            continue;
                        }

                        BlockState state = this.palette.get(paletteIndex);
                        if (state == null || state.isAir()) {
                            continue;
                        }

                        boolean surfaceCell = isSurfaceCell(x, y, z);
                        if (surfaceCell && random.nextFloat() < cfg.chipChance()) {
                            continue;
                        }

                        BlockState mapped = RockSchematicMapping.mapState(state, random, mainBlockId, biomeTemp, cfg);
                        if (mapped == null || mapped.isAir() || mapped.is(Blocks.STRUCTURE_VOID)) {
                            continue;
                        }

                        BlockPos p = rotate(start, x, y, z, w, l, rot);
                        if (!canReplace(level, p, mapped)) {
                            return false;
                        }
                    }
                }
            }

            boolean[] bottomFootprint = new boolean[w * l];
            boolean placedAny = false;

            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        int index = ((y * l) + z) * w + x;
                        int paletteIndex = this.blockIndices[index];
                        if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                            continue;
                        }

                        BlockState state = this.palette.get(paletteIndex);
                        if (state == null || state.isAir()) {
                            continue;
                        }

                        boolean surfaceCell = isSurfaceCell(x, y, z);
                        if (surfaceCell && random.nextFloat() < cfg.chipChance()) {
                            continue;
                        }

                        BlockState mapped = RockSchematicMapping.mapState(state, random, mainBlockId, biomeTemp, cfg);
                        if (mapped == null || mapped.isAir() || mapped.is(Blocks.STRUCTURE_VOID)) {
                            continue;
                        }

                        BlockPos p = rotate(start, x, y, z, w, l, rot);
                        if (level.isOutsideBuildHeight(p)) {
                            continue;
                        }

                        if (!canReplace(level, p, mapped)) {
                            continue;
                        }

                        level.setBlock(p, mapped, 2);
                        placedAny = true;
                        if (y == 0) {
                            bottomFootprint[z * w + x] = true;
                        }
                    }
                }
            }

            if (placedAny) {
                applySupportBeard(level, start, w, l, rot, bottomFootprint);
            }

            return placedAny;
        }

        private boolean isSurfaceCell(int x, int y, int z) {
            return isVoidOrAir(x + 1, y, z)
                    || isVoidOrAir(x - 1, y, z)
                    || isVoidOrAir(x, y + 1, z)
                    || isVoidOrAir(x, y - 1, z)
                    || isVoidOrAir(x, y, z + 1)
                    || isVoidOrAir(x, y, z - 1);
        }

        private boolean isVoidOrAir(int x, int y, int z) {
            if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= length) {
                return true;
            }

            int idx = ((y * length) + z) * width + x;
            int paletteIndex = this.blockIndices[idx];
            if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                return true;
            }

            BlockState state = this.palette.get(paletteIndex);
            if (state == null || state.isAir()) {
                return true;
            }

            // Match schematic conventions: tinted glass means "do not place".
            return state.is(Blocks.TINTED_GLASS) || state.is(Blocks.STRUCTURE_VOID);
        }

        private static BlockPos rotate(BlockPos start, int x, int y, int z, int w, int l, int rot) {
            return switch (rot & 3) {
                case 1 -> start.offset(l - 1 - z, y, x);
                case 2 -> start.offset(w - 1 - x, y, l - 1 - z);
                case 3 -> start.offset(z, y, w - 1 - x);
                default -> start.offset(x, y, z);
            };
        }

        private boolean hasValidSurfaceUnderBottomFootprint(LevelAccessor level, BlockPos start, int w, int l, int rot) {
            for (int z = 0; z < l; z++) {
                for (int x = 0; x < w; x++) {
                    int index = ((0 * l) + z) * w + x;
                    int paletteIndex = this.blockIndices[index];
                    if (paletteIndex < 0 || paletteIndex >= this.palette.size()) {
                        continue;
                    }

                    BlockState state = this.palette.get(paletteIndex);
                    if (state == null || state.isAir() || state.is(Blocks.TINTED_GLASS) || state.is(Blocks.STRUCTURE_VOID)) {
                        continue;
                    }

                    BlockPos p = rotate(start, x, 0, z, w, l, rot);
                    if (level.isOutsideBuildHeight(p)) {
                        return false;
                    }

                    if (!level.getFluidState(p).isEmpty()) {
                        return false;
                    }

                    BlockState existingAtP = level.getBlockState(p);
                    if (isInvalidSchematicSurfaceTop(existingAtP)) {
                        return false;
                    }
                }
            }

            return true;
        }

        private static boolean isInvalidSchematicSurfaceTop(BlockState state) {
            if (state == null) {
                return true;
            }
            if (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)) {
                return true;
            }
            return state.getBlock() instanceof HugeMushroomBlock;
        }

        private static boolean canReplace(LevelAccessor level, BlockPos p, BlockState toPlace) {
            if (level.isOutsideBuildHeight(p)) {
                return false;
            }
            if (!level.getFluidState(p).isEmpty()) {
                return false;
            }

            BlockState existing = level.getBlockState(p);
            if (existing.isAir() || existing.canBeReplaced()) {
                return true;
            }

            if (existing.is(BlockTags.LEAVES) || existing.is(BlockTags.REPLACEABLE_BY_TREES) || existing.getBlock() instanceof LeavesBlock) {
                return true;
            }

            return existing.is(ROCK_BURY_REPLACEABLE);
        }

        private static void applySupportBeard(LevelAccessor level, BlockPos start, int w, int l, int rot, boolean[] bottomFootprint) {
            // Worldgen rule: rocks should not create deep support pillars.
            // Cap fill depth to 7 blocks below the rock footprint.
            final int maxDepth = 7;

            for (int z = 0; z < l; z++) {
                for (int x = 0; x < w; x++) {
                    int col = z * w + x;
                    if (!bottomFootprint[col]) {
                        continue;
                    }

                    BlockPos base = rotate(start, x, 0, z, w, l, rot);
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
            if (existing.isAir() || existing.canBeReplaced()) {
                return true;
            }

            if (existing.is(BlockTags.LEAVES) || existing.is(BlockTags.REPLACEABLE_BY_TREES)) {
                return true;
            }

            return false;
        }

        private static BlockState pickLocalFill(LevelAccessor level, BlockPos reference, BlockState anchor) {
            BlockState fromAnchor = normalizeTerrainFill(anchor);
            if (fromAnchor != null) {
                return fromAnchor;
            }

            final int radius = 4;
            final int downScan = 6;

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

            return Blocks.DIRT.defaultBlockState();
        }

        private static BlockState normalizeTerrainFill(BlockState state) {
            if (state == null || state.isAir()) {
                return null;
            }
            if (state.canBeReplaced()) {
                return null;
            }

            Block b = state.getBlock();
            if (b instanceof LeavesBlock) {
                return null;
            }
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE_BY_TREES)) {
                return null;
            }

            // Prefer sub-soil blocks so support columns look natural when exposed.
            if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM) || state.is(Blocks.PODZOL) || state.is(Blocks.DIRT_PATH)) {
                return Blocks.DIRT.defaultBlockState();
            }

            return state;
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
                return Blocks.AIR.defaultBlockState();
            }
        }

        private static <T extends Comparable<T>> BlockState setPropertyFromString(BlockState state, Property<T> prop, String value) {
            Optional<T> parsed = prop.getValue(value);
            return parsed.map(v -> state.setValue(prop, v)).orElse(state);
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
