package com.kruemblegard.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class MoogloomEntity extends MushroomCow {

    private static final int MUTATE_CHANCE = 1024;

    public MoogloomEntity(EntityType<? extends MushroomCow> type, Level level) {
        super(type, level);
    }

    @Override
    public List<ItemStack> onSheared(
            @Nullable net.minecraft.world.entity.player.Player player,
            @org.jetbrains.annotations.NotNull ItemStack item,
            Level world,
            BlockPos pos,
            int fortune
    ) {
        this.gameEvent(GameEvent.SHEAR, player);
        return shearInternal(player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS);
    }

    @Override
    public void shear(SoundSource source) {
        shearInternal(source).forEach(s -> this.spawnAtLocation(s, 1.0F));
    }

    private List<ItemStack> shearInternal(SoundSource source) {
        this.level().playSound(null, this, SoundEvents.MOOSHROOM_SHEAR, source, 1.0F, 1.0F);
        if (!this.level().isClientSide()) {
            Cow cow = EntityType.COW.create(this.level());
            if (cow != null) {
                ((ServerLevel)this.level()).sendParticles(
                        ParticleTypes.EXPLOSION,
                        this.getX(),
                        this.getY(0.5D),
                        this.getZ(),
                        1,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D
                );

                this.discard();
                cow.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                cow.setHealth(this.getHealth());
                cow.yBodyRot = this.yBodyRot;
                if (this.hasCustomName()) {
                    cow.setCustomName(this.getCustomName());
                    cow.setCustomNameVisible(this.isCustomNameVisible());
                }

                if (this.isPersistenceRequired()) {
                    cow.setPersistenceRequired();
                }

                cow.setInvulnerable(this.isInvulnerable());
                this.level().addFreshEntity(cow);

                List<ItemStack> drops = new ArrayList<>();
                for (int i = 0; i < 5; ++i) {
                    drops.add(new ItemStack(ModBlocks.GRIEFCAP.get()));
                }
                return drops;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean canMate(Animal other) {
        if (other == this) {
            return false;
        }

        if (!(other instanceof MushroomCow)) {
            return false;
        }

        return this.isInLove() && other.isInLove();
    }

    @Override
    @Nullable
    public MushroomCow getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        if (!(otherParent instanceof MushroomCow otherMushroomCow)) {
            return super.getBreedOffspring(level, otherParent);
        }

        // Moogloom + Moogloom always produces a Moogloom.
        if (otherParent instanceof MoogloomEntity) {
            MoogloomEntity baby = ModEntities.MOOGLOOM.get().create(level);
            if (baby != null) {
                baby.setVariant(getOffspringType(this.getVariant(), otherMushroomCow.getVariant(), this.random));
            }
            return baby;
        }

        // Mixed pairing: 5% chance to produce a brown mooshroom.
        if (this.random.nextInt(20) == 0) {
            MushroomCow babyBrown = EntityType.MOOSHROOM.create(level);
            if (babyBrown != null) {
                babyBrown.setVariant(MushroomCow.MushroomType.BROWN);
            }
            return babyBrown;
        }

        boolean babyIsMoogloom = this.random.nextBoolean();
        if (babyIsMoogloom) {
            MoogloomEntity baby = ModEntities.MOOGLOOM.get().create(level);
            if (baby != null) {
                // Keep the underlying MushroomCow variant consistent, even though Moogloom uses a custom render.
                baby.setVariant(getOffspringType(this.getVariant(), otherMushroomCow.getVariant(), this.random));
            }
            return baby;
        }

        MushroomCow babyMooshroom = EntityType.MOOSHROOM.create(level);
        if (babyMooshroom != null) {
            // Preserve vanilla variant mixing behavior for the mooshroom child.
            babyMooshroom.setVariant(getOffspringType(this.getVariant(), otherMushroomCow.getVariant(), this.random));
        }
        return babyMooshroom;
    }

    private static MushroomCow.MushroomType getOffspringType(
            MushroomCow.MushroomType parentA,
            MushroomCow.MushroomType parentB,
            RandomSource random
    ) {
        if (parentA == parentB && random.nextInt(MUTATE_CHANCE) == 0) {
            return parentA == MushroomCow.MushroomType.BROWN ? MushroomCow.MushroomType.RED : MushroomCow.MushroomType.BROWN;
        }

        return random.nextBoolean() ? parentA : parentB;
    }
}
