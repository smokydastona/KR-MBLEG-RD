package com.kruemblegard.init;

import com.google.common.collect.ImmutableSet;
import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModItems;

import java.util.Set;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModVillagers {
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
            if (event.getType() == NUTRIENT_KEEPER.get()) {
                                event.getTrades().get(1).add((trader, random) -> new MerchantOffer(new ItemStack(ModItems.SOULBERRIES.get(), 12), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
                                event.getTrades().get(1).add((trader, random) -> new MerchantOffer(new ItemStack(ModItems.GHOULBERRIES.get(), 14), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
                                event.getTrades().get(2).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.BIO_RESIN.get(), 2), 12, 5, 0.05F));
                                event.getTrades().get(2).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(ModItems.MOISTURE_STONE.get()), 8, 5, 0.05F));
                                event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.REMNANT_SEEDS.get(), 3), 12, 10, 0.05F));
                                event.getTrades().get(4).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 7), new ItemStack(ModBlocks.BRINE_GARDEN_BASIN.get()), 4, 15, 0.05F));
            }

            if (event.getType() == ARCHITECT.get()) {
                                event.getTrades().get(1).add((trader, random) -> new MerchantOffer(new ItemStack(ModItems.RUNIC_SCRAP.get(), 8), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
                                event.getTrades().get(2).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.TENDRIL_STRANDS.get(), 2), 12, 5, 0.05F));
                                event.getTrades().get(2).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(ModItems.RESONANCE_SHARD.get()), 10, 5, 0.05F));
                                event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 3), new ItemStack(ModBlocks.ATTUNED_STONE.get(), 4), 10, 10, 0.05F));
                                event.getTrades().get(4).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 8), new ItemStack(ModBlocks.TENDRIL_FORGE.get()), 4, 15, 0.05F));
            }
        }
    }
}