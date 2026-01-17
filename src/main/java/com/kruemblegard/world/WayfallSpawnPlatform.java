package com.kruemblegard.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class WayfallSpawnPlatform {
    private WayfallSpawnPlatform() {}

    private static final int DEFAULT_PLATFORM_Y = 160;
    private static final int CHUNK_LOAD_PADDING_BLOCKS = 24;

    public static BlockPos ensureSpawnLanding(ServerLevel wayfall) {
        BlockPos spawn = wayfall.getSharedSpawnPos();

        int platformY = MthClamp.clamp(DEFAULT_PLATFORM_Y, wayfall.getMinBuildHeight() + 8, wayfall.getMaxBuildHeight() - 8);
        BlockPos anchor = new BlockPos(spawn.getX(), platformY, spawn.getZ());

        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        if (!data.isPlaced() || !anchor.equals(data.getAnchor()) || data.getStructureId() == null) {
            ResourceLocation poolJson = selectTemplatePoolJson(wayfall, spawn);
            ResourceLocation structureId = pickStructureFromTemplatePoolJson(wayfall, poolJson);
            if (structureId == null) {
                // Fall back to a deterministic default if the pool can't be read for some reason.
                structureId = new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_origin_island/default/default_1");
            }

            StructureTemplateManager templates = wayfall.getServer().getStructureManager();
            StructureTemplate template = templates.getOrCreate(structureId);

            // Optional explicit landing marker: put a single minecraft:barrier block in the structure template
            // at the desired "feet" position, and we'll teleport the player above it and remove the marker.
            BlockPos markerPos = findBestLandingMarker(template, anchor, spawn);

            // Ensure affected chunks are fully generated/loaded before placing the template.
            var size = template.getSize();
            int padX = Math.max(CHUNK_LOAD_PADDING_BLOCKS, size.getX());
            int padZ = Math.max(CHUNK_LOAD_PADDING_BLOCKS, size.getZ());
            int minChunkX = (anchor.getX() - padX) >> 4;
            int maxChunkX = (anchor.getX() + padX) >> 4;
            int minChunkZ = (anchor.getZ() - padZ) >> 4;
            int maxChunkZ = (anchor.getZ() + padZ) >> 4;
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    wayfall.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, true);
                }
            }

            StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(true);
            RandomSource rng = wayfall.random;
            template.placeInWorld(wayfall, anchor, anchor, settings, rng, 2);

            data.setPlaced(true);
            data.setAnchor(anchor);
            data.setStructureId(structureId);
            data.setDirty();

            // Remove the marker after placement so it can't interfere with play.
            if (markerPos != null) {
                wayfall.setBlockAndUpdate(markerPos, Blocks.AIR.defaultBlockState());
            }
        }

        // If a marker exists, prefer it; otherwise fall back to the heightmap at spawn X/Z.
        BlockPos landing = null;
        ResourceLocation cachedStructure = data.getStructureId();
        if (cachedStructure != null && wayfall.getServer() != null) {
            try {
                StructureTemplate template = wayfall.getServer().getStructureManager().getOrCreate(cachedStructure);
                BlockPos markerPos = findBestLandingMarker(template, data.getAnchor(), spawn);
                if (markerPos != null) {
                    landing = markerPos.above();
                    // Ensure the marker isn't left behind even if the first placement attempt crashed mid-way.
                    wayfall.setBlockAndUpdate(markerPos, Blocks.AIR.defaultBlockState());
                }
            } catch (Exception ignored) {
                // Heightmap fallback below.
            }
        }

        if (landing == null) {
            int surfaceY = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ());
            landing = new BlockPos(spawn.getX(), Math.min(surfaceY + 1, wayfall.getMaxBuildHeight() - 4), spawn.getZ());
        }

        // Ensure 3-block headroom.
        wayfall.setBlockAndUpdate(landing, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        wayfall.setBlockAndUpdate(landing.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        wayfall.setBlockAndUpdate(landing.above(2), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());

        return landing;
    }

    private static BlockPos findBestLandingMarker(StructureTemplate template, BlockPos anchor, BlockPos spawn) {
        if (template == null) {
            return null;
        }

        // This yields world positions as-if the template were placed at `anchor`.
        List<StructureTemplate.StructureBlockInfo> markers = template.filterBlocks(anchor, new StructurePlaceSettings(), Blocks.BARRIER);
        if (markers == null || markers.isEmpty()) {
            return null;
        }

        BlockPos best = null;
        long bestDist2 = Long.MAX_VALUE;
        for (StructureTemplate.StructureBlockInfo info : markers) {
            BlockPos pos = info.pos();
            long dx = (long) pos.getX() - (long) spawn.getX();
            long dz = (long) pos.getZ() - (long) spawn.getZ();
            long dist2 = dx * dx + dz * dz;
            if (dist2 < bestDist2) {
                bestDist2 = dist2;
                best = pos;
            }
        }
        return best;
    }

    private static ResourceLocation selectTemplatePoolJson(ServerLevel wayfall, BlockPos spawn) {
        ResourceKey<?> biomeKey = wayfall.getBiome(spawn).unwrapKey().orElse(null);
        if (biomeKey == ModWorldgenKeys.Biomes.STRATA_COLLAPSE) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/ashfall.json");
        }
        if (biomeKey == ModWorldgenKeys.Biomes.SHATTERPLATE_FLATS) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/voidfelt.json");
        }
        if (biomeKey == ModWorldgenKeys.Biomes.FRACTURE_SHOALS) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/fractured.json");
        }
        if (biomeKey == ModWorldgenKeys.Biomes.GLYPHSCAR_REACH) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/glyphscar.json");
        }
        if (biomeKey == ModWorldgenKeys.Biomes.BASIN_OF_SCARS) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/basin_of_scars.json");
        }

        float temp = wayfall.getBiome(spawn).value().getBaseTemperature();
        if (temp <= 0.25f) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/cold.json");
        }

        return new ResourceLocation(Kruemblegard.MOD_ID, "worldgen/template_pool/wayfall_origin_island/default.json");
    }

    private static ResourceLocation pickStructureFromTemplatePoolJson(ServerLevel wayfall, ResourceLocation templatePoolJson) {
        if (wayfall.getServer() == null) {
            return null;
        }

        try {
            ResourceManager resourceManager = wayfall.getServer().getResourceManager();
            var resourceOpt = resourceManager.getResource(templatePoolJson);
            if (resourceOpt.isEmpty()) {
                return null;
            }

            try (InputStream is = resourceOpt.get().open();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonElement elementsEl = root.get("elements");
                if (elementsEl == null || !elementsEl.isJsonArray()) {
                    return null;
                }

                // Weighted random pick.
                int totalWeight = 0;
                for (JsonElement weightedEl : elementsEl.getAsJsonArray()) {
                    if (!weightedEl.isJsonObject()) {
                        continue;
                    }
                    JsonObject weighted = weightedEl.getAsJsonObject();
                    int weight = weighted.has("weight") ? weighted.get("weight").getAsInt() : 1;
                    if (weight > 0) {
                        totalWeight += weight;
                    }
                }
                if (totalWeight <= 0) {
                    return null;
                }

                int roll = wayfall.random.nextInt(totalWeight);
                for (JsonElement weightedEl : elementsEl.getAsJsonArray()) {
                    if (!weightedEl.isJsonObject()) {
                        continue;
                    }
                    JsonObject weighted = weightedEl.getAsJsonObject();
                    int weight = weighted.has("weight") ? weighted.get("weight").getAsInt() : 1;
                    if (weight <= 0) {
                        continue;
                    }
                    roll -= weight;
                    if (roll >= 0) {
                        continue;
                    }

                    JsonElement elementEl = weighted.get("element");
                    if (elementEl == null || !elementEl.isJsonObject()) {
                        return null;
                    }
                    JsonObject element = elementEl.getAsJsonObject();
                    JsonElement locationEl = element.get("location");
                    if (locationEl == null || !locationEl.isJsonPrimitive()) {
                        return null;
                    }

                    return ResourceLocation.tryParse(locationEl.getAsString());
                }

                return null;
            }
        } catch (Exception ex) {
            Kruemblegard.LOGGER.warn("Failed to pick structure from template pool {}: {}", templatePoolJson, ex.toString());
            return null;
        }
    }

    /** Minimal clamp utility to avoid pulling extra math dependencies into world-only helper. */
    private static final class MthClamp {
        private static int clamp(int value, int min, int max) {
            if (value < min) {
                return min;
            }
            return Math.min(value, max);
        }
    }
}
