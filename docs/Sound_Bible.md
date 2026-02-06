# Krümblegård Sound Bible

This document is the **single source of truth** for how **sounds** are authored, registered, named, and used in Krümblegård.

Scope:
- SoundEvent naming + registration (`ModSounds`)
- `assets/kruemblegard/sounds.json` entries (subtitles, streaming)
- **Exact length requirements** (durations in seconds + ticks)

---

## Table of Contents

- [Global Rules](#global-rules)
- [Exact Length Requirements](#exact-length-requirements)
- [Asset & Data Checklist](#asset--data-checklist)
- [Registry Inventory (Generated)](#registry-inventory-generated)

---

## Global Rules

- Use stable, descriptive IDs; do not rename existing sound IDs lightly (resource packs / worlds may reference them).
- Prefer consistent key spaces:
  - Entity/boss SFX: `kruemblegard_*` or `kruemblegard.<action>`
  - Music: `music.*`
- Every sound in `sounds.json` should have a matching registered `SoundEvent` in `ModSounds`.

---

## Exact Length Requirements

These rules exist so music and timed sequences stay perfectly in sync.

- Every custom sound entry in this doc must declare:
  - **DurationSeconds**: exact audio duration in seconds (decimal allowed)
  - **DurationTicks**: exact duration in game ticks
- Conversion rule:
  - `DurationTicks = round(DurationSeconds * 20)`
- If a sound is used by a `RecordItem` (music disc):
  - The `RecordItem`’s `lengthTicks` must match `DurationTicks`.

Notes:
- Minecraft plays at 20 ticks/sec; rounding avoids drift.
- For streamed music (`"stream": true`), exact lengths are especially important.

---

## Asset & Data Checklist

For a new sound:

- Java registration
  - `src/main/java/com/kruemblegard/registry/ModSounds.java`
- Assets
  - Sound definition: `src/main/resources/assets/kruemblegard/sounds.json`
  - Audio file(s): `src/main/resources/assets/kruemblegard/sounds/**/*.ogg`
- Localization
  - If you set `subtitle`, ensure `assets/kruemblegard/lang/en_us.json` contains the subtitle key.

---

## Mob Sound Checklist (Design Targets)

These are the **mob sound hooks we want to have covered** as Krümblegård mobs get custom audio.

Notes:
- This section is a **design + production checklist**. Some entries are not registered yet; register them in `ModSounds` + `sounds.json` when audio is added.
- Recommended naming (vanilla-style): `entity.<mob_id>.<event>`.
- For every entry: fill `DurationSeconds` + `DurationTicks` once the OGG is final.

### `kruemblegard:kruemblegard` (Boss)
- Current custom sounds used in code:
  - `kruemblegard:kruemblegard_ambient` (ambient)
  - `kruemblegard:kruemblegard_death` (death)
  - `kruemblegard:kruemblegard_attack` (multiple attacks)
  - `kruemblegard:kruemblegard_dash` (dash)
  - `kruemblegard:kruemblegard_storm` (storm)
- Custom sounds to add next (recommended IDs):
  - `kruemblegard:entity.kruemblegard.hurt`
  - `kruemblegard:entity.kruemblegard.step`
  - `kruemblegard:entity.kruemblegard.roar` (phase change / intro)
  - `kruemblegard:entity.kruemblegard.cast` (rune/storm wind-up)

### `kruemblegard:traprock`
- Current sound behavior: inherits Blaze sounds when awakened; dormant state suppresses Blaze client-side `aiStep` effects.
- Custom sounds to add next (recommended IDs):
  - `kruemblegard:entity.traprock.sleep_ambient`
  - `kruemblegard:entity.traprock.awaken`
  - `kruemblegard:entity.traprock.ambient`
  - `kruemblegard:entity.traprock.hurt`
  - `kruemblegard:entity.traprock.death`
  - `kruemblegard:entity.traprock.attack`

### `kruemblegard:pebblit`
- Current sound behavior: inherits Silverfish sounds.
- Custom sounds to add next (recommended IDs):
  - `kruemblegard:entity.pebblit.ambient`
  - `kruemblegard:entity.pebblit.hurt`
  - `kruemblegard:entity.pebblit.death`
  - `kruemblegard:entity.pebblit.step`
  - `kruemblegard:entity.pebblit.tame`
  - `kruemblegard:entity.pebblit.perch`

### `kruemblegard:great_hunger`
- Current sound behavior: generic LivingEntity sounds (no custom hooks yet).
- Custom sounds to add next (recommended IDs):
  - `kruemblegard:entity.great_hunger.ambient`
  - `kruemblegard:entity.great_hunger.hurt`
  - `kruemblegard:entity.great_hunger.death`
  - `kruemblegard:entity.great_hunger.step`
  - `kruemblegard:entity.great_hunger.bite`

### `kruemblegard:scattered_enderman`
- Current sound behavior: inherits Enderman sounds (teleport, stare, ambient, etc).
- Custom sounds to add next (recommended IDs):
  - `kruemblegard:entity.scattered_enderman.ambient`
  - `kruemblegard:entity.scattered_enderman.hurt`
  - `kruemblegard:entity.scattered_enderman.death`
  - `kruemblegard:entity.scattered_enderman.teleport`
  - `kruemblegard:entity.scattered_enderman.scream`

---

## Registry Inventory (Generated)

This section is **auto-generated** from:
- `src/main/java/com/kruemblegard/registry/ModSounds.java`
- `src/main/resources/assets/kruemblegard/sounds.json`

Update workflow:
- After adding/removing/renaming sounds, run `tools/generate_sound_bible.ps1`.
- Commit the resulting doc changes in the same PR/commit as the content change.

<!-- AUTO-GENERATED:SOUNDS:START -->

### Sounds (All Registered / Declared)

#### kruemblegard.attack_rune
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_rune`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_RUNE`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_slam
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_slam`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_SLAM`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_smash
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_smash`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_SMASH`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.core_hum
- **SoundEvent ID**: `kruemblegard:kruemblegard.core_hum`
- **ModSounds field**: `KRUEMBLEGARD_CORE_HUM`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.radiant
- **SoundEvent ID**: `kruemblegard:kruemblegard.radiant`
- **ModSounds field**: `KRUEMBLEGARD_RADIANT`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.rise
- **SoundEvent ID**: `kruemblegard:kruemblegard.rise`
- **ModSounds field**: `KRUEMBLEGARD_RISE`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_ambient
- **SoundEvent ID**: `kruemblegard:kruemblegard_ambient`
- **ModSounds field**: `KRUEMBLEGARD_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.ambient`
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_attack
- **SoundEvent ID**: `kruemblegard:kruemblegard_attack`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK`
- **Subtitle key**: `subtitles.kruemblegard.attack`
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_dash
- **SoundEvent ID**: `kruemblegard:kruemblegard_dash`
- **ModSounds field**: `KRUEMBLEGARD_DASH`
- **Subtitle key**: `subtitles.kruemblegard.dash`
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_death
- **SoundEvent ID**: `kruemblegard:kruemblegard_death`
- **ModSounds field**: `KRUEMBLEGARD_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.death`
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_storm
- **SoundEvent ID**: `kruemblegard:kruemblegard_storm`
- **ModSounds field**: `KRUEMBLEGARD_STORM`
- **Subtitle key**: `subtitles.kruemblegard.storm`
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### music.kruemblegard
- **SoundEvent ID**: `kruemblegard:music.kruemblegard`
- **ModSounds field**: `KRUEMBLEGARD_MUSIC`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### music.wayfall
- **SoundEvent ID**: `kruemblegard:music.wayfall`
- **ModSounds field**: `WAYFALL_MUSIC`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:horror-background-atmosphere-09_universfield
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

<!-- AUTO-GENERATED:SOUNDS:END -->






