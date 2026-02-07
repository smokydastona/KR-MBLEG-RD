package com.kruemblegard.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
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

    static record SpawnIslandPlacement(
        BlockPos anchor,
        BlockPos spawn,
        ResourceLocation poolJson,
        ResourceLocation structureId,
        StructureTemplate template,
        StructurePlaceSettings settings,
        BlockPos markerPos,
        Vec3i size
    ) {}

    /**
     * Prepares (but does not complete) spawn-island placement.
     *
     * Intended for the Wayfall init scheduler so placement can be done incrementally across ticks.
     */
    static SpawnIslandPlacement beginSpawnIslandPlacement(ServerLevel wayfall, boolean forceChunkLoadForValidation) {
        BlockPos anchor = SPAWN_ISLAND_ANCHOR;
        BlockPos spawn = new BlockPos(anchor.getX(), anchor.getY() + 1, anchor.getZ());

        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        // If older saves recorded the island as "placed" while templates were missing/empty, force a rebuild.
        // IMPORTANT: only validate during forced checks (dimension load). Routine entry validation can
        // produce false positives and cause re-placement loops.
        if (data.isPlaced() && anchor.equals(data.getAnchor()) && data.getStructureId() != null && forceChunkLoadForValidation) {
            StructureTemplateManager templates = wayfall.getServer().getStructureManager();
            StructureTemplate existingTemplate = templates.getOrCreate(data.getStructureId());
            Vec3i size = existingTemplate.getSize();
            // getOrCreate can silently create an empty template if the resource is missing.
            if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) {
                Kruemblegard.LOGGER.warn(
                    "Wayfall spawn island marked placed but template appears empty ({}). Forcing re-place.",
                    data.getStructureId()
                );
                data.setPlaced(false);
                data.setDirty();
            } else {
                // Avoid forcing chunk generation/loading just to validate. If chunks are already loaded
                // (e.g., because a player is present), we can validate cheaply; otherwise skip.
                if (areTemplateChunksLoaded(wayfall, anchor, size) && isSpawnIslandAreaEmpty(wayfall, anchor, size)) {
                    Kruemblegard.LOGGER.warn(
                        "Wayfall spawn island marked placed but area is empty at {} (template {}). Forcing re-place.",
                        anchor,
                        data.getStructureId()
                    );
                    data.setPlaced(false);
                    data.setDirty();
                }
            }
        }

        if (data.isPlaced() && anchor.equals(data.getAnchor()) && data.getStructureId() != null) {
            return null;
        }

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

        BlockPos markerPos = findBestLandingMarker(template, anchor, spawn);

        // The scheduler should preload chunks before placement; if they aren't ready, defer.
        if (!areTemplateChunksLoaded(wayfall, anchor, template.getSize())) {
            if (ModConfig.WAYFALL_DEBUG_LOGGING.get()) {
                Kruemblegard.LOGGER.info(
                    "Wayfall spawn island placement deferred (chunks not loaded yet): template={} size={}x{}x{} anchor={}",
                    structureId,
                    template.getSize().getX(),
                    template.getSize().getY(),
                    template.getSize().getZ(),
                    anchor
                );
            }
            return null;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
            .setIgnoreEntities(true)
            // The origin island templates contain jigsaw blocks as authoring markers.
            // When placing templates directly (not via the jigsaw structure pipeline), we must
            // apply the replacement processor so those markers don't remain in the world.
            .addProcessor(JigsawReplacementProcessor.INSTANCE);
        applyBiomeRunegrowthProcessors(wayfall, spawn, settings);

        return new SpawnIslandPlacement(anchor, spawn, poolJson, structureId, template, settings, markerPos, template.getSize());
    }

    static void placeSpawnIslandBatch(ServerLevel wayfall, SpawnIslandPlacement placement, BoundingBox batchBox) {
        if (placement == null) {
            return;
        }

        placement.settings().setBoundingBox(batchBox);
        RandomSource rng = wayfall.random;
        placement.template().placeInWorld(wayfall, placement.anchor(), placement.anchor(), placement.settings(), rng, 2);
    }

    static void finishSpawnIslandPlacement(ServerLevel wayfall, SpawnIslandPlacement placement) {
        if (placement == null) {
            return;
        }

        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        if (data.isPlaced()) {
            return;
        }

        Kruemblegard.LOGGER.info(
            "Wayfall spawn island placed: pool={} template={} anchor={} marker={} size={}x{}x{}",
            placement.poolJson(),
            placement.structureId(),
            placement.anchor(),
            placement.markerPos(),
            placement.size().getX(),
            placement.size().getY(),
            placement.size().getZ()
        );

        data.setPlaced(true);
        data.setAnchor(placement.anchor());
        data.setStructureId(placement.structureId());
        data.setDirty();

        // Always remove the marker after placement.
        if (placement.markerPos() != null) {
            wayfall.setBlock(placement.markerPos(), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    public static BlockPos getSpawnIslandAnchor() {
        return SPAWN_ISLAND_ANCHOR;
    }

    static int getChunkLoadPaddingBlocks() {
        return CHUNK_LOAD_PADDING_BLOCKS;
    }

    static Vec3i getSpawnIslandTemplateSizeForPreload(ServerLevel wayfall) {
        ResourceLocation structureId = null;
        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        if (data.getStructureId() != null) {
            structureId = data.getStructureId();
        }

        // Conservative fallback for first load.
        if (structureId == null) {
            structureId = new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_origin_island/default/default_1");
        }

        try {
            StructureTemplateManager templates = wayfall.getServer().getStructureManager();
            StructureTemplate template = templates.getOrCreate(structureId);
            Vec3i size = template.getSize();
            if (size.getX() > 0 && size.getY() > 0 && size.getZ() > 0) {
                return size;
            }
        } catch (Exception ignored) {
            // Fallback below.
        }

        // Worst-case safety fallback to avoid empty envelopes.
        return new Vec3i(64, 64, 64);
    }

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

    /**
     * Returns a reasonable landing position ONLY if the spawn island is already known/placed.
     *
     * This intentionally avoids triggering any placement or forced chunk loading so that mobs/items
     * entering the portal can't accidentally bootstrap heavy Wayfall initialization while a player
     * is still in the overworld.
     */
    public static BlockPos getSpawnLandingIfPlaced(ServerLevel wayfall) {
        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        if (!data.isPlaced()) {
            return null;
        }

        BlockPos anchor = SPAWN_ISLAND_ANCHOR;
        BlockPos spawn = new BlockPos(anchor.getX(), anchor.getY() + 1, anchor.getZ());

        int surfaceY = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ());
        return new BlockPos(spawn.getX(), Math.min(surfaceY + 1, wayfall.getMaxBuildHeight() - 4), spawn.getZ());
    }

    /** Places the origin island structure at the fixed anchor if not already placed. */
    public static void ensureSpawnIslandPlaced(ServerLevel wayfall) {
        ensureSpawnIslandPlaced(wayfall, false);
    }

    /** Places the origin island structure at the fixed anchor if not already placed. */
    public static void ensureSpawnIslandPlaced(ServerLevel wayfall, boolean forceChunkLoadForValidation) {
        BlockPos anchor = SPAWN_ISLAND_ANCHOR;
        BlockPos spawn = new BlockPos(anchor.getX(), anchor.getY() + 1, anchor.getZ());

        WayfallSpawnIslandSavedData data = WayfallSpawnIslandSavedData.get(wayfall);
        // If older saves recorded the island as "placed" while templates were missing/empty, force a rebuild.
        // IMPORTANT: only validate during forced checks (dimension load). Routine entry validation can
        // produce false positives and cause re-placement loops.
        if (data.isPlaced() && anchor.equals(data.getAnchor()) && data.getStructureId() != null && forceChunkLoadForValidation) {
            StructureTemplateManager templates = wayfall.getServer().getStructureManager();
            StructureTemplate existingTemplate = templates.getOrCreate(data.getStructureId());
            Vec3i size = existingTemplate.getSize();
            // getOrCreate can silently create an empty template if the resource is missing.
            if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) {
                Kruemblegard.LOGGER.warn(
                    "Wayfall spawn island marked placed but template appears empty ({}). Forcing re-place.",
                    data.getStructureId()
                );
                data.setPlaced(false);
                data.setDirty();
            } else {
                // Avoid forcing chunk generation/loading just to validate. If chunks are already loaded
                // (e.g., because a player is present), we can validate cheaply; otherwise skip.
                if (areTemplateChunksLoaded(wayfall, anchor, size) && isSpawnIslandAreaEmpty(wayfall, anchor, size)) {
                    Kruemblegard.LOGGER.warn(
                        "Wayfall spawn island marked placed but area is empty at {} (template {}). Forcing re-place.",
                        anchor,
                        data.getStructureId()
                    );
                    data.setPlaced(false);
                    data.setDirty();
                }
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

            // IMPORTANT: Never force-load/generate FULL chunks here. Wayfall init is queued via
            // WayfallWorkScheduler which (at most) does a one-time preload on first generation/first visit.
            // If chunks aren't ready yet, just skip this tick and let the scheduler retry later.
            if (!areTemplateChunksLoaded(wayfall, anchor, template.getSize())) {
                if (ModConfig.WAYFALL_DEBUG_LOGGING.get()) {
                    Kruemblegard.LOGGER.info(
                        "Wayfall spawn island placement deferred (chunks not loaded yet): template={} size={}x{}x{} anchor={}",
                        structureId,
                        template.getSize().getX(),
                        template.getSize().getY(),
                        template.getSize().getZ(),
                        anchor
                    );
                }
                return;
            }

            StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(true)
                // The origin island templates contain jigsaw blocks as authoring markers.
                // When placing templates directly (not via the jigsaw structure pipeline), we must
                // apply the replacement processor so those markers don't remain in the world.
                .addProcessor(JigsawReplacementProcessor.INSTANCE);

            applyBiomeRunegrowthProcessors(wayfall, spawn, settings);
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
                wayfall.setBlock(markerPos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static boolean areTemplateChunksLoaded(ServerLevel level, BlockPos anchor, Vec3i size) {
        int pad = CHUNK_LOAD_PADDING_BLOCKS;
        int sizeX = Math.max(1, size.getX());
        int sizeZ = Math.max(1, size.getZ());

        int minBlockX = anchor.getX() - pad;
        int maxBlockX = anchor.getX() + sizeX + pad;
        int minBlockZ = anchor.getZ() - pad;
        int maxBlockZ = anchor.getZ() + sizeZ + pad;

        int minChunkX = minBlockX >> 4;
        int maxChunkX = maxBlockX >> 4;
        int minChunkZ = minBlockZ >> 4;
        int maxChunkZ = maxBlockZ >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!level.getChunkSource().hasChunk(cx, cz)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSpawnIslandAreaEmpty(ServerLevel level, BlockPos anchor, Vec3i size) {
        // Heuristic: the spawn island template should contribute at least some solid collision blocks.
        // IMPORTANT: only sample inside the template bounds (the anchor is a template corner).
        int sizeX = Math.max(1, size.getX());
        int sizeY = Math.max(1, size.getY());
        int sizeZ = Math.max(1, size.getZ());

        int minX = anchor.getX();
        int minY = anchor.getY();
        int minZ = anchor.getZ();
        int maxY = Math.min(level.getMaxBuildHeight() - 1, minY + sizeY - 1);

        int solidCount = 0;
        // Sample biased toward the lower portion where solid ground should be.
        int sampleCount = 64;
        int ySpan = Math.min(10, Math.max(1, sizeY));
        for (int i = 0; i < sampleCount; i++) {
            int x = minX + Math.floorMod(i * 7, sizeX);
            int z = minZ + Math.floorMod(i * 11, sizeZ);
            int y = Math.min(maxY, minY + Math.floorMod(i * 13, ySpan));

            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }

            VoxelShape shape = state.getCollisionShape(level, pos);
            if (!shape.isEmpty()) {
                solidCount++;
                if (solidCount >= 8) {
                    return false;
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
                    wayfall.setBlock(markerPos, Blocks.AIR.defaultBlockState(), 2);
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
        // Avoid neighbor-update storms; we only need the block state changed + client sync.
        wayfall.setBlock(landing, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
        wayfall.setBlock(landing.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
        wayfall.setBlock(landing.above(2), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);

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

    private static void applyBiomeRunegrowthProcessors(ServerLevel wayfall, BlockPos spawn, StructurePlaceSettings settings) {
        ResourceKey<StructureProcessorList> processorKey = selectRunegrowthProcessorList(wayfall, spawn);
        if (processorKey == null) {
            return;
        }

        try {
            Holder<StructureProcessorList> holder = wayfall.registryAccess()
                .registryOrThrow(Registries.PROCESSOR_LIST)
                .getHolderOrThrow(processorKey);

            for (StructureProcessor processor : holder.value().list()) {
                settings.addProcessor(processor);
            }
        } catch (Exception ex) {
            Kruemblegard.LOGGER.warn(
                "Failed to apply Wayfall origin island runegrowth processor list {}: {}",
                processorKey.location(),
                ex.toString()
            );
        }
    }

    private static ResourceKey<StructureProcessorList> selectRunegrowthProcessorList(ServerLevel wayfall, BlockPos spawn) {
        ResourceKey<?> biomeKey = wayfall.getBiome(spawn).unwrapKey().orElse(null);

        // Explicit biome mappings (preferred over temperature fallback).
        if (biomeKey == ModWorldgenKeys.Biomes.GLYPHSCAR_REACH || biomeKey == ModWorldgenKeys.Biomes.HOLLOW_TRANSIT_PLAINS) {
            return ModWorldgenKeys.ProcessorLists.WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_FROSTBOUND;
        }
        if (biomeKey == ModWorldgenKeys.Biomes.SHATTERPLATE_FLATS
            || biomeKey == ModWorldgenKeys.Biomes.BETWEENLIGHT_VOID
            || biomeKey == ModWorldgenKeys.Biomes.CRUMBLED_CROSSING
            || biomeKey == ModWorldgenKeys.Biomes.UNDERWAY_FALLS) {
            return ModWorldgenKeys.ProcessorLists.WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_VERDANT;
        }
        if (biomeKey == ModWorldgenKeys.Biomes.FRACTURE_SHOALS
            || biomeKey == ModWorldgenKeys.Biomes.BASIN_OF_SCARS
            || biomeKey == ModWorldgenKeys.Biomes.STRATA_COLLAPSE) {
            return ModWorldgenKeys.ProcessorLists.WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_EMBERWARMED;
        }

        // Temperature fallback for any future cold biomes.
        float temp = wayfall.getBiome(spawn).value().getBaseTemperature();
        if (temp <= 0.25f) {
            return ModWorldgenKeys.ProcessorLists.WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_FROSTBOUND;
        }

        // Default: keep the base/resonant runegrowth.
        return ModWorldgenKeys.ProcessorLists.WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_RESONANT;
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
