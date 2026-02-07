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

import net.minecraftforge.server.ServerLifecycleHooks;

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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        ResourceManager rm = server.getResourceManager();
        SpongeSchematic schem;
        try {
            schem = SpongeSchematic.load(rm, this.schematic);
        } catch (IOException e) {
            Kruemblegard.LOGGER.warn("Failed to load lost pillager ship schematic during placement: {}", this.schematic);
            return;
        }

        int w = schem.width();
        int h = schem.height();
        int l = schem.length();

        for (int y = 0; y < h; y++) {
            for (int z = 0; z < l; z++) {
                for (int x = 0; x < w; x++) {
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
