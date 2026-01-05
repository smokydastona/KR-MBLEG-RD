package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, value = Dist.CLIENT)
public final class WayfallSurfaceAmbience {

    private static int nextAmbientTickDelay = 0;

    private WayfallSurfaceAmbience() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;
        if (player == null || level == null) {
            return;
        }

        if (nextAmbientTickDelay > 0) {
            nextAmbientTickDelay--;
            return;
        }

        // Only when standing still (or very close): "silence is intentional".
        if (player.getDeltaMovement().horizontalDistanceSqr() > 0.0006) {
            nextAmbientTickDelay = 20;
            return;
        }

        BlockPos below = player.blockPosition().below();
        var state = level.getBlockState(below);

        float volume = 0.15F;
        float pitch = 0.95F + level.random.nextFloat() * 0.1F;

        if (state.is(ModBlocks.VEILGROWTH.get())) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, volume * 0.6F, pitch, false);
            nextAmbientTickDelay = 20 * (8 + level.random.nextInt(12));
        } else if (state.is(ModBlocks.ASHMOSS.get())) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.AMBIENT, volume * 0.6F, pitch, false);
            nextAmbientTickDelay = 20 * (10 + level.random.nextInt(14));
        } else if (state.is(ModBlocks.RUNEGROWTH.get())) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, volume * 0.4F, pitch, false);
            nextAmbientTickDelay = 20 * (12 + level.random.nextInt(18));
        } else if (state.is(ModBlocks.VOIDFELT.get())) {
            // Dampened feel: just less frequent + lower volume.
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.WOOL_PLACE, SoundSource.AMBIENT, volume * 0.25F, 0.75F + level.random.nextFloat() * 0.1F, false);
            nextAmbientTickDelay = 20 * (16 + level.random.nextInt(22));
        } else {
            nextAmbientTickDelay = 40;
        }
    }
}
