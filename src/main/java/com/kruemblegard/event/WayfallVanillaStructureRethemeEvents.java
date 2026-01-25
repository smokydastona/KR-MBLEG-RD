package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.WayfallStructureRethemeSavedData;
import com.kruemblegard.worldgen.ModWorldgenKeys;
import com.kruemblegard.worldgen.WayfallStructureRetheme;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallVanillaStructureRethemeEvents {
    private WayfallVanillaStructureRethemeEvents() {}

    private static final ResourceLocation SHIPWRECK_ID = new ResourceLocation("minecraft", "shipwreck");
    private static final ResourceLocation SHIPWRECK_BEACHED_ID = new ResourceLocation("minecraft", "shipwreck_beached");
    private static final ResourceLocation JUNGLE_PYRAMID_ID = new ResourceLocation("minecraft", "jungle_pyramid");

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure shipwreck = structureRegistry.get(SHIPWRECK_ID);
        Structure shipwreckBeached = structureRegistry.get(SHIPWRECK_BEACHED_ID);
        Structure junglePyramid = structureRegistry.get(JUNGLE_PYRAMID_ID);
        if (shipwreck == null && shipwreckBeached == null && junglePyramid == null) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        StructureManager structureManager = level.structureManager();

        List<StructureStart> starts = structureManager.startsForStructure(
                chunkPos,
                s -> s == shipwreck || s == shipwreckBeached || s == junglePyramid
        );

        if (starts.isEmpty()) {
            return;
        }

        WayfallStructureRethemeSavedData data = WayfallStructureRethemeSavedData.get(level);

        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMaxX = chunkPos.getMaxBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkMaxZ = chunkPos.getMaxBlockZ();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (StructureStart start : starts) {
            if (!start.isValid()) {
                continue;
            }

            ResourceLocation structureId = structureRegistry.getKey(start.getStructure());
            if (structureId == null) {
                continue;
            }

            String startKey = structureId + "@" + start.getChunkPos().x + "," + start.getChunkPos().z;
            long chunkLong = chunkPos.toLong();
            if (data.isChunkProcessed(startKey, chunkLong)) {
                continue;
            }

            ResourceKey<Biome> biomeKey = getBiomeKeyForStructure(level, start.getBoundingBox());
            WayfallStructureRetheme.WoodPalette palette = WayfallStructureRetheme.woodPaletteForBiome(
                    biomeKey == null ? ModWorldgenKeys.Biomes.CRUMBLED_CROSSING : biomeKey
            );

            long seed = structureId.hashCode();
            seed ^= (long) start.getChunkPos().x * 341873128712L;
            seed ^= (long) start.getChunkPos().z * 132897987541L;
            seed ^= chunkLong;

            boolean isShipwreck = structureId.equals(SHIPWRECK_ID) || structureId.equals(SHIPWRECK_BEACHED_ID);
            boolean isJunglePyramid = structureId.equals(JUNGLE_PYRAMID_ID);

            for (StructurePiece piece : start.getPieces()) {
                BoundingBox bbox = piece.getBoundingBox();

                int minX = Math.max(bbox.minX(), chunkMinX);
                int maxX = Math.min(bbox.maxX(), chunkMaxX);
                int minZ = Math.max(bbox.minZ(), chunkMinZ);
                int maxZ = Math.min(bbox.maxZ(), chunkMaxZ);

                if (minX > maxX || minZ > maxZ) {
                    continue;
                }

                int minY = Math.max(bbox.minY(), level.getMinBuildHeight());
                int maxY = Math.min(bbox.maxY(), level.getMaxBuildHeight() - 1);
                if (minY > maxY) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        for (int x = minX; x <= maxX; x++) {
                            mutablePos.set(x, y, z);

                            var state = chunk.getBlockState(mutablePos);
                            if (state.isAir()) {
                                continue;
                            }

                            var newState = state;
                            if (isShipwreck) {
                                if (!WayfallStructureRetheme.isVanillaShipwreckWoodCandidate(state)) {
                                    continue;
                                }

                                boolean keepVanilla = WayfallStructureRetheme.shouldKeepVanillaWood(
                                        seed,
                                        x,
                                        y,
                                        z,
                                        WayfallStructureRetheme.DEFAULT_KEEP_VANILLA_WOOD_CHANCE
                                );

                                newState = WayfallStructureRetheme.rethemeShipwreckBlock(state, palette, keepVanilla);
                            } else if (isJunglePyramid) {
                                // Cheap prefilter: only these blocks can change.
                                if (!WayfallStructureRetheme.isJungleTempleStoneCandidate(state)) {
                                    continue;
                                }
                                newState = WayfallStructureRetheme.rethemeJungleTempleStone(state);
                            }

                            if (newState != state) {
                                // Avoid neighbor-update storms during chunk load/gen; we only need the blocks to persist.
                                chunk.setBlockState(mutablePos, newState, false);
                            }
                        }
                    }
                }
            }

            data.markChunkProcessed(startKey, chunkLong);
        }
    }

    private static ResourceKey<Biome> getBiomeKeyForStructure(ServerLevel level, BoundingBox bbox) {
        BlockPos center = bbox.getCenter();
        BlockPos sample = new BlockPos(center.getX(), Math.max(level.getMinBuildHeight() + 1, center.getY()), center.getZ());
        return level.getBiome(sample).unwrapKey().orElse(null);
    }
}
