package com.kruemblegard.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import java.util.List;

public class RunePetalItem extends Item {
    public static final String TAG_ENCHANTMENT = "Enchantment";

    public RunePetalItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_ENCHANTMENT)) {
            String id = tag.getString(TAG_ENCHANTMENT);
            tooltip.add(Component.literal(id).withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.literal("Unattuned").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static ItemStack withEnchantment(ItemStack base, ResourceLocation enchantmentId) {
        CompoundTag tag = base.getOrCreateTag();
        tag.putString(TAG_ENCHANTMENT, enchantmentId.toString());
        return base;
    }
}
