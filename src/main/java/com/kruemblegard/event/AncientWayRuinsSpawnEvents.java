package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AncientWayRuinsSpawnEvents {
    private AncientWayRuinsSpawnEvents() {}

    private static final ResourceLocation ANCIENT_WAY_RUINS_ID = ModWorldgenKeys.Structures.ANCIENT_WAY_RUINS.location();

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Mob mob) || mob.getType().getCategory() != MobCategory.MONSTER) {
            return;
        }

        if (isAllowedMonster(mob.getType())) {
            return;
        }

        if (!isInsideAncientWayRuins(level, mob.blockPosition())) {
            return;
        }

        event.setCanceled(true);
        mob.discard();
    }

    public static boolean isInsideAncientWayRuins(ServerLevel level, BlockPos pos) {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure structure = structureRegistry.get(ANCIENT_WAY_RUINS_ID);
        if (structure == null) {
            return false;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        StructureManager structureManager = level.structureManager();
        List<StructureStart> starts = structureManager.startsForStructure(chunkPos, candidate -> candidate == structure);
        if (starts.isEmpty()) {
            return false;
        }

        for (StructureStart start : starts) {
            if (!start.isValid()) {
                continue;
            }

            for (StructurePiece piece : start.getPieces()) {
                BoundingBox box = piece.getBoundingBox();
                if (box != null && box.isInside(pos)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isAllowedMonster(EntityType<?> entityType) {
        return entityType == EntityType.WARDEN
                || matches(entityType, ModEntities.CEPHALARI_ZOMBIE)
                || matches(entityType, ModEntities.CEPHALARI_HUSK)
                || matches(entityType, ModEntities.CEPHALARI_DROWNED);
    }

    private static boolean matches(EntityType<?> entityType, RegistryObject<? extends EntityType<?>> registryObject) {
        return registryObject.isPresent() && entityType == registryObject.get();
    }
}