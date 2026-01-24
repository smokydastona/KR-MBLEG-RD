package com.kruemblegard.world;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
public final class WayfallOriginMonument {
    private WayfallOriginMonument() {}

    private static final BlockPos ORIGIN = new BlockPos(0, 190, 0);

    private static final int ISLAND_RADIUS = 14;
    private static final int ISLAND_DEPTH = 12;

    private static final int BLOCK_PLACE_FLAGS = 2; // notify clients, avoid neighbor updates (prevents huge hitch)

    static BlockPos getOrigin() {
        return ORIGIN;
    }

    static int getIslandRadius() {
        return ISLAND_RADIUS;
    }

    static WayfallWorkScheduler.WayfallTask createPlacementTask() {
        return new PlacementTask();
    }

    /**
     * Ensures the origin monument island has been placed.
     *
     * This is intentionally NOT executed automatically on dimension load, because forcing chunks to load/generate
     * without a ticking ticket can expose vanilla retention issues when those chunks unload (see MC-272673 class of bugs).
     *
     * Call this when a player actually enters Wayfall (chunks are naturally ticketed and will tick).
     */
    public static void ensurePlaced(ServerLevel wayfall) {
        if (wayfall.dimension() != ModWorldgenKeys.Levels.WAYFALL) {
            return;
        }

        WayfallOriginMonumentSavedData data = WayfallOriginMonumentSavedData.get(wayfall);
        if (data.isPlaced()) {
            return;
        }

        // Never block the server tick doing a large placement. Schedule the work over multiple ticks.
        WayfallWorkScheduler.enqueueOriginMonumentPlacement(wayfall);
    }

    private static final class PlacementTask implements WayfallWorkScheduler.WayfallTask {
        private Palette palette;
        private BlockPos center;
        private int dx = -ISLAND_RADIUS;
        private int dz = -ISLAND_RADIUS;
        private boolean headroomCleared;

        @Override
        public boolean tick(ServerLevel wayfall) {
            if (wayfall.dimension() != ModWorldgenKeys.Levels.WAYFALL) {
                return true;
            }

            WayfallOriginMonumentSavedData data = WayfallOriginMonumentSavedData.get(wayfall);
            if (data.isPlaced()) {
                return true;
            }

            // Wait for chunks to be loaded/ticketed.
            int minChunkX = (ORIGIN.getX() - ISLAND_RADIUS) >> 4;
            int maxChunkX = (ORIGIN.getX() + ISLAND_RADIUS) >> 4;
            int minChunkZ = (ORIGIN.getZ() - ISLAND_RADIUS) >> 4;
            int maxChunkZ = (ORIGIN.getZ() + ISLAND_RADIUS) >> 4;
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    if (!wayfall.getChunkSource().hasChunk(cx, cz)) {
                        return false;
                    }
                }
            }

            if (palette == null) {
                ResourceKey<?> biomeKey = wayfall.getBiome(ORIGIN).unwrapKey().orElse(null);
                palette = Palette.forBiome(biomeKey);

                int surfaceY = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING, ORIGIN.getX(), ORIGIN.getZ());
                int targetY = Math.max(ORIGIN.getY(), surfaceY + 6);
                center = new BlockPos(ORIGIN.getX(), targetY, ORIGIN.getZ());
            }

            int budget = Math.max(100, ModConfig.WAYFALL_BLOCKS_PER_TICK.get());

            while (budget > 0 && dx <= ISLAND_RADIUS) {
                while (budget > 0 && dz <= ISLAND_RADIUS) {
                    double dist = Math.sqrt((double) dx * dx + (double) dz * dz);
                    dz++;
                    if (dist > (double) ISLAND_RADIUS + 0.35D) {
                        continue;
                    }

                    double t = 1.0D - (dist / (double) ISLAND_RADIUS);
                    int columnDepth = 2 + (int) Math.floor(t * (double) ISLAND_DEPTH);
                    BlockPos columnTop = center.offset(dx, 0, dz - 1);

                    wayfall.setBlock(columnTop, palette.top, BLOCK_PLACE_FLAGS);
                    budget--;

                    for (int dy = 1; dy <= columnDepth && budget > 0; dy++) {
                        BlockState state;
                        if (dy <= 6) {
                            state = palette.mid;
                        } else {
                            state = palette.deep;
                        }
                        wayfall.setBlock(columnTop.below(dy), state, BLOCK_PLACE_FLAGS);
                        budget--;
                    }

                    // If we ran out of budget mid-column, resume next tick (same dx, next dz continues).
                    if (budget <= 0) {
                        return false;
                    }
                }

                dz = -ISLAND_RADIUS;
                dx++;
            }

            if (!headroomCleared) {
                wayfall.setBlock(center.above(1), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), BLOCK_PLACE_FLAGS);
                wayfall.setBlock(center.above(2), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), BLOCK_PLACE_FLAGS);
                wayfall.setBlock(center.above(3), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), BLOCK_PLACE_FLAGS);
                headroomCleared = true;
                return false;
            }

            data.setPlaced(true);
            data.setDirty();
            return true;
        }
    }

    private record Palette(BlockState top, BlockState mid, BlockState deep) {
        private static Palette forBiome(ResourceKey<?> biomeKey) {
            if (biomeKey == ModWorldgenKeys.Biomes.STRATA_COLLAPSE) {
                return new Palette(
                        ModBlocks.ASHFALL_LOAM.get().defaultBlockState(),
                        ModBlocks.ASHFALL_STONE.get().defaultBlockState(),
                        ModBlocks.STONEVEIL_RUBBLE.get().defaultBlockState()
                );
            }

            if (biomeKey == ModWorldgenKeys.Biomes.SHATTERPLATE_FLATS) {
                return new Palette(
                        ModBlocks.VOIDFELT.get().defaultBlockState(),
                        ModBlocks.FAULT_DUST.get().defaultBlockState(),
                        ModBlocks.STONEVEIL_RUBBLE.get().defaultBlockState()
                );
            }

            if (biomeKey == ModWorldgenKeys.Biomes.FRACTURE_SHOALS) {
                return new Palette(
                        ModBlocks.FRACTURED_WAYROCK.get().defaultBlockState(),
                        ModBlocks.CRUSHSTONE.get().defaultBlockState(),
                        ModBlocks.STONEVEIL_RUBBLE.get().defaultBlockState()
                );
            }

            if (biomeKey == ModWorldgenKeys.Biomes.GLYPHSCAR_REACH) {
                return new Palette(
                        ModBlocks.FRACTURED_WAYROCK.get().defaultBlockState(),
                        ModBlocks.FAULT_DUST.get().defaultBlockState(),
                        ModBlocks.SCARSTONE.get().defaultBlockState()
                );
            }

            if (biomeKey == ModWorldgenKeys.Biomes.BASIN_OF_SCARS) {
                return new Palette(
                        ModBlocks.RUNEGROWTH.get().defaultBlockState(),
                        ModBlocks.FAULT_DUST.get().defaultBlockState(),
                        ModBlocks.SCARSTONE.get().defaultBlockState()
                );
            }

            // Default Wayfall surface identity.
            return new Palette(
                    ModBlocks.RUNEGROWTH.get().defaultBlockState(),
                    ModBlocks.FAULT_DUST.get().defaultBlockState(),
                    ModBlocks.STONEVEIL_RUBBLE.get().defaultBlockState()
            );
        }
    }
}
