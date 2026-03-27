package com.kruemblegard.world.structure;

import com.kruemblegard.registry.ModStructures;
import com.kruemblegard.world.schematic.SpongeSchematic;

import java.io.IOException;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import net.minecraftforge.server.ServerLifecycleHooks;

public final class AncientWayRuinsPiece extends StructurePiece {
    private final BlockPos origin;
    private final Rotation rotation;
    private final ResourceLocation schematicId;
    private final ResourceLocation processorListId;

    public AncientWayRuinsPiece(BlockPos origin, Rotation rotation, ResourceLocation schematicId, ResourceLocation processorListId) {
        super(ModStructures.ANCIENT_WAY_RUINS_PIECE.get(), 0, BoundingBox.fromCorners(origin, origin));
        this.origin = origin;
        this.rotation = rotation;
        this.schematicId = schematicId;
        this.processorListId = processorListId;
        rebuildBoundingBox();
    }

    public AncientWayRuinsPiece(CompoundTag tag) {
        super(ModStructures.ANCIENT_WAY_RUINS_PIECE.get(), tag);
        this.origin = NbtUtils.readBlockPos(tag.getCompound("Origin"));
        this.rotation = Rotation.valueOf(tag.getString("Rotation"));
        this.schematicId = new ResourceLocation(tag.getString("Schematic"));
        this.processorListId = new ResourceLocation(tag.getString("Processors"));
        rebuildBoundingBox();
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.put("Origin", NbtUtils.writeBlockPos(this.origin));
        tag.putString("Rotation", this.rotation.name());
        tag.putString("Schematic", this.schematicId.toString());
        tag.putString("Processors", this.processorListId.toString());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        SpongeSchematic schematic = loadSchematic();
        if (schematic == null) {
            return;
        }

        BlockPos localPivot = schematic.rotateLocalXZ(schematic.findPivotRedWool(), this.rotation);
        BlockPos start = this.origin.offset(-localPivot.getX(), -localPivot.getY(), -localPivot.getZ());

        for (int y = 0; y < schematic.height(); y++) {
            for (int z = 0; z < schematic.length(); z++) {
                for (int x = 0; x < schematic.width(); x++) {
                    BlockState rawState = schematic.stateAt(x, y, z);
                    if (rawState == null || rawState.isAir()) {
                        continue;
                    }

                    BlockPos worldPos = schematic.toWorld(start, x, y, z, this.rotation);
                    if (!box.isInside(worldPos)) {
                        continue;
                    }

                    RandomSource blockRandom = RandomSource.create(level.getSeed() ^ worldPos.asLong());
                    BlockState processed = AncientWayRuinsBlockProcessor.process(rawState, blockRandom);

                    if (processed == null || processed.isAir()) {
                        level.setBlock(worldPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
                        continue;
                    }

                    placeBlock(level, processed, worldPos.getX(), worldPos.getY(), worldPos.getZ(), box);
                }
            }
        }
    }

    private void rebuildBoundingBox() {
        SpongeSchematic schematic = loadSchematic();
        if (schematic == null) {
            this.boundingBox = BoundingBox.fromCorners(this.origin, this.origin);
            return;
        }

        BlockPos rotatedPivot = schematic.rotateLocalXZ(schematic.findPivotRedWool(), this.rotation);
        BlockPos start = this.origin.offset(-rotatedPivot.getX(), -rotatedPivot.getY(), -rotatedPivot.getZ());
        this.boundingBox = new BoundingBox(
                start.getX(),
                start.getY(),
                start.getZ(),
                start.getX() + schematic.rotatedWidth(this.rotation) - 1,
                start.getY() + schematic.height() - 1,
                start.getZ() + schematic.rotatedLength(this.rotation) - 1
        );
    }

    private SpongeSchematic loadSchematic() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }

        try {
            return SpongeSchematic.loadCached(server.getResourceManager(), this.schematicId);
        } catch (IOException ignored) {
            return null;
        }
    }
}