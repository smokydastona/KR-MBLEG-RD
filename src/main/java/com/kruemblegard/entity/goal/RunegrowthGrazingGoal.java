package com.kruemblegard.entity.goal;

import java.util.EnumSet;
import java.util.function.Predicate;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RunegrowthGrazingGoal extends Goal {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final int EAT_TRIGGER_TICK = 4;

    private final Mob mob;
    private final Predicate<Mob> shouldGraze;
    private final boolean allowGrassBlock;
    private final int chance;

    private int eatAnimationTick;

    public RunegrowthGrazingGoal(Mob mob, Predicate<Mob> shouldGraze, boolean allowGrassBlock, int chance) {
        this.mob = mob;
        this.shouldGraze = shouldGraze;
        this.allowGrassBlock = allowGrassBlock;
        this.chance = Math.max(1, chance);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.shouldGraze.test(this.mob)) {
            return false;
        }

        if (this.mob.isPassenger() || !this.mob.onGround()) {
            return false;
        }

        if (this.mob.getRandom().nextInt(this.chance) != 0) {
            return false;
        }

        return isEdibleTarget(this.mob.level(), this.mob.blockPosition().below());
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0
            && this.shouldGraze.test(this.mob)
            && isEdibleTarget(this.mob.level(), this.mob.blockPosition().below());
    }

    @Override
    public void start() {
        this.eatAnimationTick = this.adjustedTickDelay(EAT_ANIMATION_TICKS);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.eatAnimationTick <= 0) {
            return;
        }

        this.mob.getNavigation().stop();
        this.eatAnimationTick--;

        if (this.eatAnimationTick == this.adjustedTickDelay(EAT_TRIGGER_TICK)) {
            BlockPos targetPos = this.mob.blockPosition().below();
            if (consumeIfEdible(this.mob.level(), targetPos)) {
                this.mob.ate();
            }
        }
    }

    private boolean isEdibleTarget(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return isRunegrowth(state) || (this.allowGrassBlock && state.is(Blocks.GRASS_BLOCK));
    }

    private boolean consumeIfEdible(Level level, BlockPos pos) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.GRASS_BLOCK)) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 2);
            return true;
        }

        if (isRunegrowth(state)) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return true;
        }

        return false;
    }

    private static boolean isRunegrowth(BlockState state) {
        return state.is(ModBlocks.RUNEGROWTH.get())
            || state.is(ModBlocks.FROSTBOUND_RUNEGROWTH.get())
            || state.is(ModBlocks.VERDANT_RUNEGROWTH.get())
            || state.is(ModBlocks.EMBERWARMED_RUNEGROWTH.get());
    }
}