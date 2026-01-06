package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Kruemblegard.MOD_ID);

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_AMBIENT =
        SOUNDS.register("kruemblegard_ambient",
            () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_ambient")));

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_DEATH =
        SOUNDS.register("kruemblegard_death",
            () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_death")));

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_ATTACK =
        SOUNDS.register("kruemblegard_attack",
            () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_attack")));

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_DASH =
        SOUNDS.register("kruemblegard_dash",
            () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_dash")));

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_STORM =
        SOUNDS.register("kruemblegard_storm",
            () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_storm")));

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_RISE = register("kruemblegard.rise");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_CORE_HUM = register("kruemblegard.core_hum");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_ATTACK_SMASH = register("kruemblegard.attack_smash");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_ATTACK_SLAM = register("kruemblegard.attack_slam");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_ATTACK_RUNE = register("kruemblegard.attack_rune");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_RADIANT = register("kruemblegard.radiant");

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_MUSIC = register("music.kruemblegard");

    public static final RegistryObject<SoundEvent> WAYFALL_MUSIC = register("music.wayfall");

    private static RegistryObject<SoundEvent> register(String path) {
        return SOUNDS.register(path,
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kruemblegard.MOD_ID, path)));
    }
}
