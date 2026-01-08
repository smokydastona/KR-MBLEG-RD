package com.kruemblegard.book;

import java.io.Reader;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class KruemblegardGuidebook {
    private KruemblegardGuidebook() {}

    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PAGES = "pages";
    private static final String TAG_RESOLVED = "resolved";

    private static final ResourceLocation CONTENTS_ID = new ResourceLocation(Kruemblegard.MOD_ID, "books/crumbling_codex.json");

    public static ItemStack createDefaultFilledBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        writeContents(book.getOrCreateTag(), defaultContents());
        return book;
    }

    public static ItemStack createServerFilledBook(MinecraftServer server) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CodexContents contents = loadContents(server).orElseGet(KruemblegardGuidebook::defaultContents);
        writeContents(book.getOrCreateTag(), contents);
        return book;
    }

    private static void writeContents(CompoundTag tag, CodexContents contents) {
        tag.putString(TAG_TITLE, contents.title());
        tag.putString(TAG_AUTHOR, contents.author());

        ListTag pages = new ListTag();
        for (String page : contents.pages()) {
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(page))));
        }
        tag.put(TAG_PAGES, pages);

        // Mark as resolved so clients don't try to resolve it later.
        tag.putBoolean(TAG_RESOLVED, true);
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
                "Welcome to Krümblegård.\n\nThis book is your field guide to the ruins.",
                "Traprock\n\nSome stone is alive.\n\n- Traprock can remain dormant until disturbed.\n- Linger too long, and it may awaken.",
                "Runic craft\n\nRunic Core is used to craft Runic tools.\n\nIf you face Krümblegård, it can drop a Runic Core."
        );

        return new CodexContents("Krümblegård Guide", "Krümblegård", pages);
    }

    private record CodexContents(String title, String author, List<String> pages) {
        private static final Codec<CodexContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("title", "Krümblegård Guide").forGetter(CodexContents::title),
                Codec.STRING.optionalFieldOf("author", "Krümblegård").forGetter(CodexContents::author),
                Codec.list(Codec.STRING).fieldOf("pages").forGetter(CodexContents::pages)
        ).apply(instance, CodexContents::new));
    }
}
