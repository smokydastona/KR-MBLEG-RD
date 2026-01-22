package com.kruemblegard.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class MoogloomEntity extends MushroomCow {

    public MoogloomEntity(EntityType<? extends MushroomCow> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.SHEARS) && this.readyForShearing()) {
            if (!this.level().isClientSide) {
                itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                this.level().playSound(null, this.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);

                // Special behavior: shearing kills the Moogloom (and triggers normal death drops).
                this.hurt(this.damageSources().playerAttack(player), Float.MAX_VALUE);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }
}
