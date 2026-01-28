package com.kruemblegard;

import com.kruemblegard.entity.GreatHungerEntity;
import com.kruemblegard.entity.KruemblegardBossEntity;
import com.kruemblegard.entity.ScatteredEndermanEntity;
import com.kruemblegard.entity.TraprockEntity;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;

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
        // Wayfall: Glow Squid are air swimmers and should be able to spawn at any height/light.
        event.register(
            EntityType.GLOW_SQUID,
            null,
            null,
            CommonModEvents::canSpawnGlowSquid,
            SpawnPlacementRegisterEvent.Operation.OR
        );

        // Wayfall: fish spawn rules must tolerate high-altitude lakes (no sea-level assumptions).
        event.register(
            EntityType.COD,
            null,
            null,
            CommonModEvents::canSpawnWayfallFish,
            SpawnPlacementRegisterEvent.Operation.OR
        );

        event.register(
            EntityType.SALMON,
            null,
            null,
            CommonModEvents::canSpawnWayfallFish,
            SpawnPlacementRegisterEvent.Operation.OR
        );

        event.register(
            EntityType.TROPICAL_FISH,
            null,
            null,
            CommonModEvents::canSpawnWayfallFish,
            SpawnPlacementRegisterEvent.Operation.OR
        );

        event.register(
            EntityType.PUFFERFISH,
            null,
            null,
            CommonModEvents::canSpawnWayfallFish,
            SpawnPlacementRegisterEvent.Operation.OR
        );

        // Wayfall: axolotls should be able to spawn in high-altitude lakes (Underway Falls).
        event.register(
            EntityType.AXOLOTL,
            null,
            null,
            CommonModEvents::canSpawnWayfallAxolotl,
            SpawnPlacementRegisterEvent.Operation.OR
        );

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
            CommonModEvents::canSpawnMoogloom,
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

    private static boolean canSpawnMoogloom(
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

        // Require space for the mob.
        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
            return false;
        }

        if (!level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
            return false;
        }

        // Similar to vanilla animal rules, but without requiring the block below to be in
        // `minecraft:animals_spawnable_on` (Wayfall biomes use custom ground blocks).
        if (level.getLevel().getRawBrightness(pos, 0) < 9) {
            return false;
        }

        return true;
    }

    private static boolean canSpawnGlowSquid(
            net.minecraft.world.entity.EntityType<? extends GlowSquid> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        if (level.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            // Allow any height/light in Wayfall, but restrict to water bodies so we don't
            // spawn Glow Squid everywhere in open air (which can explode entity counts).
            boolean inWater = level.getFluidState(pos).getType() == Fluids.WATER
                    && level.getFluidState(pos.above()).getType() == Fluids.WATER;

            boolean justAboveWaterSurface = level.getFluidState(pos.below()).getType() == Fluids.WATER
                    && level.getFluidState(pos).getType() != Fluids.WATER
                    && level.getFluidState(pos.above()).getType() != Fluids.WATER;

            if (!inWater && !justAboveWaterSurface) {
                return false;
            }

            // Require space for the mob.
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return false;
            }

            if (!level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
                return false;
            }

            return true;
        }

        // Vanilla-ish fallback (so this override doesn't make Glow Squid spawn everywhere):
        // - must be in water
        // - dark (no sky/block light)
        // - deep-ish Y (vanilla uses low Y; we approximate with <= 30)
        if (level.getFluidState(pos).getType() != Fluids.WATER) {
            return false;
        }

        if (level.getFluidState(pos.above()).getType() != Fluids.WATER) {
            return false;
        }

        if (pos.getY() > 30) {
            return false;
        }

        int sky = level.getLevel().getBrightness(LightLayer.SKY, pos);
        int block = level.getLevel().getBrightness(LightLayer.BLOCK, pos);
        return sky == 0 && block == 0;
    }

    private static boolean canSpawnWayfallFish(
            net.minecraft.world.entity.EntityType<? extends AbstractFish> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        // Only loosen in Wayfall; elsewhere vanilla spawn placement stays authoritative (via OR).
        if (!level.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return false;
        }

        // Fish should still spawn in water, but height/light should not matter in Wayfall.
        if (level.getFluidState(pos).getType() != Fluids.WATER) {
            return false;
        }

        if (level.getFluidState(pos.above()).getType() != Fluids.WATER) {
            return false;
        }

        return true;
    }

    private static boolean canSpawnWayfallAxolotl(
            net.minecraft.world.entity.EntityType<? extends Axolotl> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        if (!level.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return false;
        }

        // Keep axolotls water-spawned, but ignore height/light constraints in Wayfall.
        if (level.getFluidState(pos).getType() != Fluids.WATER) {
            return false;
        }

        if (level.getFluidState(pos.above()).getType() != Fluids.WATER) {
            return false;
        }

        return true;
    }
}
