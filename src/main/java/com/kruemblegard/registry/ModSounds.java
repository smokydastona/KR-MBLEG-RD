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

    // -----------------------------
    // ENTITY SFX
    // -----------------------------
    public static final RegistryObject<SoundEvent> TRAPROCK_SLEEP_AMBIENT = register("entity.traprock.sleep_ambient");
    public static final RegistryObject<SoundEvent> TRAPROCK_AWAKEN = register("entity.traprock.awaken");
    public static final RegistryObject<SoundEvent> TRAPROCK_AMBIENT = register("entity.traprock.ambient");
    public static final RegistryObject<SoundEvent> TRAPROCK_HURT = register("entity.traprock.hurt");
    public static final RegistryObject<SoundEvent> TRAPROCK_DEATH = register("entity.traprock.death");
    public static final RegistryObject<SoundEvent> TRAPROCK_ATTACK = register("entity.traprock.attack");

    public static final RegistryObject<SoundEvent> PEBBLIT_AMBIENT = register("entity.pebblit.ambient");
    public static final RegistryObject<SoundEvent> PEBBLIT_HURT = register("entity.pebblit.hurt");
    public static final RegistryObject<SoundEvent> PEBBLIT_DEATH = register("entity.pebblit.death");
    public static final RegistryObject<SoundEvent> PEBBLIT_STEP = register("entity.pebblit.step");
    public static final RegistryObject<SoundEvent> PEBBLIT_TAME = register("entity.pebblit.tame");
    public static final RegistryObject<SoundEvent> PEBBLIT_PERCH = register("entity.pebblit.perch");

    public static final RegistryObject<SoundEvent> GREAT_HUNGER_AMBIENT = register("entity.great_hunger.ambient");
    public static final RegistryObject<SoundEvent> GREAT_HUNGER_HURT = register("entity.great_hunger.hurt");
    public static final RegistryObject<SoundEvent> GREAT_HUNGER_DEATH = register("entity.great_hunger.death");
    public static final RegistryObject<SoundEvent> GREAT_HUNGER_STEP = register("entity.great_hunger.step");
    public static final RegistryObject<SoundEvent> GREAT_HUNGER_BITE = register("entity.great_hunger.bite");

    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_AMBIENT = register("entity.fault_crawler.ambient");
    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_HURT = register("entity.fault_crawler.hurt");
    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_DEATH = register("entity.fault_crawler.death");
    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_STEP = register("entity.fault_crawler.step");
    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_PULSE = register("entity.fault_crawler.pulse");
    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_SLAM = register("entity.fault_crawler.slam");
    public static final RegistryObject<SoundEvent> FAULT_CRAWLER_EMERGE = register("entity.fault_crawler.emerge");

    public static final RegistryObject<SoundEvent> SCARALON_BEETLE_AMBIENT = register("entity.scaralon_beetle.ambient");
    public static final RegistryObject<SoundEvent> SCARALON_BEETLE_HURT = register("entity.scaralon_beetle.hurt");
    public static final RegistryObject<SoundEvent> SCARALON_BEETLE_DEATH = register("entity.scaralon_beetle.death");
    public static final RegistryObject<SoundEvent> SCARALON_BEETLE_STEP = register("entity.scaralon_beetle.step");
    public static final RegistryObject<SoundEvent> SCARALON_BEETLE_ATTACK = register("entity.scaralon_beetle.attack");

    public static final RegistryObject<SoundEvent> WYRDWING_AMBIENT = register("entity.wyrdwing.ambient");
    public static final RegistryObject<SoundEvent> WYRDWING_HURT = register("entity.wyrdwing.hurt");
    public static final RegistryObject<SoundEvent> WYRDWING_DEATH = register("entity.wyrdwing.death");
    public static final RegistryObject<SoundEvent> WYRDWING_STEP = register("entity.wyrdwing.step");
    public static final RegistryObject<SoundEvent> WYRDWING_ATTACK = register("entity.wyrdwing.attack");
    public static final RegistryObject<SoundEvent> WYRDWING_CALL = register("entity.wyrdwing.call");

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_HURT = register("entity.kruemblegard.hurt");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_ROAR = register("entity.kruemblegard.roar");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_CAST = register("entity.kruemblegard.cast");
    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_STEP = register("entity.kruemblegard.step");

    public static final RegistryObject<SoundEvent> KRUEMBLEGARD_MUSIC = register("music.kruemblegard");

    public static final RegistryObject<SoundEvent> WAYFALL_MUSIC = register("music.wayfall");

    private static RegistryObject<SoundEvent> register(String path) {
        return SOUNDS.register(path,
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kruemblegard.MOD_ID, path)));
    }
}
