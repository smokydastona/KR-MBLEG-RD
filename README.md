# Krümblegård (Forge 1.20.1 + GeckoLib)

This project contains a Forge 1.20.1 + GeckoLib mod.

Feature reference: [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md)

## What this mod already does
- **Traprock** can remain dormant until disturbed, then attacks.
- **Pebblit**: a hostile stone-bug that can be tamed with cobblestone.
- **Wayfall**: a new void dimension with End-like floating islands, containing Attuned Ore (custom Wayfall biomes only; spawns restricted to Krümblegård mobs).
- **Attuned Ore**: drops Attuned Rune Shards (Fortune affects drops).
- **Wayfall staples**: staple flora + Wayfall wood sets are registered as blocks/items and injected into Wayfall worldgen (some client assets may be placeholders).
- **Crumbling Codex**: an in-game guidebook that opens with pre-written pages (granted once on first join).
- **Advancements** are granted by code.
- **Loot table** for unique drops.

Codex page text lives in: `src/main/resources/data/kruemblegard/books/crumbling_codex.json`.

This mod uses **custom advancement triggers** registered in `ModCriteria`:
 (currently none)

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
2. Spawn a Traprock:
    - `/summon kruemblegard:traprock`
3. Right-click it (or linger close) to awaken it.

### Pebblit
- Spawn: `/summon kruemblegard:pebblit`
- Tame: right-click a Pebblit with `minecraft:cobblestone`

### Wayfall
- Enter via command (no portal yet):
    - `/execute in kruemblegard:wayfall run tp @s 0 80 0`

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
