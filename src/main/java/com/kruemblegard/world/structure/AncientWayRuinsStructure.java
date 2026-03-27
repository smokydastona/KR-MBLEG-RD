package com.kruemblegard.world.structure;

import com.kruemblegard.registry.ModStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public final class AncientWayRuinsStructure extends Structure {
    public static final Codec<AncientWayRuinsStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("schematic").forGetter(structure -> structure.schematicId),
            ResourceLocation.CODEC.fieldOf("processors").forGetter(structure -> structure.processorListId),
            Codec.INT.optionalFieldOf("surface_y_offset", 0).forGetter(structure -> structure.surfaceYOffset),
            Codec.INT.optionalFieldOf("minimum_origin_distance", 2048).forGetter(structure -> structure.minimumOriginDistance)
    ).apply(instance, AncientWayRuinsStructure::new));

    private final ResourceLocation schematicId;
    private final ResourceLocation processorListId;
    private final int surfaceYOffset;
    private final int minimumOriginDistance;

    public AncientWayRuinsStructure(StructureSettings settings, ResourceLocation schematicId, ResourceLocation processorListId, int surfaceYOffset, int minimumOriginDistance) {
        super(settings);
        this.schematicId = schematicId;
        this.processorListId = processorListId;
        this.surfaceYOffset = surfaceYOffset;
        this.minimumOriginDistance = minimumOriginDistance;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX() + 8;
        int z = chunkPos.getMinBlockZ() + 8;
        if ((long) x * x + (long) z * z < (long) this.minimumOriginDistance * this.minimumOriginDistance) {
            return Optional.empty();
        }

        int y = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()) + this.surfaceYOffset;
        if (y <= context.heightAccessor().getMinBuildHeight()) {
            return Optional.empty();
        }

        var origin = new net.minecraft.core.BlockPos(x, y, z);
        Rotation rotation = Rotation.getRandom(context.random());
        return Optional.of(new GenerationStub(origin, builder -> builder.addPiece(new AncientWayRuinsPiece(origin, rotation, this.schematicId, this.processorListId))));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.ANCIENT_WAY_RUINS.get();
    }
}