package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LostPillagerShipCombatEvents {
    private LostPillagerShipCombatEvents() {}

    private static final ResourceLocation LOST_PILLAGER_SHIP_ID = ModWorldgenKeys.Structures.LOST_PILLAGER_SHIP.location();

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractArrow arrow)) {
            return;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof Pillager)) {
            return;
        }

        if (!isInsideLostPillagerShip(level, arrow.blockPosition())) {
            return;
        }

        // Pillagers use crossbows by default (no Flame enchant). To guarantee "fire damage arrows",
        // ignite their fired projectiles when they're coming from this structure.
        arrow.setSecondsOnFire(6);
    }

    private static boolean isInsideLostPillagerShip(ServerLevel level, net.minecraft.core.BlockPos pos) {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure ship = structureRegistry.get(LOST_PILLAGER_SHIP_ID);
        if (ship == null) {
            return false;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        StructureManager structureManager = level.structureManager();

        List<StructureStart> starts = structureManager.startsForStructure(chunkPos, s -> s == ship);
        if (starts.isEmpty()) {
            return false;
        }

        for (StructureStart start : starts) {
            if (!start.isValid()) {
                continue;
            }

            for (StructurePiece piece : start.getPieces()) {
                BoundingBox bbox = piece.getBoundingBox();
                if (bbox != null && bbox.isInside(pos)) {
                    return true;
                }
            }
        }

        return false;
    }
}
