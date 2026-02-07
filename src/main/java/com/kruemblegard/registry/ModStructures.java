package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.structure.LostPillagerShipPiece;
import com.kruemblegard.world.structure.LostPillagerShipStructure;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructures {
    private ModStructures() {}

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Kruemblegard.MOD_ID);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Kruemblegard.MOD_ID);

    public static final RegistryObject<StructureType<LostPillagerShipStructure>> LOST_PILLAGER_SHIP = STRUCTURE_TYPES.register(
            "lost_pillager_ship",
            () -> () -> LostPillagerShipStructure.CODEC
    );

    public static final RegistryObject<StructurePieceType> LOST_PILLAGER_SHIP_PIECE = STRUCTURE_PIECES.register(
            "lost_pillager_ship_piece",
            () -> LostPillagerShipPiece::new
    );

    public static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        STRUCTURE_PIECES.register(bus);
    }
}
