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

    private static final BlockPos SPAWN_ISLAND_ANCHOR = new BlockPos(0, 175, 0);
    private static final int CHUNK_LOAD_PADDING_BLOCKS = 24;

    public static BlockPos ensureSpawnLanding(ServerLevel wayfall) {
        // Portal entry always targets the fixed Wayfall origin island anchor.
        BlockPos anchor = SPAWN_ISLAND_ANCHOR;
        BlockPos spawn = new BlockPos(anchor.getX(), anchor.getY() + 1, anchor.getZ());

        ensureSpawnIslandPlaced(wayfall);

        // Compute landing from marker/heightmap.
        return computeLanding(wayfall, anchor, spawn);
    }

    /** Places the origin island structure at the fixed anchor if not already placed. */
    public static void ensureSpawnIslandPlaced(ServerLevel wayfall) {
        BlockPos anchor = SPAWN_ISLAND_ANCHOR;
        BlockPos spawn = new BlockPos(anchor.getX(), anchor.getY() + 1, anchor.getZ());

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

            // Optional explicit landing marker: a single minecraft:barrier block inside the template.
            // If the marker sits on solid support, treat it as a "feet" marker (we remove it and land at that block).
            // If it's floating (no support below), treat it as a "support" marker (we keep it and land above it).
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

            Kruemblegard.LOGGER.info(
                "Wayfall spawn island placed: pool={} template={} anchor={} marker={} size={}x{}x{}",
                poolJson,
                structureId,
                anchor,
                markerPos,
                template.getSize().getX(),
                template.getSize().getY(),
                template.getSize().getZ()
            );

            data.setPlaced(true);
            data.setAnchor(anchor);
            data.setStructureId(structureId);
            data.setDirty();

            // Remove the marker after placement only when it's clearly a feet-marker.
            if (markerPos != null) {
                BlockPos below = markerPos.below();
                boolean hasSupport = !wayfall.getBlockState(below).getCollisionShape(wayfall, below).isEmpty();
                if (hasSupport) {
                    wayfall.setBlockAndUpdate(markerPos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static BlockPos computeLanding(ServerLevel wayfall, BlockPos anchor, BlockPos spawn) {
        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);

        // If a marker exists, prefer it; otherwise fall back to the heightmap at spawn X/Z.
        BlockPos landing = null;
        ResourceLocation cachedStructure = data.getStructureId();
        if (cachedStructure != null && wayfall.getServer() != null) {
            try {
                StructureTemplate template = wayfall.getServer().getStructureManager().getOrCreate(cachedStructure);
                BlockPos markerPos = findBestLandingMarker(template, data.getAnchor(), spawn);
                if (markerPos != null) {
                    BlockPos below = markerPos.below();
                    boolean hasSupport = !wayfall.getBlockState(below).getCollisionShape(wayfall, below).isEmpty();
                    if (hasSupport) {
                        // Feet-marker: remove marker and land in its block space.
                        wayfall.setBlockAndUpdate(markerPos, Blocks.AIR.defaultBlockState());
                        landing = markerPos;
                    } else {
                        // Support-marker: keep marker and land above it.
                        landing = markerPos.above();
                    }
                }
            } catch (Exception ignored) {
                // Heightmap fallback below.
            }
        }

        if (landing == null) {
            int surfaceY = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ());
            landing = new BlockPos(spawn.getX(), Math.min(surfaceY + 1, wayfall.getMaxBuildHeight() - 4), spawn.getZ());
        }

        // Final safety check: if there's still no solid floor under the landing, build a tiny invisible pad.
        // This avoids "fell into the void" even if the template failed to place (or was empty/misaligned).
        BlockPos floor = landing.below();
        boolean hasFloor = !wayfall.getBlockState(floor).getCollisionShape(wayfall, floor).isEmpty();
        if (!hasFloor) {
            Kruemblegard.LOGGER.error(
                "Wayfall spawn island did not provide a solid landing floor at {} (floor {}). Creating emergency pad. template={} anchor={}",
                landing,
                floor,
                data.getStructureId(),
                data.getAnchor()
            );

            // 3x3 barrier pad one block below the landing.
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pad = floor.offset(dx, 0, dz);
                    wayfall.setBlockAndUpdate(pad, Blocks.BARRIER.defaultBlockState());
                }
            }
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

}
