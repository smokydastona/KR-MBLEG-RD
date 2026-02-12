package com.kruemblegard.item;

import com.kruemblegard.entity.ScaralonBeetleEntity;
import com.kruemblegard.registry.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * A simple larva bucket: placing it spawns a baby Scaralon Beetle (larva form) and returns an empty bucket.
 */
public class ScaralonLarvaBucketItem extends Item {

    private static final String NBT_TEXTURE_VARIANT = "TextureVariant";

    public ScaralonLarvaBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());

        ScaralonBeetleEntity larva = ModEntities.SCARALON_BEETLE.get().create(serverLevel);
        if (larva == null) {
            return InteractionResult.PASS;
        }

        if (stack.hasTag() && stack.getTag().contains(NBT_TEXTURE_VARIANT)) {
            larva.setTextureVariant(stack.getTag().getInt(NBT_TEXTURE_VARIANT));
        }

        larva.setBaby(true);
        larva.setAge(-24000);
        larva.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.05D, spawnPos.getZ() + 0.5D, serverLevel.random.nextFloat() * 360.0F, 0.0F);
        serverLevel.addFreshEntity(larva);

        serverLevel.playSound(null, spawnPos, SoundEvents.BUCKET_EMPTY_AXOLOTL, SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (player != null && !player.getAbilities().instabuild) {
            // Replace with empty bucket.
            if (stack.getCount() == 1) {
                player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
            } else {
                stack.shrink(1);
                ItemStack empty = new ItemStack(Items.BUCKET);
                if (!player.getInventory().add(empty)) {
                    player.drop(empty, false);
                }
            }
        }

        return InteractionResult.CONSUME;
    }
}
