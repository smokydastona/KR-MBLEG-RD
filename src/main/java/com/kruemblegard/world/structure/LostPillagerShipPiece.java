package com.kruemblegard.world.structure;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModStructures;
import com.kruemblegard.world.schematic.SpongeSchematic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.io.IOException;
import java.util.List;

public final class LostPillagerShipPiece extends StructurePiece {
    private static final ResourceLocation LOOT_PILLAGER_OUTPOST = new ResourceLocation("minecraft", "chests/pillager_outpost");
    private static final List<ResourceLocation> LOOT_VILLAGE = List.of(
            new ResourceLocation("minecraft", "chests/village/village_plains_house"),
            new ResourceLocation("minecraft", "chests/village/village_taiga_house"),
            new ResourceLocation("minecraft", "chests/village/village_snowy_house"),
            new ResourceLocation("minecraft", "chests/village/village_savanna_house"),
            new ResourceLocation("minecraft", "chests/village/village_desert_house")
    );
    private static final ResourceLocation LOOT_BASTION = new ResourceLocation("minecraft", "chests/bastion_treasure");

    private final ResourceLocation schematic;
    private final BlockPos start;
    private final Rotation rotation;

    public LostPillagerShipPiece(ResourceLocation schematic, BlockPos start, Rotation rotation, BoundingBox box) {
        super(ModStructures.LOST_PILLAGER_SHIP_PIECE.get(), 0, box);
        this.schematic = schematic;
        this.start = start;
        this.rotation = rotation;
    }

    public LostPillagerShipPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructures.LOST_PILLAGER_SHIP_PIECE.get(), tag);
        this.schematic = new ResourceLocation(tag.getString("Schematic"));
        this.start = new BlockPos(tag.getInt("StartX"), tag.getInt("StartY"), tag.getInt("StartZ"));
        int rot = tag.getInt("Rotation");
        this.rotation = Rotation.values()[Math.floorMod(rot, 4)];
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Schematic", this.schematic.toString());
        tag.putInt("StartX", this.start.getX());
        tag.putInt("StartY", this.start.getY());
        tag.putInt("StartZ", this.start.getZ());
        tag.putInt("Rotation", this.rotation.ordinal());
    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            net.minecraft.world.level.chunk.ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos pivot
    ) {
        MinecraftServer server = level.getLevel().getServer();
        if (server == null) {
            return;
        }

        ResourceManager rm = server.getResourceManager();
        SpongeSchematic schem;
        try {
            schem = SpongeSchematic.loadCached(rm, this.schematic);
        } catch (IOException e) {
            Kruemblegard.LOGGER.warn("Failed to load lost pillager ship schematic during placement: {}", this.schematic);
            return;
        }

        int w = schem.width();
        int h = schem.height();
        int l = schem.length();

        // IMPORTANT: postProcess is called once per-chunk within the structure's bounding box.
        // Iterating the entire schematic volume each call causes runaway O(volume * chunks) work.
        // Instead, iterate only the local-coordinate region that can land within this chunk's box.
        int minLocalY = Mth.clamp(box.minY() - this.start.getY(), 0, h - 1);
        int maxLocalY = Mth.clamp(box.maxY() - this.start.getY(), 0, h - 1);

        int[] localMinMaxXZ = localXZBoundsForWorldBox(box, this.start, this.rotation, w, l);
        int minLocalX = Mth.clamp(localMinMaxXZ[0], 0, w - 1);
        int maxLocalX = Mth.clamp(localMinMaxXZ[1], 0, w - 1);
        int minLocalZ = Mth.clamp(localMinMaxXZ[2], 0, l - 1);
        int maxLocalZ = Mth.clamp(localMinMaxXZ[3], 0, l - 1);

        if (minLocalX > maxLocalX || minLocalY > maxLocalY || minLocalZ > maxLocalZ) {
            return;
        }

        for (int y = minLocalY; y <= maxLocalY; y++) {
            for (int z = minLocalZ; z <= maxLocalZ; z++) {
                for (int x = minLocalX; x <= maxLocalX; x++) {
                    BlockState original = schem.stateAt(x, y, z);
                    if (original == null || original.isAir()) {
                        continue;
                    }

                    BlockState mapped = mapState(original, random, schem, x, y, z);
                    if (mapped.is(Blocks.STRUCTURE_VOID)) {
                        continue;
                    }

                    BlockPos worldPos = schem.toWorld(this.start, x, y, z, this.rotation);
                    if (!box.isInside(worldPos)) {
                        continue;
                    }

                    // For interior clearing, we intentionally place AIR (white glass marker).
                    level.setBlock(worldPos, mapped, 2);

                    // Post-pass: set block entity data (loot/spawners) after placement.
                    if (mapped.is(Blocks.SPAWNER)) {
                        configureVindicatorSpawner(level, worldPos, random);
                    }

                    if (mapped.is(Blocks.CHEST) || mapped.is(Blocks.TRAPPED_CHEST) || mapped.is(Blocks.BARREL)) {
                        configureContainerLoot(level, worldPos, original, random);
                    }
                }
            }
        }
    }

    private static int[] localXZBoundsForWorldBox(BoundingBox worldBox, BlockPos start, Rotation rotation, int schematicWidth, int schematicLength) {
        int minX = worldBox.minX();
        int maxX = worldBox.maxX();
        int minZ = worldBox.minZ();
        int maxZ = worldBox.maxZ();

        int[][] corners = new int[][]{
                {minX, minZ},
                {minX, maxZ},
                {maxX, minZ},
                {maxX, maxZ}
        };

        int localMinX = Integer.MAX_VALUE;
        int localMaxX = Integer.MIN_VALUE;
        int localMinZ = Integer.MAX_VALUE;
        int localMaxZ = Integer.MIN_VALUE;

        for (int[] c : corners) {
            int[] local = worldToLocalXZ(c[0], c[1], start, rotation, schematicWidth, schematicLength);
            localMinX = Math.min(localMinX, local[0]);
            localMaxX = Math.max(localMaxX, local[0]);
            localMinZ = Math.min(localMinZ, local[1]);
            localMaxZ = Math.max(localMaxZ, local[1]);
        }

        return new int[]{localMinX, localMaxX, localMinZ, localMaxZ};
    }

    private static int[] worldToLocalXZ(int worldX, int worldZ, BlockPos start, Rotation rotation, int schematicWidth, int schematicLength) {
        int dx = worldX - start.getX();
        int dz = worldZ - start.getZ();
        int rot = rotation.ordinal() & 3;

        int lx;
        int lz;
        switch (rot) {
            case 1 -> {
                lx = dz;
                lz = schematicLength - 1 - dx;
            }
            case 2 -> {
                lx = schematicWidth - 1 - dx;
                lz = schematicLength - 1 - dz;
            }
            case 3 -> {
                lx = schematicWidth - 1 - dz;
                lz = dx;
            }
            default -> {
                lx = dx;
                lz = dz;
            }
        }

        return new int[]{lx, lz};
    }

    private static BlockState mapState(BlockState original, RandomSource random, SpongeSchematic schem, int x, int y, int z) {
        // Interior filler must become air.
        if (original.is(Blocks.WHITE_STAINED_GLASS)) {
            return Blocks.AIR.defaultBlockState();
        }

        // Pivot marker becomes planks.
        if (original.is(Blocks.RED_WOOL)) {
            BlockState adjacentPlanks = schem.findAdjacentPlanksState(x, y, z);
            if (adjacentPlanks != null) {
                return adjacentPlanks;
            }

            // Safe fallback if the marker isn't next to planks (e.g., schematic authoring mistakes).
            return Blocks.SPRUCE_PLANKS.defaultBlockState();
        }

        // Ground marker (burial limit) is not part of the final build.
        if (original.is(Blocks.RED_STAINED_GLASS)) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        }

        // Beacon markers become vindicator spawners.
        if (original.is(Blocks.BEACON)) {
            return Blocks.SPAWNER.defaultBlockState();
        }

        // Pink wool markers become bastion-loot chests.
        if (original.is(Blocks.PINK_WOOL)) {
            return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
        }

        return original;
    }

    private static void configureVindicatorSpawner(WorldGenLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SpawnerBlockEntity spawner) {
            spawner.getSpawner().setEntityId(EntityType.VINDICATOR, level.getLevel(), random, pos);
            spawner.setChanged();
        }
    }

    private static void configureContainerLoot(WorldGenLevel level, BlockPos pos, BlockState original, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RandomizableContainerBlockEntity container)) {
            return;
        }

        ResourceLocation loot;

        // Pink wool markers always become bastion treasure.
        if (original.is(Blocks.PINK_WOOL)) {
            loot = LOOT_BASTION;
        } else {
            // Default: 60% village loot, 40% pillager outpost loot.
            if (random.nextFloat() < 0.60f) {
                loot = LOOT_VILLAGE.get(random.nextInt(LOOT_VILLAGE.size()));
            } else {
                loot = LOOT_PILLAGER_OUTPOST;
            }
        }

        container.setLootTable(loot, random.nextLong());
        container.setChanged();
    }
}
