# Krümblegård (Forge 1.20.1 + GeckoLib)

This project contains a Forge 1.20.1 + GeckoLib mod.

## What this mod already does
- **Haunted Waystone** (looks normal) triggers the fight on right-click.
- A hidden **Arena Anchor** blockentity is placed under it to persist the event.
- Arena **rises from the ground in layers**, then standing stones appear.
- **Krümblegård spawns underground and emerges** (no teleport).
- **Arena anti-cheese**: leaving the circle applies Slowness + Mining Fatigue and heals the boss.
- On boss death, the arena **cleanses** and the **Ancient Waystone** forms (radiant particles).
- **Crumbling Codex**: an in-game guidebook that opens with pre-written pages.
- **Advancements** (observed / survived / cleansed) are granted by code.
- **Loot table** for unique drops.

This mod uses **custom advancement triggers** registered in `ModCriteria`:
- `kruemblegard:haunted_waystone_clicked`
- `kruemblegard:kruemblegard_survived`
- `kruemblegard:kruemblegard_cleansed`

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
- Mod id used here: `kruemblegard`
- Base package used here: `com.kruemblegard`

If your mod id/package differ, update:
- `com.kruemblegard.Kruemblegard` (MODID constant)
- `META-INF/mods.toml`
- resource folder names: `assets/kruemblegard`, `data/kruemblegard`

## How to test quickly (dev)
1. Start a dev world.
2. Give yourself a haunted waystone:
    - `/give @p kruemblegard:haunted_waystone`
3. Place it and right-click it.
4. The arena should build and Krümblegård should emerge.

Quick test (manual place):
    - `/setblock ~ ~ ~ kruemblegard:haunted_waystone`

## Assets you still need to provide
This mod references textures/sounds; some binary assets may still be missing depending on what you’re testing.

### Textures
Add PNGs under:

#### Boss bar atlas layout
The boss bar uses a single atlas texture: `assets/kruemblegard/textures/gui/kruemblegard_bossbar.png`.

- Texture size: **256×32**
- Bar size: **182×5** (same as vanilla)
- Atlas layout:
    - Background: `u=0..181`, `v=0..4`
    - Fill: `u=0..181`, `v=5..9`
    - Overlay/frame: `u=0..181`, `v=10..14`
    - Hit-flash fill: `u=0..181`, `v=15..19`
    - Icon (16×16): `u=182..197`, `v=0..15`

You can repaint/replace the PNG anytime as long as those regions stay in the same positions.

### Sounds
Add OGGs under:
- `assets/kruemblegard/sounds/`

The `assets/kruemblegard/sounds.json` defines these keys:
- `kruemblegard.rise`
- `kruemblegard.core_hum`
- `kruemblegard.attack_smash`
- `kruemblegard.attack_slam`
- `kruemblegard.attack_rune`
- `kruemblegard.radiant`

## Notes / next upgrades (optional)
- Replace the rune barrage placeholder damage with real projectiles.
- Add boss music.
- Convert the arena boundary from “distance check” to a strict circle and include vertical bounds.
- Add phase 2/3 arena behaviors (standing stones rising/rotating).

### Boss music (true music layer)
- Sound event key: `music.kruemblegard`
- Expected asset: `assets/kruemblegard/sounds/kruemblegard_theme.ogg`

### Config-driven waystone rarity
Common config file: `config/kruemblegard-common.toml`

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
        bossPhase4HealthRatio = 0.1
        bossPhase2RuneBoltCooldownTicks = 40
        bossPhase2GraviticPullCooldownTicks = 60
        bossPhase3DashCooldownTicks = 80
        bossPhase3MeteorArmCooldownTicks = 100
        bossPhase3ArcaneStormCooldownTicks = 120
		bossPhase1CleaveCooldownTicks = 50
		bossPhase2RuneVolleyCooldownTicks = 70
		bossPhase3BlinkStrikeCooldownTicks = 60
		bossPhase3MeteorShowerCooldownTicks = 140
		bossPhase4WhirlwindCooldownTicks = 80
		bossPhase4ArcaneBeamCooldownTicks = 120
        bossAbilityGlobalCooldownTicks = 20
        bossPhaseTransitionBuffTicks = 80
        bossPhaseTransitionRadius = 10.0
        bossPhaseTransitionKnockback = 1.1
```
