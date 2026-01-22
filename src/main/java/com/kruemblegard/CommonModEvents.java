package com.kruemblegard;

import com.kruemblegard.entity.GreatHungerEntity;
import com.kruemblegard.entity.KruemblegardBossEntity;
import com.kruemblegard.entity.ScatteredEndermanEntity;
import com.kruemblegard.entity.TraprockEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonModEvents {
    private CommonModEvents() {}

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KRUEMBLEGARD.get(), KruemblegardBossEntity.createAttributes().build());
        event.put(ModEntities.TRAPROCK.get(), TraprockEntity.createAttributes().build());
        event.put(ModEntities.PEBBLIT.get(), Silverfish.createAttributes().build());
        event.put(ModEntities.GREAT_HUNGER.get(), GreatHungerEntity.createAttributes().build());
        event.put(ModEntities.SCATTERED_ENDERMAN.get(), ScatteredEndermanEntity.createAttributes().build());
        event.put(ModEntities.MOOGLOOM.get(), Cow.createAttributes().build());
    }

    @SubscribeEvent
    public static void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntities.TRAPROCK.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CommonModEvents::canSpawnOnSolidGround,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(
                ModEntities.PEBBLIT.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CommonModEvents::canSpawnOnSolidGround,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(
            ModEntities.GREAT_HUNGER.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            CommonModEvents::canSpawnOnSolidGround,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(
            ModEntities.SCATTERED_ENDERMAN.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            CommonModEvents::canSpawnOnSolidGround,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(
            ModEntities.MOOGLOOM.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            CommonModEvents::canSpawnAnimalOnSolidGround,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }

    private static boolean canSpawnOnSolidGround(
            net.minecraft.world.entity.EntityType<? extends Monster> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        BlockPos below = pos.below();
        if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
            return false;
        }

        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    private static boolean canSpawnAnimalOnSolidGround(
            net.minecraft.world.entity.EntityType<? extends Animal> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        BlockPos below = pos.below();
        if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
            return false;
        }

        return Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }
}
