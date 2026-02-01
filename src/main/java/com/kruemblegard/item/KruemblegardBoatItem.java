package com.kruemblegard.item;

import com.kruemblegard.entity.vehicle.KruemblegardBoatEntity;
import com.kruemblegard.entity.vehicle.KruemblegardBoatType;
import com.kruemblegard.entity.vehicle.KruemblegardChestBoatEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;

public class KruemblegardBoatItem extends Item {
    private final KruemblegardBoatType boatType;
    private final boolean chestBoat;

    public KruemblegardBoatItem(KruemblegardBoatType boatType, boolean chestBoat, Properties properties) {
        super(properties.stacksTo(1));
        this.boatType = boatType;
        this.chestBoat = chestBoat;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            double x = hit.getLocation().x;
            double y = hit.getLocation().y;
            double z = hit.getLocation().z;

            if (chestBoat) {
                var boat = new KruemblegardChestBoatEntity(ModEntities.KRUEMBLEGARD_CHEST_BOAT.get(), level);
                boat.setPos(x, y, z);
                boat.setYRot(player.getYRot());
                boat.setKruemblegardBoatType(boatType);

                if (level.noCollision(boat, boat.getBoundingBox())) {
                    level.addFreshEntity(boat);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, boat.position());
                } else {
                    return InteractionResultHolder.fail(stack);
                }
            } else {
                var boat = new KruemblegardBoatEntity(ModEntities.KRUEMBLEGARD_BOAT.get(), level);
                boat.setPos(x, y, z);
                boat.setYRot(player.getYRot());
                boat.setKruemblegardBoatType(boatType);

                if (level.noCollision(boat, boat.getBoundingBox())) {
                    level.addFreshEntity(boat);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, boat.position());
                } else {
                    return InteractionResultHolder.fail(stack);
                }
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
