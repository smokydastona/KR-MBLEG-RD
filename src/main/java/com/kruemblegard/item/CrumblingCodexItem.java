package com.kruemblegard.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;

public final class CrumblingCodexItem extends WrittenBookItem {

    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PAGES = "pages";

    public CrumblingCodexItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack createFilledStack(Item item) {
        ItemStack stack = new ItemStack(item);
        ensureFilled(stack);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ensureFilled(stack);

        // Let vanilla open the book UI.
        return super.use(level, player, hand);
    }

    private static void ensureFilled(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(TAG_PAGES)) {
            return;
        }

        tag.putString(TAG_TITLE, "Crumbling Codex");
        tag.putString(TAG_AUTHOR, "Krümblegård");

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(toJsonPage("Crumbling Codex\n\nA guide to the ruins of Krümblegård.")));
        pages.add(StringTag.valueOf(toJsonPage(
            "How to start\n\n" +
            "1) Explore until you find a Haunted Waystone.\n" +
            "2) Right-click it to begin the encounter.\n" +
            "3) The arena forms and Krümblegård emerges.")));
        pages.add(StringTag.valueOf(toJsonPage(
            "Survival tips\n\n" +
            "- Krümblegård grows more dangerous each phase.\n" +
            "- Watch for new attack patterns as the fight escalates.\n" +
            "- Stay mobile during ranged attacks.")));
        pages.add(StringTag.valueOf(toJsonPage(
            "Rewards\n\n" +
            "Defeat Krümblegård to earn a Runic Core.\n" +
            "Use it to craft Runic tools.")));

        tag.put(TAG_PAGES, pages);

        // Mark as resolved so the client doesn't try to resolve it later.
        tag.putBoolean("resolved", true);
    }

    private static String toJsonPage(String text) {
        return Component.Serializer.toJson(Component.literal(text));
    }
}
