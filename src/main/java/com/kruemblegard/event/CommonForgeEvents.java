package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEvents {
    private CommonForgeEvents() {}

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
}
