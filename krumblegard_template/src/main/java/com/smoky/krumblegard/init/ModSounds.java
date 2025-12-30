package com.smoky.krumblegard.init;

import com.smoky.krumblegard.KrumblegardMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    private ModSounds() {}

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, KrumblegardMod.MODID);

    public static final RegistryObject<SoundEvent> KRUMBLEGARD_RISE = register("krumblegard.rise");
    public static final RegistryObject<SoundEvent> KRUMBLEGARD_CORE_HUM = register("krumblegard.core_hum");
    public static final RegistryObject<SoundEvent> KRUMBLEGARD_ATTACK_SMASH = register("krumblegard.attack_smash");
    public static final RegistryObject<SoundEvent> KRUMBLEGARD_ATTACK_SLAM = register("krumblegard.attack_slam");
    public static final RegistryObject<SoundEvent> KRUMBLEGARD_ATTACK_RUNE = register("krumblegard.attack_rune");
    public static final RegistryObject<SoundEvent> KRUMBLEGARD_RADIANT = register("krumblegard.radiant");

    // Boss theme (streamed). Played through MusicManager (not ambient looping).
    public static final RegistryObject<SoundEvent> KRUMBLEGARD_MUSIC = register("music.krumblegard");

    private static RegistryObject<SoundEvent> register(String path) {
        return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(KrumblegardMod.MODID, path)));
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
