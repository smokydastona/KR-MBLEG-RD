package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.structure.AncientWayRuinsPiece;
import com.kruemblegard.world.structure.AncientWayRuinsStructure;

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

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Kruemblegard.MOD_ID);

    public static final RegistryObject<StructureType<AncientWayRuinsStructure>> ANCIENT_WAY_RUINS =
            STRUCTURE_TYPES.register("ancient_way_ruins", () -> () -> AncientWayRuinsStructure.CODEC);

    public static final RegistryObject<StructurePieceType> ANCIENT_WAY_RUINS_PIECE =
            STRUCTURE_PIECE_TYPES.register("ancient_way_ruins", () -> (StructurePieceType.ContextlessType) AncientWayRuinsPiece::new);

    public static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        STRUCTURE_PIECE_TYPES.register(bus);
    }
}
