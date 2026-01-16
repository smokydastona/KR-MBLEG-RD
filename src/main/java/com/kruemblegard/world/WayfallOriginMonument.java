package com.kruemblegard.world;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallOriginMonument {
    private WayfallOriginMonument() {}

    private static final BlockPos ORIGIN = new BlockPos(0, 190, 0);

    private static final int ISLAND_RADIUS = 14;
    private static final int ISLAND_DEPTH = 12;

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.dimension() != ModWorldgenKeys.Levels.WAYFALL) {
            return;
        }

        // Avoid any off-thread world writes.
        serverLevel.getServer().execute(() -> ensurePlaced(serverLevel));
    }

    private static void ensurePlaced(ServerLevel wayfall) {
        WayfallOriginMonumentSavedData data = WayfallOriginMonumentSavedData.get(wayfall);
        if (data.isPlaced()) {
            return;
        }

        // Ensure chunks are fully generated/loaded before writing blocks.
        int minChunkX = (ORIGIN.getX() - ISLAND_RADIUS) >> 4;
        int maxChunkX = (ORIGIN.getX() + ISLAND_RADIUS) >> 4;
        int minChunkZ = (ORIGIN.getZ() - ISLAND_RADIUS) >> 4;
        int maxChunkZ = (ORIGIN.getZ() + ISLAND_RADIUS) >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                wayfall.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, true);
            }
        }

        // Pick a palette based on the biome at the origin.
        ResourceKey<?> biomeKey = wayfall.getBiome(ORIGIN).unwrapKey().orElse(null);
        Palette palette = Palette.forBiome(biomeKey);

        // If the origin happens to land inside terrain, lift slightly above the heightmap.
        int surfaceY = wayfall.getHeight(Heightmap.Types.MOTION_BLOCKING, ORIGIN.getX(), ORIGIN.getZ());
        int targetY = Math.max(ORIGIN.getY(), surfaceY + 6);
        BlockPos center = new BlockPos(ORIGIN.getX(), targetY, ORIGIN.getZ());

        buildIsland(wayfall, center, palette);

        data.setPlaced(true);
        data.setDirty();
    }

    private static void buildIsland(ServerLevel wayfall, BlockPos center, Palette palette) {
        BlockState top = palette.top;
        BlockState mid = palette.mid;
        BlockState deep = palette.deep;

        for (int dx = -ISLAND_RADIUS; dx <= ISLAND_RADIUS; dx++) {
            for (int dz = -ISLAND_RADIUS; dz <= ISLAND_RADIUS; dz++) {
                double dist = Math.sqrt((double) dx * dx + (double) dz * dz);
                if (dist > (double) ISLAND_RADIUS + 0.35D) {
                    continue;
                }

                double t = 1.0D - (dist / (double) ISLAND_RADIUS);
                int columnDepth = 2 + (int) Math.floor(t * (double) ISLAND_DEPTH);

                BlockPos columnTop = center.offset(dx, 0, dz);

                wayfall.setBlockAndUpdate(columnTop, top);

                // Soft stratification: mid under the surface, deep at the tapered underside.
                for (int dy = 1; dy <= columnDepth; dy++) {
                    BlockState state;
                    if (dy <= 2) {
                        state = mid;
                    } else if (dy <= 6) {
                        state = mid;
                    } else {
                        state = deep;
                    }
                    wayfall.setBlockAndUpdate(columnTop.below(dy), state);
                }
            }
        }

        // Keep a safe 3-block headroom at the center.
        wayfall.setBlockAndUpdate(center.above(1), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        wayfall.setBlockAndUpdate(center.above(2), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        wayfall.setBlockAndUpdate(center.above(3), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
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
