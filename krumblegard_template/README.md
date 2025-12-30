# Krümblegård (Forge 1.20.1 + GeckoLib) — Drop-in Template

This folder is a **module template** you can copy into a Forge MDK workspace.

## What this template already does
- **Haunted Waystone** (looks normal) triggers the fight on right-click.
- A hidden **Arena Anchor** blockentity is placed under it to persist the event.
- Arena **rises from the ground in layers**, then standing stones appear.
- **Krümblegård spawns underground and emerges** (no teleport).
- **Arena anti-cheese**: leaving the circle applies Slowness + Mining Fatigue and heals the boss.
- On boss death, the arena **cleanses** and the **Ancient Waystone** forms (radiant particles).
- **Advancements** (observed / survived / cleansed) are granted by code.
- **Loot table** for unique drops.

This template uses **custom advancement triggers** registered in `ModCriteria`:
- `krumblegard:haunted_waystone_clicked`
- `krumblegard:krumblegard_survived`
- `krumblegard:krumblegard_cleansed`

## Files you copy into your Forge MDK
Copy these folders into the root of your Forge MDK project:
- `src/main/java`
- `src/main/resources`

## Dependencies you must add (ForgeGradle)
You need GeckoLib 4.x for Forge 1.20.1.

Add GeckoLib to your `build.gradle` dependencies (exact version may differ by release):

```gradle
repositories {
    maven { url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/' }
}

dependencies {
    implementation fg.deobf('software.bernie.geckolib:geckolib-forge-1.20.1:4.+')
}
```

If you already use another dependency setup, just make sure the `geckolib` mod loads and your dev run config includes it.

## Registration checklist (if you merge into an existing mod)
- Mod id used here: `krumblegard`
- Base package used here: `com.smoky.krumblegard`

If your mod id/package differ, update:
- `com.smoky.krumblegard.KrumblegardMod` (MODID constant)
- `META-INF/mods.toml`
- resource folder names: `assets/krumblegard`, `data/krumblegard`

## How to test quickly (dev)
1. Start a dev world.
2. Give yourself a haunted waystone:
   - `/give @p krumblegard:haunted_waystone`
3. Place it and right-click it.
4. The arena should build and Krümblegård should emerge.

### Worldgen false waystones
This template now also includes worldgen data for a **world-only** trap block:
- Block: `krumblegard:false_waystone` (no loot table; not intended to be obtained)
- Worldgen is **code-registered** and driven by config (see below)
- Biome modifier: `data/forge/biome_modifier/add_false_waystone.json`
- Biome tag: `data/krumblegard/tags/worldgen/biome/has_false_waystone.json`

If you want to test worldgen quickly in dev, you can place it manually:
- `/setblock ~ ~ ~ krumblegard:false_waystone`

Optional: craft a Radiant Sword
- Recipe file: `data/krumblegard/recipes/radiant_sword.json`

## Assets you still need to provide
This template references textures/sounds but does not include binary assets.

### Textures
Add PNGs under:
- `assets/krumblegard/textures/block/*.png`
- `assets/krumblegard/textures/item/*.png`
- `assets/krumblegard/textures/entity/krumblegard.png`

By default, the template reuses `assets/krumblegard/textures/block/standing_stone.png` for:
- `standing_stone`
- `haunted_waystone`
- `false_waystone`
- `ancient_waystone`

### Sounds
Add OGGs under:
- `assets/krumblegard/sounds/`

The `assets/krumblegard/sounds.json` already defines these keys:
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

### Lore drop
The boss loot table can drop `krumblegard:crumbling_codex` (a `WrittenBookItem`), which opens with the vanilla book UI.

### Boss music (true music layer)
- Sound event key: `music.krumblegard`
- Expected asset: `assets/krumblegard/sounds/krumblegard_theme.ogg`

Music is played through Minecraft's **MusicManager** (not a looping ambient sound), starts when the boss engages, and fades out when you leave the area or the boss dies.

### Config-driven waystone rarity
Common config file: `config/krumblegard-common.toml`

```toml
[Krumblegard]
    enableWaystones = true
    waystoneRarity = 800
```
