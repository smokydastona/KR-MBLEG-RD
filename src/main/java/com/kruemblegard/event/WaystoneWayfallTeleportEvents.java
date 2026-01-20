package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.ModConfig;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.world.WayfallTravel;

import net.blay09.mods.waystones.block.WaystoneBlock;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WaystoneWayfallTeleportEvents {
    private WaystoneWayfallTeleportEvents() {}

    @SubscribeEvent
    public static void onRightClickWaystone(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND) {
            return;
        }

        // Hold shift to use Waystones normally without rolling the "lore teleport".
        if (player.isShiftKeyDown()) {
            return;
        }

        if (!ModConfig.WAYSTONE_WAYFALL_TELEPORT_ENABLED.get()) {
            return;
        }

        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof WaystoneBlock)) {
            return;
        }

        KruemblegardPlayerData data = KruemblegardPlayerData.read(player.getPersistentData());
        if (data.visitedWayfall()) {
            return;
        }

        double chance = ModConfig.WAYSTONE_WAYFALL_TELEPORT_CHANCE.get();
        if (chance <= 0.0D) {
            return;
        }

        if (level.random.nextDouble() >= chance) {
            return;
        }

        boolean teleported = WayfallTravel.teleportToWayfallSpawnLanding(player, level);
        if (!teleported) {
            return;
        }

        // Prevent the Waystones UI from opening in the source dimension.
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
