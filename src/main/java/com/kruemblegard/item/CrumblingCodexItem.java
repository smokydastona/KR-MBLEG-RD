package com.kruemblegard.item;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.kruemblegard.Kruemblegard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
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
    private static final String TAG_RESOLVED = "resolved";

    private static final ResourceLocation CONTENTS_ID = new ResourceLocation(Kruemblegard.MOD_ID, "books/crumbling_codex.json");

    private static final String MOD_DATA_KEY = Kruemblegard.MOD_ID;
    private static final String MOD_DATA_AUTOFILLED = "codex_autofilled";

    public CrumblingCodexItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack createFilledStack(Item item) {
        ItemStack stack = new ItemStack(item);
        markAutofilled(stack);
        fillWithDefaults(stack);
        return stack;
    }

    public static ItemStack createServerFilledStack(Item item, Level level) {
        ItemStack stack = new ItemStack(item);
        markAutofilled(stack);
        ensureFilledFromServer(level, stack);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            ensureFilledFromServer(level, stack);
        }

        // Let vanilla open the book UI.
        return super.use(level, player, hand);
    }

    private static void ensureFilledFromServer(Level level, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(TAG_PAGES) && !isAutofilled(tag)) {
            return;
        }

        CodexContents contents = loadContents(level.getServer()).orElseGet(CrumblingCodexItem::defaultContents);
        writeContents(tag, contents);

        // Mark as resolved so the client doesn't try to resolve it later.
        tag.putBoolean(TAG_RESOLVED, true);

        // Keep this true so server content can refresh stacks created earlier.
        setAutofilled(tag, true);
    }

    private static String toJsonPage(String text) {
        return Component.Serializer.toJson(Component.literal(text));
    }

    private static void fillWithDefaults(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        writeContents(tag, defaultContents());
        tag.putBoolean(TAG_RESOLVED, true);
    }

    private static void writeContents(CompoundTag tag, CodexContents contents) {
        tag.putString(TAG_TITLE, contents.title);
        tag.putString(TAG_AUTHOR, contents.author);

        ListTag pages = new ListTag();
        for (String page : contents.pages) {
            pages.add(StringTag.valueOf(toJsonPage(page)));
        }
        tag.put(TAG_PAGES, pages);
    }

    private static void markAutofilled(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        setAutofilled(tag, true);
    }

    private static boolean isAutofilled(CompoundTag tag) {
        if (!tag.contains(MOD_DATA_KEY, CompoundTag.TAG_COMPOUND)) {
            return false;
        }
        return tag.getCompound(MOD_DATA_KEY).getBoolean(MOD_DATA_AUTOFILLED);
    }

    private static void setAutofilled(CompoundTag tag, boolean value) {
        CompoundTag modData = tag.getCompound(MOD_DATA_KEY);
        modData.putBoolean(MOD_DATA_AUTOFILLED, value);
        tag.put(MOD_DATA_KEY, modData);
    }

    private static Optional<CodexContents> loadContents(MinecraftServer server) {
        if (server == null) {
            return Optional.empty();
        }

        try {
            Optional<Resource> resource = server.getResourceManager().getResource(CONTENTS_ID);
            if (resource.isEmpty()) {
                return Optional.empty();
            }

            try (Reader reader = resource.get().openAsReader()) {
                JsonObject obj = GsonHelper.parse(reader);
                String title = GsonHelper.getAsString(obj, "title", "Crumbling Codex");
                String author = GsonHelper.getAsString(obj, "author", "Krümblegård");
                JsonArray pagesJson = GsonHelper.getAsJsonArray(obj, "pages");

                List<String> pages = new ArrayList<>();
                for (int i = 0; i < pagesJson.size(); i++) {
                    pages.add(GsonHelper.convertToString(pagesJson.get(i), "pages[" + i + "]"));
                }

                if (pages.isEmpty()) {
                    return Optional.empty();
                }

                return Optional.of(new CodexContents(title, author, pages));
            }
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private static CodexContents defaultContents() {
        List<String> pages = List.of(
            "Crumbling Codex\n\nA guide to the ruins of Krümblegård.",
            "How to start\n\n" +
                "1) Explore until you find a Haunted Waystone.\n" +
                "2) Right-click it to begin the encounter.\n" +
                "3) The arena forms and Krümblegård emerges.",
            "Survival tips\n\n" +
                "- Krümblegård grows more dangerous each phase.\n" +
                "- Watch for new attack patterns as the fight escalates.\n" +
                "- Stay mobile during ranged attacks.",
            "Rewards\n\n" +
                "Defeat Krümblegård to earn a Runic Core.\n" +
                "Use it to craft Runic tools."
        );

        return new CodexContents("Crumbling Codex", "Krümblegård", pages);
    }

    private static final class CodexContents {
        private final String title;
        private final String author;
        private final List<String> pages;

        private CodexContents(String title, String author, List<String> pages) {
            this.title = title;
            this.author = author;
            this.pages = pages;
        }
    }
}
