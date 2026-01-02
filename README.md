# Krümblegård (Forge 1.20.1 + GeckoLib)

This project contains a Forge 1.20.1 + GeckoLib mod.

## What this mod already does
- **Haunted Waystone** (looks normal) triggers the fight on right-click.
- A hidden **Arena Anchor** blockentity is placed under it to persist the event.
- Arena **rises from the ground in layers**, then standing stones appear.
- **Krümblegård spawns underground and emerges** (no teleport).
- **Arena anti-cheese**: leaving the circle applies Slowness + Mining Fatigue and heals the boss.
- On boss death, the arena **cleanses** and the **Ancient Waystone** forms (radiant particles).
- **Advancements** (observed / survived / cleansed) are granted by code.
- **Loot table** for unique drops.

This mod uses **custom advancement triggers** registered in `ModCriteria`:
- `krumblegard:haunted_waystone_clicked`
- `krumblegard:krumblegard_survived`
- `krumblegard:krumblegard_cleansed`

## Dependencies (ForgeGradle)
You need GeckoLib 4.x for Forge 1.20.1.

```gradle
repositories {
    maven { url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/' }
}

dependencies {
    implementation fg.deobf('software.bernie.geckolib:geckolib-forge-1.20.1:4.+')
}
```

## Registration checklist (if you rename things)
- Mod id used here: `krumblegard`
- Base package used here: `com.kruemblegard`

If your mod id/package differ, update:
- `com.kruemblegard.Kruemblegard` (MODID constant)
- `META-INF/mods.toml`
- resource folder names: `assets/krumblegard`, `data/krumblegard`

## How to test quickly (dev)
1. Start a dev world.
2. Give yourself a haunted waystone:
   - `/give @p krumblegard:haunted_waystone`
3. Place it and right-click it.
4. The arena should build and Krümblegård should emerge.

### Worldgen false waystones
- Block: `krumblegard:false_waystone` (not intended to be obtained)
- Biome modifier: `data/forge/biome_modifier/add_false_waystone.json`
- Biome tag: `data/krumblegard/tags/worldgen/biome/has_false_waystone.json`

Quick test (manual place):
- `/setblock ~ ~ ~ krumblegard:false_waystone`

## Assets you still need to provide
This mod references textures/sounds but does not include binary assets.

### Textures
Add PNGs under:
- `assets/krumblegard/textures/block/*.png`
- `assets/krumblegard/textures/item/*.png`
- `assets/krumblegard/textures/entity/krumblegard.png`

### Sounds
Add OGGs under:
- `assets/krumblegard/sounds/`

The `assets/krumblegard/sounds.json` defines these keys:
- `krumblegard.rise`
- `krumblegard.core_hum`
- `krumblegard.attack_smash`
- `krumblegard.attack_slam`
- `krumblegard.attack_rune`
- `krumblegard.radiant`

## Notes / next upgrades (optional)
- Replace the rune barrage placeholder damage with real projectiles.
- Add boss bar + music.
- Convert the arena boundary from “distance check” to a strict circle and include vertical bounds.
- Add phase 2/3 arena behaviors (standing stones rising/rotating).

### Boss music (true music layer)
- Sound event key: `music.krumblegard`
- Expected asset: `assets/krumblegard/sounds/krumblegard_theme.ogg`

### Config-driven waystone rarity
Common config file: `config/krumblegard-common.toml`

```toml
[Krumblegard]
    enableWaystones = true
    waystoneRarity = 800

    [Krumblegard.Boss]
        bossMaxHealth = 1200.0
        bossArmor = 18.0
        bossArmorToughness = 10.0
        bossAttackDamage = 20.0
        bossAttackKnockback = 1.5
        bossRegenPerTick = 0.0
        bossPhase2HealthRatio = 0.7
        bossPhase3HealthRatio = 0.3
        bossPhase2RuneBoltCooldownTicks = 40
        bossPhase2GraviticPullCooldownTicks = 60
        bossPhase3DashCooldownTicks = 80
        bossPhase3MeteorArmCooldownTicks = 100
        bossPhase3ArcaneStormCooldownTicks = 120
```
