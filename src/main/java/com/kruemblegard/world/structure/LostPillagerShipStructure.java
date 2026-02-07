package com.kruemblegard.world.structure;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModStructures;
import com.kruemblegard.world.schematic.SpongeSchematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.util.Optional;

public final class LostPillagerShipStructure extends Structure {
    public static final Codec<LostPillagerShipStructure> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("schematic").forGetter(s -> s.schematic)
        ).apply(instance, LostPillagerShipStructure::new)
    );

    private final ResourceLocation schematic;

    public LostPillagerShipStructure(StructureSettings settings, ResourceLocation schematic) {
        super(settings);
        this.schematic = schematic;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int pivotX = chunkPos.getMiddleBlockX();
        int pivotZ = chunkPos.getMiddleBlockZ();

        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                pivotX,
                pivotZ,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Optional.empty();
        }

        ResourceManager rm = server.getResourceManager();
        SpongeSchematic schem;
        try {
            schem = SpongeSchematic.loadCached(rm, this.schematic);
        } catch (IOException e) {
            Kruemblegard.LOGGER.warn("Lost pillager ship schematic missing/invalid: {}", this.schematic);
            return Optional.empty();
        }

        Rotation rotation = Rotation.values()[context.random().nextInt(4)];

        BlockPos pivotLocal = schem.findPivotRedWool();
        BlockPos pivotRot = schem.rotateLocalXZ(pivotLocal, rotation);

        BlockPos groundLocal = schem.findHighestRedStainedGlass();
        int groundLocalY = groundLocal.getY();

        int startX = pivotX - pivotRot.getX();
        int startZ = pivotZ - pivotRot.getZ();
        int startY = surfaceY - groundLocalY;

        int rw = schem.rotatedWidth(rotation);
        int rl = schem.rotatedLength(rotation);
        int h = schem.height();

        BlockPos start = new BlockPos(startX, startY, startZ);
        BoundingBox box = new BoundingBox(
                startX,
                startY,
                startZ,
                startX + (rw - 1),
                startY + (h - 1),
                startZ + (rl - 1)
        );

        // Safety: avoid placing fully outside build height.
        if (box.maxY() < context.heightAccessor().getMinBuildHeight() || box.minY() > context.heightAccessor().getMaxBuildHeight()) {
            return Optional.empty();
        }

        LostPillagerShipPiece piece = new LostPillagerShipPiece(this.schematic, start, rotation, box);

        return Optional.of(new GenerationStub(start, builder -> builder.addPiece(piece)));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LOST_PILLAGER_SHIP.get();
    }
}
