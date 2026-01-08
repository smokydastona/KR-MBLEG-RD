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
- **sounds.json name(s)**: kruemblegard:attack_rune
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/attack_rune.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_slam
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_slam`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_SLAM`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:attack_slam
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/attack_slam.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_smash
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_smash`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_SMASH`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:attack_smash
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/attack_smash.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.core_hum
- **SoundEvent ID**: `kruemblegard:kruemblegard.core_hum`
- **ModSounds field**: `KRUEMBLEGARD_CORE_HUM`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:kruemblegard_core_hum
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_core_hum.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.radiant
- **SoundEvent ID**: `kruemblegard:kruemblegard.radiant`
- **ModSounds field**: `KRUEMBLEGARD_RADIANT`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:radiant
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/radiant.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.rise
- **SoundEvent ID**: `kruemblegard:kruemblegard.rise`
- **ModSounds field**: `KRUEMBLEGARD_RISE`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:kruemblegard_rise
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_rise.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_ambient
- **SoundEvent ID**: `kruemblegard:kruemblegard_ambient`
- **ModSounds field**: `KRUEMBLEGARD_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.ambient`
- **sounds.json name(s)**: kruemblegard:kruemblegard_ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_ambient.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_attack
- **SoundEvent ID**: `kruemblegard:kruemblegard_attack`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK`
- **Subtitle key**: `subtitles.kruemblegard.attack`
- **sounds.json name(s)**: kruemblegard:kruemblegard_attack
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_attack.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_dash
- **SoundEvent ID**: `kruemblegard:kruemblegard_dash`
- **ModSounds field**: `KRUEMBLEGARD_DASH`
- **Subtitle key**: `subtitles.kruemblegard.dash`
- **sounds.json name(s)**: kruemblegard:kruemblegard_dash
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_dash.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_death
- **SoundEvent ID**: `kruemblegard:kruemblegard_death`
- **ModSounds field**: `KRUEMBLEGARD_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.death`
- **sounds.json name(s)**: kruemblegard:kruemblegard_death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_death.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_storm
- **SoundEvent ID**: `kruemblegard:kruemblegard_storm`
- **ModSounds field**: `KRUEMBLEGARD_STORM`
- **Subtitle key**: `subtitles.kruemblegard.storm`
- **sounds.json name(s)**: kruemblegard:kruemblegard_storm
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_storm.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### music.kruemblegard
- **SoundEvent ID**: `kruemblegard:music.kruemblegard`
- **ModSounds field**: `KRUEMBLEGARD_MUSIC`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:kruemblegard_theme
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/kruemblegard_theme.ogg = False
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### music.wayfall
- **SoundEvent ID**: `kruemblegard:music.wayfall`
- **ModSounds field**: `WAYFALL_MUSIC`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:jungle-ish-beat-for-video-games
- **Streamed**: `true`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/jungle-ish-beat-for-video-games.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))
- **RecordItem lengthTicks**: `4800`


<!-- AUTO-GENERATED:SOUNDS:END -->
