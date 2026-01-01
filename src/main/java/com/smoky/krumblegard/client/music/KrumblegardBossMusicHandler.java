package com.smoky.krumblegard.client.music;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.entity.boss.KrumblegardEntity;
import com.smoky.krumblegard.init.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KrumblegardMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class KrumblegardBossMusicHandler {
    private KrumblegardBossMusicHandler() {}

    private static final double MUSIC_RADIUS = 128.0;
    private static boolean startedByUs;
    private static Music cachedMusic;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            stopIfOurs(mc);
            return;
        }

        boolean shouldPlay = false;
        AABB scan = mc.player.getBoundingBox().inflate(MUSIC_RADIUS);
        for (KrumblegardEntity boss : mc.level.getEntitiesOfClass(KrumblegardEntity.class, scan)) {
            if (!boss.isAlive()) continue;
            if (!boss.isEngaged()) continue;

            if (mc.player.distanceToSqr(boss) <= MUSIC_RADIUS * MUSIC_RADIUS) {
                shouldPlay = true;
                break;
            }
        }

        if (shouldPlay) {
            ensureStarted(mc);
        } else {
            stopIfOurs(mc);
        }
    }

    private static void ensureStarted(Minecraft mc) {
        if (startedByUs) return;

        if (cachedMusic == null) {
            cachedMusic = new Music(
                    ModSounds.KRUMBLEGARD_MUSIC.getHolder().orElseThrow(),
                    0,
                    0,
                    true
            );
        }

        mc.getMusicManager().startPlaying(cachedMusic);
        startedByUs = true;
    }

    private static void stopIfOurs(Minecraft mc) {
        if (!startedByUs) return;
        mc.getMusicManager().stopPlaying();
        startedByUs = false;
    }
}
