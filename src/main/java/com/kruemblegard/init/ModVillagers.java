package com.kruemblegard.init;

import com.google.common.collect.ImmutableSet;
import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModEnchantments;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.registry.ModTags;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModVillagers {
        private static final float PRICE_MULTIPLIER = 0.05F;

    private ModVillagers() {
    }

    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, Kruemblegard.MOD_ID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, Kruemblegard.MOD_ID);

    public static final RegistryObject<PoiType> BRINE_GARDEN_BASIN_POI = POI_TYPES.register(
            "brine_garden_basin",
            () -> new PoiType(allStates(ModBlocks.BRINE_GARDEN_BASIN.get()), 1, 1)
    );

    public static final RegistryObject<PoiType> TENDRIL_FORGE_POI = POI_TYPES.register(
            "tendril_forge",
            () -> new PoiType(allStates(ModBlocks.TENDRIL_FORGE.get()), 1, 1)
    );

    public static final RegistryObject<VillagerProfession> NUTRIENT_KEEPER = PROFESSIONS.register(
            "nutrient_keeper",
            () -> new VillagerProfession(
                    "nutrient_keeper",
                    holder -> holder.value() == BRINE_GARDEN_BASIN_POI.get(),
                    holder -> holder.value() == BRINE_GARDEN_BASIN_POI.get(),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.COMPOSTER_READY
            )
    );

    public static final RegistryObject<VillagerProfession> ARCHITECT = PROFESSIONS.register(
            "architect",
            () -> new VillagerProfession(
                    "architect",
                    holder -> holder.value() == TENDRIL_FORGE_POI.get(),
                    holder -> holder.value() == TENDRIL_FORGE_POI.get(),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE
            )
    );

    public static void register(IEventBus bus) {
        POI_TYPES.register(bus);
        PROFESSIONS.register(bus);
    }

    private static Set<BlockState> allStates(net.minecraft.world.level.block.Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    @Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {
        private ForgeEvents() {
        }

        @SubscribeEvent
        public static void onVillagerTrades(VillagerTradesEvent event) {
                        if (event.getType() == VillagerProfession.LIBRARIAN) {
                                addLibrarianTrades(event);
                        }

                        if (event.getType() == VillagerProfession.FLETCHER) {
                                addFletcherTrades(event);
                        }

                        if (event.getType() == VillagerProfession.TOOLSMITH) {
                                addToolsmithTrades(event);
                        }

                        if (event.getType() == VillagerProfession.WEAPONSMITH) {
                                addWeaponsmithTrades(event);
                        }

                        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
                                addCartographerTrades(event);
                        }

            if (event.getType() == NUTRIENT_KEEPER.get()) {
                                addNutrientKeeperTrades(event);
            }

            if (event.getType() == ARCHITECT.get()) {
                                addArchitectTrades(event);
            }
        }
    }

                                private static void addLibrarianTrades(VillagerTradesEvent event) {
                                                                                                event.getTrades().get(5).add(telekinesisBookTrade(24, 4, 30));
                                }

                private static void addCartographerTrades(VillagerTradesEvent event) {
                                                event.getTrades().get(5).add(ancientWayRuinsMapTrade(14, 30));
                }

                private static void addFletcherTrades(VillagerTradesEvent event) {
                                                event.getTrades().get(5).add(telekineticGearTrade(16, 22, 4, 30, 0.3F,
                                                                                Items.BOW,
                                                                                Items.CROSSBOW
                                                ));
                }

                private static void addToolsmithTrades(VillagerTradesEvent event) {
                                                event.getTrades().get(5).add(telekineticGearTrade(15, 21, 4, 30, 0.32F,
                                                                                Items.DIAMOND_PICKAXE,
                                                                                Items.DIAMOND_AXE,
                                                                                Items.DIAMOND_SHOVEL,
                                                                                Items.DIAMOND_HOE
                                                ));
                }

                private static void addWeaponsmithTrades(VillagerTradesEvent event) {
                                                event.getTrades().get(5).add(telekineticGearTrade(16, 23, 4, 30, 0.32F,
                                                                                Items.DIAMOND_SWORD,
                                                                                Items.DIAMOND_AXE
                                                ));
                }

        private static void addNutrientKeeperTrades(VillagerTradesEvent event) {
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.SOULBERRIES.get(), 16, 12, 2));
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.GHOULBERRIES.get(), 16, 12, 2));
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.REMNANT_SEEDS.get(), 24, 12, 2));
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.PALEWEFT_SEEDS.get(), 24, 12, 2));
                        event.getTrades().get(1).add(sellForEmeralds(1, ModItems.VOLATILE_PULP.get(), 3, 10, 1));

                        event.getTrades().get(2).add(buyForOneEmerald(ModItems.RUNEDRIFT_REED_ITEM.get(), 12, 10, 5));
                        event.getTrades().get(2).add(buyForOneEmerald(ModItems.GRAVEVINE_ITEM.get(), 10, 10, 5));
                        event.getTrades().get(2).add(sellForEmeralds(1, ModItems.PYROKELP_ITEM.get(), 4, 10, 5));
                        event.getTrades().get(2).add(sellForEmeralds(1, ModItems.WISPSTALK_ITEM.get(), 3, 10, 5));
                        event.getTrades().get(2).add(sellForEmeralds(2, ModItems.BIO_RESIN.get(), 2, 8, 5));

                        event.getTrades().get(3).add(buyForOneEmerald(ModItems.VOLATILE_RESIN.get(), 6, 8, 10));
                        event.getTrades().get(3).add(buyForOneEmerald(ModItems.RUNE_PETALS.get(), 10, 8, 10));
                        event.getTrades().get(3).add(sellForEmeralds(2, ModItems.REMNANT_SEEDS.get(), 5, 8, 10));
                        event.getTrades().get(3).add(sellForEmeralds(2, ModItems.PALEWEFT_SEEDS.get(), 5, 8, 10));
                        event.getTrades().get(3).add(sellForEmeralds(3, ModItems.MOISTURE_STONE.get(), 1, 6, 10));
                        event.getTrades().get(3).add(sellForEmeralds(3, ModItems.RUNEBLOOM_ITEM.get(), 1, 6, 10));

                        event.getTrades().get(4).add(buyForEmeralds(ModItems.RUNEBLOOM_ITEM.get(), 12, 2, 6, 15));
                        event.getTrades().get(4).add(sellForEmeralds(3, ModItems.RUNE_PETALS.get(), 2, 6, 15));
                        event.getTrades().get(4).add(sellForEmeralds(4, ModItems.SOULBERRY_SHRUB_ITEM.get(), 1, 5, 15));
                        event.getTrades().get(4).add(sellForEmeralds(4, ModItems.GHOULBERRY_SHRUB_ITEM.get(), 1, 5, 15));
                        event.getTrades().get(4).add(sellForEmeralds(4, ModItems.BIO_RESIN.get(), 4, 5, 15));

                        event.getTrades().get(5).add(sellForEmeralds(5, ModItems.MOISTURE_STONE.get(), 2, 3, 30));
                        event.getTrades().get(5).add(sellForEmeraldsAndItem(5, ModItems.BIO_RESIN.get(), 1, ModItems.RUNEBLOOM_ITEM.get(), 3, 2, 30));
                        event.getTrades().get(5).add(sellForEmeraldsAndItem(6, ModItems.MOISTURE_STONE.get(), 1, ModItems.SOULBERRY_SHRUB_ITEM.get(), 2, 2, 30));
                        event.getTrades().get(5).add(sellForEmeraldsAndItem(8, ModItems.BIO_RESIN.get(), 1, ModBlocks.BRINE_GARDEN_BASIN.get(), 1, 1, 30));
        }

        private static void addArchitectTrades(VillagerTradesEvent event) {
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.RUNIC_SCRAP.get(), 10, 12, 2));
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.RUNIC_DEBRIS_ITEM.get(), 8, 12, 2));
                        event.getTrades().get(1).add(buyForOneEmerald(ModItems.ATTUNED_RUNE_SHARD.get(), 7, 10, 2));
                        event.getTrades().get(1).add(sellForEmeralds(1, ModBlocks.ATTUNED_STONE.get(), 2, 10, 1));
                        event.getTrades().get(1).add(sellForEmeralds(1, ModBlocks.SCARSTONE.get(), 4, 10, 1));

                        event.getTrades().get(2).add(buyForOneEmerald(ModItems.VOLATILE_RESIN.get(), 6, 10, 5));
                        event.getTrades().get(2).add(buyForOneEmerald(ModItems.RUNE_PETALS.get(), 12, 10, 5));
                        event.getTrades().get(2).add(sellForEmeralds(2, ModItems.TENDRIL_STRANDS.get(), 2, 8, 5));
                        event.getTrades().get(2).add(sellForEmeralds(2, ModBlocks.RUNED_STONEVEIL_RUBBLE.get(), 4, 8, 5));
                        event.getTrades().get(2).add(sellForEmeralds(3, ModBlocks.ATTUNED_STONE.get(), 5, 8, 5));

                        event.getTrades().get(3).add(buyForEmeralds(ModItems.ATTUNED_INGOT.get(), 1, 2, 6, 10));
                        event.getTrades().get(3).add(buyForEmeralds(ModItems.RUNIC_INGOT.get(), 1, 2, 6, 10));
                        event.getTrades().get(3).add(sellForEmeralds(2, ModItems.RESONANCE_SHARD.get(), 1, 8, 10));
                        event.getTrades().get(3).add(sellForEmeralds(3, ModBlocks.WAYPOINT_MOLD.get(), 1, 6, 10));
                        event.getTrades().get(3).add(sellForEmeralds(4, ModBlocks.STANDING_STONE.get(), 1, 6, 10));

                        event.getTrades().get(4).add(buyForEmeralds(ModItems.RUNE_ETCHED_CHITIN_PLATE.get(), 1, 2, 5, 15));
                        event.getTrades().get(4).add(sellForEmeralds(4, ModItems.TENDRIL_STRANDS.get(), 5, 5, 15));
                        event.getTrades().get(4).add(sellForEmeralds(5, ModItems.RESONANCE_SHARD.get(), 2, 5, 15));
                        event.getTrades().get(4).add(sellForEmeralds(5, ModBlocks.WAYPOINT_MOLD.get(), 2, 4, 15));
                        event.getTrades().get(4).add(sellForEmeralds(6, ModBlocks.STANDING_STONE.get(), 1, 4, 15));

                        event.getTrades().get(5).add(sellForEmeraldsAndItem(6, ModItems.ATTUNED_RUNE_SHARD.get(), 1, ModBlocks.WAYPOINT_MOLD.get(), 3, 2, 30));
                        event.getTrades().get(5).add(sellForEmeraldsAndItem(8, ModItems.RESONANCE_SHARD.get(), 1, ModBlocks.TENDRIL_FORGE.get(), 1, 1, 30));
                        event.getTrades().get(5).add(sellForEmeraldsAndItem(9, ModItems.TENDRIL_STRANDS.get(), 1, ModBlocks.STANDING_STONE.get(), 2, 1, 30));
                        event.getTrades().get(5).add(sellForEmeralds(7, ModItems.RESONANCE_SHARD.get(), 2, 2, 30));
        }

        private static VillagerTrades.ItemListing buyForOneEmerald(ItemLike item, int count, int maxUses, int villagerXp) {
                return buyForEmeralds(item, count, 1, maxUses, villagerXp);
        }

        private static VillagerTrades.ItemListing buyForEmeralds(ItemLike item, int count, int emeralds, int maxUses, int villagerXp) {
                return (trader, random) -> new MerchantOffer(
                                new ItemStack(item, count),
                                new ItemStack(Items.EMERALD, emeralds),
                                maxUses,
                                villagerXp,
                                PRICE_MULTIPLIER
                );
        }

        private static VillagerTrades.ItemListing sellForEmeralds(int emeralds, ItemLike item, int count, int maxUses, int villagerXp) {
                return (trader, random) -> new MerchantOffer(
                                new ItemStack(Items.EMERALD, emeralds),
                                new ItemStack(item, count),
                                maxUses,
                                villagerXp,
                                PRICE_MULTIPLIER
                );
        }

        private static VillagerTrades.ItemListing sellForEmeraldsAndItem(int emeralds, ItemLike catalyst, int catalystCount, ItemLike item, int count, int maxUses, int villagerXp) {
                return (trader, random) -> new MerchantOffer(
                                new ItemStack(Items.EMERALD, emeralds),
                                new ItemStack(catalyst, catalystCount),
                                new ItemStack(item, count),
                                maxUses,
                                villagerXp,
                                PRICE_MULTIPLIER
                );
        }

        private static VillagerTrades.ItemListing telekinesisBookTrade(int emeralds, int maxUses, int villagerXp) {
                return (trader, random) -> new MerchantOffer(
                                new ItemStack(Items.EMERALD, emeralds),
                                new ItemStack(Items.BOOK),
                                EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ModEnchantments.TELEKINESIS.get(), 1)),
                                maxUses,
                                villagerXp,
                                PRICE_MULTIPLIER
                );
        }

        private static VillagerTrades.ItemListing telekineticGearTrade(int plainEmeralds, int enchantedEmeralds, int maxUses, int villagerXp, float enchantedChance, Item... items) {
                return (trader, random) -> {
                                if (items.length == 0) {
                                                return null;
                                }

                                Item item = items[random.nextInt(items.length)];
                                ItemStack stack = new ItemStack(item);
                                int emeraldCost = plainEmeralds;
                                if (random.nextFloat() < enchantedChance) {
                                                stack.enchant(ModEnchantments.TELEKINESIS.get(), 1);
                                                emeraldCost = enchantedEmeralds;
                                }

                                return new MerchantOffer(
                                                new ItemStack(Items.EMERALD, emeraldCost),
                                                stack,
                                                maxUses,
                                                villagerXp,
                                                PRICE_MULTIPLIER
                                );
                };
        }

        private static VillagerTrades.ItemListing ancientWayRuinsMapTrade(int emeralds, int villagerXp) {
                return (trader, random) -> {
                                if (!(trader instanceof Villager villager)) {
                                                return null;
                                }
                                if (!(villager.level() instanceof ServerLevel serverLevel)) {
                                                return null;
                                }

                                BlockPos target = serverLevel.findNearestMapStructure(
                                                ModTags.Structures.ANCIENT_WAY_RUINS_MAPS,
                                                villager.blockPosition(),
                                                2048,
                                                true
                                );
                                if (target == null) {
                                                return null;
                                }

                                ItemStack map = MapItem.create(serverLevel, target.getX(), target.getZ(), (byte) 2, true, true);
                                MapItem.renderBiomePreviewMap(serverLevel, map);
                                MapItemSavedData.addTargetDecoration(map, target, "+", MapDecoration.Type.RED_X);
                                map.setHoverName(Component.translatable("filled_map.kruemblegard.ancient_way_ruins"));

                                return new MerchantOffer(
                                                new ItemStack(Items.EMERALD, emeralds),
                                                new ItemStack(Items.COMPASS),
                                                map,
                                                12,
                                                villagerXp,
                                                PRICE_MULTIPLIER
                                );
                };
        }
}