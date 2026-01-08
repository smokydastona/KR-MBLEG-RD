package com.kruemblegard.item;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

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
            return InteractionResultHolder.consume(stack);
        }

        // Creative-search / give commands may hand out an untagged book stack.
        // Also: vanilla's WrittenBookItem UI opening logic is item-ID specific (minecraft:written_book),
        // so custom WrittenBookItem subclasses won't automatically open. We open the screen ourselves.
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_PAGES, CompoundTag.TAG_LIST)) {
            markAutofilled(stack);
            fillWithDefaults(stack);
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openBookScreen(stack));
        return InteractionResultHolder.success(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientHooks {
        private ClientHooks() {}

        private static void openBookScreen(ItemStack stack) {
            try {
                var mc = net.minecraft.client.Minecraft.getInstance();

                // Try common constructor shape first: new BookViewScreen(ItemStack)
                Class<?> bookViewScreenClass = Class.forName("net.minecraft.client.gui.screens.inventory.BookViewScreen");
                for (Constructor<?> ctor : bookViewScreenClass.getConstructors()) {
                    Class<?>[] params = ctor.getParameterTypes();
                    if (params.length == 1 && params[0] == ItemStack.class) {
                        Object screen = ctor.newInstance(stack);
                        mc.setScreen((net.minecraft.client.gui.screens.Screen) screen);
                        return;
                    }
                }

                // Fallback: look for a static method that can produce a screen from an ItemStack.
                for (Method m : bookViewScreenClass.getDeclaredMethods()) {
                    if (!Modifier.isStatic(m.getModifiers())) {
                        continue;
                    }

                    Class<?>[] params = m.getParameterTypes();
                    if (params.length == 1 && params[0] == ItemStack.class) {
                        m.setAccessible(true);
                        Object result = m.invoke(null, stack);
                        if (result instanceof net.minecraft.client.gui.screens.Screen screen) {
                            mc.setScreen(screen);
                            return;
                        }
                    }

                    if (params.length == 2
                            && params[0].getName().equals("net.minecraft.client.Minecraft")
                            && params[1] == ItemStack.class) {
                        m.setAccessible(true);
                        Object result = m.invoke(null, mc, stack);
                        if (result instanceof net.minecraft.client.gui.screens.Screen screen) {
                            mc.setScreen(screen);
                            return;
                        }
                    }
                }

                // If we get here, we couldn't find a compatible API shape.
                Kruemblegard.LOGGER.warn("Unable to open Crumbling Codex book screen (BookViewScreen API mismatch)");
            } catch (Throwable t) {
                Kruemblegard.LOGGER.warn("Unable to open Crumbling Codex book screen", t);
            }
        }
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
        tag.putString(TAG_TITLE, contents.title());
        tag.putString(TAG_AUTHOR, contents.author());

        ListTag pages = new ListTag();
        for (String page : contents.pages()) {
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

                return CodexContents.CODEC
                        .parse(JsonOps.INSTANCE, obj)
                        .result()
                        .filter(c -> !c.pages().isEmpty());
            }
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private static CodexContents defaultContents() {
        List<String> pages = List.of(
            "Crumbling Codex\n\nA guide to the ruins of Krümblegård.",
            "Traprock\n\n" +
                "Some stone is alive.\n\n" +
                "- Traprock can remain dormant until disturbed.\n" +
                "- Linger too long, and it may awaken.",
            "Traprock\n\n" +
                "Traprock are small ambush predators.\n\n" +
                "- Expect them to strike quickly.\n" +
                "- Clear space so you can kite safely.",
            "Runic craft\n\n" +
                "Runic Core is used to craft Runic tools.\n\n" +
                "If you face Krümblegård, it can drop a Runic Core."
        );

        return new CodexContents("Crumbling Codex", "Krümblegård", pages);
    }

    private record CodexContents(String title, String author, List<String> pages) {
        private static final Codec<CodexContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("title", "Crumbling Codex").forGetter(CodexContents::title),
                Codec.STRING.optionalFieldOf("author", "Krümblegård").forGetter(CodexContents::author),
                Codec.list(Codec.STRING).fieldOf("pages").forGetter(CodexContents::pages)
        ).apply(instance, CodexContents::new));
    }
}
