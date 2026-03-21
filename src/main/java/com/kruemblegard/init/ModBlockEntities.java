package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.blockentity.AirLiftTubeBlockEntity;
import com.kruemblegard.blockentity.AtmosphericCompressorBlockEntity;
import com.kruemblegard.blockentity.BuoyancyLiftPlatformBlockEntity;
import com.kruemblegard.blockentity.ConveyorMembraneBlockEntity;
import com.kruemblegard.blockentity.CrystalInfuserBlockEntity;
import com.kruemblegard.blockentity.MembranePressBlockEntity;
import com.kruemblegard.blockentity.MembranePumpBlockEntity;
import com.kruemblegard.blockentity.PneumaticCatapultBlockEntity;
import com.kruemblegard.blockentity.PneumaticSeparatorBlockEntity;
import com.kruemblegard.blockentity.PressureClutchBlockEntity;
import com.kruemblegard.blockentity.PressureConduitBlockEntity;
import com.kruemblegard.blockentity.PressureKilnBlockEntity;
import com.kruemblegard.blockentity.PressureLoomBlockEntity;
import com.kruemblegard.blockentity.PressureRailBlockEntity;
import com.kruemblegard.blockentity.PressureRegulatorBlockEntity;
import com.kruemblegard.blockentity.PressureSensorBlockEntity;
import com.kruemblegard.blockentity.PressureSequencerBlockEntity;
import com.kruemblegard.blockentity.PressureTurbineBlockEntity;
import com.kruemblegard.blockentity.PressureValveBlockEntity;
import com.kruemblegard.blockentity.SpiralGearboxBlockEntity;
import com.kruemblegard.blockentity.SpiralShaftBlockEntity;
import com.kruemblegard.blockentity.VentPistonBlockEntity;
import com.kruemblegard.blockentity.VortexFunnelBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Kruemblegard.MOD_ID);

    public static final RegistryObject<BlockEntityType<PressureConduitBlockEntity>> PRESSURE_CONDUIT =
            BLOCK_ENTITIES.register(
                    "pressure_conduit",
                    () -> BlockEntityType.Builder.of(PressureConduitBlockEntity::new, ModBlocks.PRESSURE_CONDUIT.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<MembranePumpBlockEntity>> MEMBRANE_PUMP =
            BLOCK_ENTITIES.register(
                    "membrane_pump",
                    () -> BlockEntityType.Builder.of(MembranePumpBlockEntity::new, ModBlocks.MEMBRANE_PUMP.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureTurbineBlockEntity>> PRESSURE_TURBINE =
            BLOCK_ENTITIES.register(
                    "pressure_turbine",
                    () -> BlockEntityType.Builder.of(PressureTurbineBlockEntity::new, ModBlocks.PRESSURE_TURBINE.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<SpiralGearboxBlockEntity>> SPIRAL_GEARBOX =
            BLOCK_ENTITIES.register(
                    "spiral_gearbox",
                    () -> BlockEntityType.Builder.of(SpiralGearboxBlockEntity::new, ModBlocks.SPIRAL_GEARBOX.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<SpiralShaftBlockEntity>> SPIRAL_SHAFT =
            BLOCK_ENTITIES.register(
                    "spiral_shaft",
                    () -> BlockEntityType.Builder.of(SpiralShaftBlockEntity::new, ModBlocks.SPIRAL_SHAFT.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<VentPistonBlockEntity>> VENT_PISTON =
            BLOCK_ENTITIES.register(
                    "vent_piston",
                    () -> BlockEntityType.Builder.of(VentPistonBlockEntity::new, ModBlocks.VENT_PISTON.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<AtmosphericCompressorBlockEntity>> ATMOSPHERIC_COMPRESSOR =
            BLOCK_ENTITIES.register(
                    "atmospheric_compressor",
                    () -> BlockEntityType.Builder.of(AtmosphericCompressorBlockEntity::new, ModBlocks.ATMOSPHERIC_COMPRESSOR.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureValveBlockEntity>> PRESSURE_VALVE =
            BLOCK_ENTITIES.register(
                    "pressure_valve",
                    () -> BlockEntityType.Builder.of(PressureValveBlockEntity::new, ModBlocks.PRESSURE_VALVE.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<BuoyancyLiftPlatformBlockEntity>> BUOYANCY_LIFT_PLATFORM =
            BLOCK_ENTITIES.register(
                    "buoyancy_lift_platform",
                    () -> BlockEntityType.Builder.of(BuoyancyLiftPlatformBlockEntity::new, ModBlocks.BUOYANCY_LIFT_PLATFORM.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<ConveyorMembraneBlockEntity>> CONVEYOR_MEMBRANE =
            BLOCK_ENTITIES.register(
                    "conveyor_membrane",
                    () -> BlockEntityType.Builder.of(ConveyorMembraneBlockEntity::new, ModBlocks.CONVEYOR_MEMBRANE.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureLoomBlockEntity>> PRESSURE_LOOM =
            BLOCK_ENTITIES.register(
                    "pressure_loom",
                    () -> BlockEntityType.Builder.of(PressureLoomBlockEntity::new, ModBlocks.PRESSURE_LOOM.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureClutchBlockEntity>> PRESSURE_CLUTCH =
            BLOCK_ENTITIES.register(
                    "pressure_clutch",
                    () -> BlockEntityType.Builder.of(PressureClutchBlockEntity::new, ModBlocks.PRESSURE_CLUTCH.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureRegulatorBlockEntity>> PRESSURE_REGULATOR =
            BLOCK_ENTITIES.register(
                    "pressure_regulator",
                    () -> BlockEntityType.Builder.of(PressureRegulatorBlockEntity::new, ModBlocks.PRESSURE_REGULATOR.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureSequencerBlockEntity>> PRESSURE_SEQUENCER =
            BLOCK_ENTITIES.register(
                    "pressure_sequencer",
                    () -> BlockEntityType.Builder.of(PressureSequencerBlockEntity::new, ModBlocks.PRESSURE_SEQUENCER.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureSensorBlockEntity>> PRESSURE_SENSOR =
            BLOCK_ENTITIES.register(
                    "pressure_sensor",
                    () -> BlockEntityType.Builder.of(PressureSensorBlockEntity::new, ModBlocks.PRESSURE_SENSOR.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<VortexFunnelBlockEntity>> VORTEX_FUNNEL =
            BLOCK_ENTITIES.register(
                    "vortex_funnel",
                    () -> BlockEntityType.Builder.of(VortexFunnelBlockEntity::new, ModBlocks.VORTEX_FUNNEL.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureRailBlockEntity>> PRESSURE_RAIL =
            BLOCK_ENTITIES.register(
                    "pressure_rail",
                    () -> BlockEntityType.Builder.of(PressureRailBlockEntity::new, ModBlocks.PRESSURE_RAIL.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PneumaticCatapultBlockEntity>> PNEUMATIC_CATAPULT =
            BLOCK_ENTITIES.register(
                    "pneumatic_catapult",
                    () -> BlockEntityType.Builder.of(PneumaticCatapultBlockEntity::new, ModBlocks.PNEUMATIC_CATAPULT.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<AirLiftTubeBlockEntity>> AIR_LIFT_TUBE =
            BLOCK_ENTITIES.register(
                    "air_lift_tube",
                    () -> BlockEntityType.Builder.of(AirLiftTubeBlockEntity::new, ModBlocks.AIR_LIFT_TUBE.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PressureKilnBlockEntity>> PRESSURE_KILN =
            BLOCK_ENTITIES.register(
                    "pressure_kiln",
                    () -> BlockEntityType.Builder.of(PressureKilnBlockEntity::new, ModBlocks.PRESSURE_KILN.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<MembranePressBlockEntity>> MEMBRANE_PRESS =
            BLOCK_ENTITIES.register(
                    "membrane_press",
                    () -> BlockEntityType.Builder.of(MembranePressBlockEntity::new, ModBlocks.MEMBRANE_PRESS.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<CrystalInfuserBlockEntity>> CRYSTAL_INFUSER =
            BLOCK_ENTITIES.register(
                    "crystal_infuser",
                    () -> BlockEntityType.Builder.of(CrystalInfuserBlockEntity::new, ModBlocks.CRYSTAL_INFUSER.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<PneumaticSeparatorBlockEntity>> PNEUMATIC_SEPARATOR =
            BLOCK_ENTITIES.register(
                    "pneumatic_separator",
                    () -> BlockEntityType.Builder.of(PneumaticSeparatorBlockEntity::new, ModBlocks.PNEUMATIC_SEPARATOR.get()).build(null)
            );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
