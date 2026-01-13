# Krümblegård (Forge 1.20.1 + GeckoLib)

This project contains a Forge 1.20.1 + GeckoLib mod.

Recommended Forge runtime: `1.20.1-47.4.0` (or newer 1.20.1 patch). If you see a shutdown crash like `SimpleCommentedConfig cannot be cast to CommentedFileConfig` when leaving a world, update the instance’s Forge.

Feature reference: [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md)

Material references:
- Blocks: [docs/Block_Material_Bible.md](docs/Block_Material_Bible.md)
- Items: [docs/Item_Material_Bible.md](docs/Item_Material_Bible.md) (refresh generated inventories via `tools/generate_material_bibles.ps1`)
- Sounds: [docs/Sound_Bible.md](docs/Sound_Bible.md) (refresh generated inventory via `tools/generate_sound_bible.ps1`)

## What this mod already does
- **Traprock** can remain dormant until disturbed, then attacks.
- **Pebblit**: a hostile stone-bug that can be tamed with cobblestone; right-click with empty hand toggles sit, and shift + right-click perches it on your shoulder (granting knockback resistance) until it dies; its attacks apply knockback.
- **The Great Hunger**: a hostile GeckoLib-rendered mob (spawn egg available).
- **Wayfall**: a new void dimension with Aether-inspired floating islands, containing Attuned Ore (custom Wayfall biomes only; spawns restricted to Krümblegård mobs). Terrain palette defaults to **Fractured Wayrock** via custom noise settings, and portal arrivals fall back to a safe platform if the spawn area is void.
- **Attuned Ore**: drops Attuned Rune Shards (Fortune affects drops).
- **Wayfall staples**: staple flora + Wayfall wood sets are registered as blocks/items and injected into Wayfall worldgen (some client assets may be placeholders).
- **Wayfall surface covers**: Ashmoss/Runegrowth/Voidfelt (placeholder textures; basic conversion rules).
- **Paleweft (Wayfall farming)**: Paleweft Grass spawns in Wayfall biomes (worldgen; mix of short + tall) and bonemealed Runegrowth can bloom Paleweft + other biome plants (excluding the other food plants and saplings); Paleweft Seeds can be farmed into Paleweft Corn yielding Weftkern + rare Echokern, craftable into Weftmeal.
- **Rubble Tilth**: a Wayfall farmland analog created by hoeing rubble-tillable Wayfall soils.
- **Guidebook (Written Book)**: players are given a vanilla `minecraft:written_book` on first join, pre-filled with your mod’s guide text.
- **Advancements** are granted by code.
- **Loot table** for unique drops.

Guidebook page text lives in: `src/main/resources/data/kruemblegard/books/crumbling_codex.json`.

## Worldgen organization
- Datapack content lives under `src/main/resources/data/kruemblegard/worldgen/**`.
- Code references to important worldgen IDs are centralized in `src/main/java/com/kruemblegard/worldgen/ModWorldgenKeys.java`.
- On server start, `com.kruemblegard.worldgen.WorldgenValidator` logs warnings if critical registry entries/tags are missing.

## Asset licensing
- Don’t copy assets from other mods. See `CONTRIBUTING.md`.

This mod uses **custom advancement triggers** registered in `ModCriteria`:
- `kruemblegard:pebblit_shoulder` (grants the "A little Clingy" advancement)

## Dependencies (ForgeGradle)
You need GeckoLib 4.x for Forge 1.20.1.

This mod also has **native Waystones integration** (the Ancient Waystone is a real Waystones variant). That requires **Waystones + Balm**.

```gradle
repositories {
    maven { url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/' }
}

dependencies {
    implementation fg.deobf('software.bernie.geckolib:geckolib-forge-1.20.1:4.+')

    // Waystones integration
    implementation fg.deobf('curse.maven:waystones-245755:6856603')
    implementation fg.deobf('curse.maven:balm-531761:7420617')
}
```

## Registration checklist (if you rename things)
- Mod id used here: `kruemblegard`
- Base package used here: `com.kruemblegard`

If your mod id/package differ, update:
- `com.kruemblegard.Kruemblegard` (MODID constant)
- `META-INF/mods.toml`
- resource folder names: `assets/kruemblegard`, `data/kruemblegard`

## How to run quickly (dev)
1. Start a dev world.
2. Spawn a Traprock:
    - `/summon kruemblegard:traprock`
3. Right-click it (or linger close) to awaken it.

### Pebblit
- Spawn: `/summon kruemblegard:pebblit`
- Tame: right-click a Pebblit with `minecraft:cobblestone`

### Wayfall
- Enter via the `kruemblegard:wayfall_portal` dev portal block (place it in creative and walk into it).
- Or enter via command:
    - `/execute in kruemblegard:wayfall run tp @s 0 160 0`

## Assets you still need to provide
This mod references textures/sounds; some binary assets may still be missing depending on what you’re running.

Note: this repo includes placeholder generators under `tools/` that can backfill missing referenced PNGs for dev so you don’t see purple/black missing-texture spam.

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
