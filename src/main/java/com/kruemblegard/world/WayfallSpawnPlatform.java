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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

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
        BlockPos landing = computeLanding(wayfall, anchor, spawn);

        // Keep Wayfall's dimension spawn on the surface of the origin island.
        // This does NOT override a player's bed/respawn point in other dimensions.
        wayfall.setDefaultSpawnPos(landing, 0.0F);

        return landing;
    }

    /** Places the origin island structure at the fixed anchor if not already placed. */
    public static void ensureSpawnIslandPlaced(ServerLevel wayfall) {
        BlockPos anchor = SPAWN_ISLAND_ANCHOR;
        BlockPos spawn = new BlockPos(anchor.getX(), anchor.getY() + 1, anchor.getZ());

        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        // If older saves recorded the island as "placed" while templates were missing/empty, force a rebuild.
        if (data.isPlaced() && anchor.equals(data.getAnchor()) && data.getStructureId() != null) {
            if (isSpawnIslandAreaEmpty(wayfall, anchor)) {
                Kruemblegard.LOGGER.warn(
                    "Wayfall spawn island marked placed but area is empty at {} (template {}). Forcing re-place.",
                    anchor,
                    data.getStructureId()
                );
                data.setPlaced(false);
                data.setDirty();
            }
        }

        if (!data.isPlaced() || !anchor.equals(data.getAnchor()) || data.getStructureId() == null) {
            ResourceLocation poolJson = selectTemplatePoolJson(wayfall, spawn);
            ResourceLocation structureId = pickStructureFromTemplatePoolJson(wayfall, poolJson);
            if (structureId == null) {
                // Fall back to a deterministic default if the pool can't be read for some reason.
                structureId = new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_origin_island/default/default_1");
            }

            StructureTemplateManager templates = wayfall.getServer().getStructureManager();
            StructureTemplate template = templates.getOrCreate(structureId);
            // getOrCreate can silently create an empty template if the resource is missing.
            if (template.getSize().getX() <= 0 || template.getSize().getY() <= 0 || template.getSize().getZ() <= 0) {
                Kruemblegard.LOGGER.error(
                    "Wayfall spawn island template appears empty ({}). Falling back to default_1.",
                    structureId
                );
                structureId = new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_origin_island/default/default_1");
                template = templates.getOrCreate(structureId);
            }

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
            boolean placedOk = template.placeInWorld(wayfall, anchor, anchor, settings, rng, 2);

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

            if (!placedOk) {
                Kruemblegard.LOGGER.error(
                    "Wayfall spawn island placement returned false (pool={}, template={}, anchor={}). Will not mark as placed.",
                    poolJson,
                    structureId,
                    anchor
                );
                return;
            }

            data.setPlaced(true);
            data.setAnchor(anchor);
            data.setStructureId(structureId);
            data.setDirty();

            // Always remove the marker after placement.
            // The marker is only a placement hint and should never remain in the world.
            if (markerPos != null) {
                wayfall.setBlockAndUpdate(markerPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static boolean isSpawnIslandAreaEmpty(ServerLevel level, BlockPos anchor) {
        // Heuristic: the spawn island should contribute at least some solid blocks near the anchor.
        // If we can't find any collision blocks in a small cube, assume the template never placed.
        int minX = anchor.getX() - 6;
        int maxX = anchor.getX() + 6;
        int minZ = anchor.getZ() - 6;
        int maxZ = anchor.getZ() + 6;
        int minY = Math.max(level.getMinBuildHeight(), anchor.getY() - 10);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, anchor.getY() + 10);

        int solidCount = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    BlockState state = level.getBlockState(new BlockPos(x, y, z));
                    if (state.isAir()) {
                        continue;
                    }
                    VoxelShape shape = state.getCollisionShape(level, new BlockPos(x, y, z));
                    if (!shape.isEmpty()) {
                        solidCount++;
                        if (solidCount >= 12) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
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
                        // Feet-marker: land in its block space.
                        landing = markerPos;
                    } else {
                        // Support-marker: land above it.
                        landing = markerPos.above();
                    }

                    // Always remove the marker after using it.
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

        // Even if a marker is present, clamp the landing upward to the local surface.
        // Prefer a surface that ignores leaves (avoid landing on tree canopies).
        int surfaceYAtLanding = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, landing.getX(), landing.getZ());
        if (surfaceYAtLanding <= wayfall.getMinBuildHeight()) {
            surfaceYAtLanding = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING, landing.getX(), landing.getZ());
        }
        int surfaceLandingY = Math.min(surfaceYAtLanding + 1, wayfall.getMaxBuildHeight() - 4);
        if (landing.getY() < surfaceLandingY) {
            landing = new BlockPos(landing.getX(), surfaceLandingY, landing.getZ());
        }

        // Note: do not place any "emergency" blocks here. Portal entry should only place the NBT island.

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
