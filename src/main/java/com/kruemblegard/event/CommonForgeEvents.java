package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.DriftwhaleEntity;
import com.kruemblegard.entity.GraveCairnEntity;
import com.kruemblegard.entity.WyrdwingEntity;
import com.kruemblegard.entity.goal.RunegrowthGrazingGoal;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModEnchantments;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.registry.ModTags;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEvents {
    private CommonForgeEvents() {}

    private static final String NBT_AVOID_WYRDWING = "kruemblegard:avoid_wyrdwing";
    private static final String NBT_SMASH_SCARALON_EGGS = "kruemblegard:smash_scaralon_eggs";
    private static final String NBT_AVOID_DRIFTWHALE = "kruemblegard:avoid_driftwhale";
    private static final String NBT_SHEEP_RUNEGROWTH_GRAZING = "kruemblegard:sheep_runegrowth_grazing";
    private static final String TELEKINESIS_TOOLTIP_MARKER = "tooltip.kruemblegard.telekinesis.routes_drops";

    @SubscribeEvent
    public static void onHoeWayfallSoil(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof HoeItem)) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModTags.Blocks.RUBBLE_TILLABLE)) {
            return;
        }

        if (!level.getBlockState(pos.above()).isAir()) {
            return;
        }

        level.setBlock(pos, ModBlocks.RUBBLE_TILTH.get().defaultBlockState(), 11);
        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
        stack.hurtAndBreak(1, event.getEntity(), p -> p.broadcastBreakEvent(event.getHand()));

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        Player player = event.getPlayer();
        if (!(player.level() instanceof ServerLevel level)) return;

        // "Reacts to player speech": if the player chats near Echocaps, make them pulse.
        BlockPos origin = player.blockPosition();
        int radius = 8;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            if (!level.getBlockState(p).is(ModBlocks.ECHOCAP.get())) continue;

            level.sendParticles(
                    ParticleTypes.NOTE,
                    p.getX() + 0.5,
                    p.getY() + 0.7,
                    p.getZ() + 0.5,
                    8,
                    0.25,
                    0.25,
                    0.25,
                    0.02
            );
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!hasTelekinesisTooltip(event.getItemStack())) {
            return;
        }

        List<Component> tooltip = event.getToolTip();
        Component firstLine = Component.translatable(TELEKINESIS_TOOLTIP_MARKER).withStyle(ChatFormatting.AQUA);
        if (tooltip.stream().anyMatch(firstLine::equals)) {
            return;
        }

        tooltip.add(firstLine);
        tooltip.add(Component.translatable("tooltip.kruemblegard.telekinesis.inventory_priority").withStyle(ChatFormatting.DARK_AQUA));
    }

    @SubscribeEvent
    public static void onSilverfishAndEndermiteDropsBugMeat(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!(event.getEntity() instanceof Silverfish) && !(event.getEntity() instanceof Endermite)) {
            return;
        }

        int looting = Math.max(0, event.getLootingLevel());
        int count = 1;
        if (looting > 0 && event.getEntity().getRandom().nextFloat() < (0.25F * looting)) {
            count++;
        }

        ItemStack stack = new ItemStack(ModItems.BUG_MEAT.get(), count);
        ItemEntity drop = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack);
        event.getDrops().add(drop);
    }

    @SubscribeEvent
    public static void onMakeBugsFearWyrdwing(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (!(mob instanceof Silverfish) && !(mob instanceof Endermite) && !(mob instanceof Spider) && !(mob instanceof CaveSpider)) {
            return;
        }

        if (mob.getPersistentData().getBoolean(NBT_AVOID_WYRDWING)) {
            return;
        }

        if (!(mob instanceof PathfinderMob pathfinder)) {
            return;
        }

        pathfinder.goalSelector.addGoal(1, new AvoidEntityGoal<>(pathfinder, WyrdwingEntity.class, 10.0F, 1.1D, 1.35D));
        mob.getPersistentData().putBoolean(NBT_AVOID_WYRDWING, true);
    }

    @SubscribeEvent
    public static void onMakeGlowSquidsFearDriftwhales(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof GlowSquid squid)) {
            return;
        }

        if (squid.getPersistentData().getBoolean(NBT_AVOID_DRIFTWHALE)) {
            return;
        }

        squid.goalSelector.addGoal(1, new AvoidEntityGoal<>(squid, DriftwhaleEntity.class, 12.0F, 1.05D, 1.25D));
        squid.getPersistentData().putBoolean(NBT_AVOID_DRIFTWHALE, true);
    }

    @SubscribeEvent
    public static void onGiveSheepRunegrowthGrazing(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof Sheep sheep)) {
            return;
        }

        if (sheep.getPersistentData().getBoolean(NBT_SHEEP_RUNEGROWTH_GRAZING)) {
            return;
        }

        sheep.goalSelector.addGoal(5, new RunegrowthGrazingGoal(sheep, mob -> true, false, 100));
        sheep.getPersistentData().putBoolean(NBT_SHEEP_RUNEGROWTH_GRAZING, true);
    }

    /**
     * Vanilla zombie-family mobs will path to and smash turtle eggs.
     * This extends that behavior to {@link ModBlocks#SCARALON_EGG}.
     */
    @SubscribeEvent
    public static void onMakeZombiesSmashScaralonEggs(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof Zombie zombie)) {
            return;
        }

        if (zombie.getPersistentData().getBoolean(NBT_SMASH_SCARALON_EGGS)) {
            return;
        }

        zombie.goalSelector.addGoal(4, new RemoveBlockGoal(ModBlocks.SCARALON_EGG.get(), zombie, 1.0D, 3));
        zombie.getPersistentData().putBoolean(NBT_SMASH_SCARALON_EGGS, true);
    }

    @SubscribeEvent
    public static void onWakeDormantGraveCairnsOnBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null || player.isSpectator()) {
            return;
        }

        BlockPos pos = event.getPos();
        int r = 8;
        AABB box = new AABB(pos).inflate(r);

        for (GraveCairnEntity cairn : level.getEntitiesOfClass(GraveCairnEntity.class, box)) {
            if (!cairn.isAlive()) {
                continue;
            }

            if (cairn.isDormant()) {
                cairn.wakeFromDisturbance(player);
            }
        }
    }

    private static boolean hasTelekinesisTooltip(ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TELEKINESIS.get(), stack) > 0) {
            return true;
        }

        return EnchantedBookItem.getEnchantments(stack).stream()
        .anyMatch(tag -> tag instanceof CompoundTag compoundTag
            && ModEnchantments.TELEKINESIS.getId().toString().equals(compoundTag.getString("id")));
    }
}
