package com.kruemblegard;

import com.kruemblegard.entity.MoogloomEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.MushroomCow;

import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEvents {
    private static final int MOOSHROOM_MUTATE_CHANCE = 1024;

    private CommonForgeEvents() {}

    @SubscribeEvent
    public static void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        Mob parentA = event.getParentA();
        Mob parentB = event.getParentB();

        if (!(parentA instanceof MushroomCow a) || !(parentB instanceof MushroomCow b)) {
            return;
        }

        boolean aIsMoogloom = a instanceof MoogloomEntity;
        boolean bIsMoogloom = b instanceof MoogloomEntity;
        if (!aIsMoogloom && !bIsMoogloom) {
            return;
        }

        // Moogloom + Moogloom is already handled by MoogloomEntity#getBreedOffspring.
        if (aIsMoogloom && bIsMoogloom) {
            return;
        }

        if (!(a.level() instanceof ServerLevel level)) {
            return;
        }

        RandomSource random = level.getRandom();

        // Mixed pairing: 5% chance to produce a brown mooshroom.
        if (random.nextInt(20) == 0) {
            MushroomCow babyBrown = net.minecraft.world.entity.EntityType.MOOSHROOM.create(level);
            if (babyBrown != null) {
                babyBrown.setVariant(MushroomCow.MushroomType.BROWN);
                event.setChild(babyBrown);
            }
            return;
        }

        MushroomCow.MushroomType variant = getOffspringType(a.getVariant(), b.getVariant(), random);

        boolean babyIsMoogloom = random.nextBoolean();
        if (babyIsMoogloom) {
            MoogloomEntity baby = ModEntities.MOOGLOOM.get().create(level);
            if (baby != null) {
                baby.setVariant(variant);
                event.setChild(baby);
            }
            return;
        }

        MushroomCow babyMooshroom = net.minecraft.world.entity.EntityType.MOOSHROOM.create(level);
        if (babyMooshroom != null) {
            babyMooshroom.setVariant(variant);
            event.setChild(babyMooshroom);
        }
    }

    private static MushroomCow.MushroomType getOffspringType(
            MushroomCow.MushroomType parentA,
            MushroomCow.MushroomType parentB,
            RandomSource random
    ) {
        if (parentA == parentB && random.nextInt(MOOSHROOM_MUTATE_CHANCE) == 0) {
            return parentA == MushroomCow.MushroomType.BROWN ? MushroomCow.MushroomType.RED : MushroomCow.MushroomType.BROWN;
        }

        return random.nextBoolean() ? parentA : parentB;
    }
}
