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

#### entity.fault_crawler.ambient
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.ambient`
- **ModSounds field**: `FAULT_CRAWLER_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.ambient`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.fault_crawler.death
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.death`
- **ModSounds field**: `FAULT_CRAWLER_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.death`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.fault_crawler.emerge
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.emerge`
- **ModSounds field**: `FAULT_CRAWLER_EMERGE`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.emerge`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/emerge
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/emerge.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.fault_crawler.hurt
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.hurt`
- **ModSounds field**: `FAULT_CRAWLER_HURT`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.hurt`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.fault_crawler.pulse
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.pulse`
- **ModSounds field**: `FAULT_CRAWLER_PULSE`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.pulse`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/pulse
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/pulse.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.fault_crawler.slam
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.slam`
- **ModSounds field**: `FAULT_CRAWLER_SLAM`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.slam`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/slam
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/slam.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.fault_crawler.step
- **SoundEvent ID**: `kruemblegard:entity.fault_crawler.step`
- **ModSounds field**: `FAULT_CRAWLER_STEP`
- **Subtitle key**: `subtitles.kruemblegard.fault_crawler.step`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/step
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/step.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.great_hunger.ambient
- **SoundEvent ID**: `kruemblegard:entity.great_hunger.ambient`
- **ModSounds field**: `GREAT_HUNGER_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.great_hunger.ambient`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.great_hunger.bite
- **SoundEvent ID**: `kruemblegard:entity.great_hunger.bite`
- **ModSounds field**: `GREAT_HUNGER_BITE`
- **Subtitle key**: `subtitles.kruemblegard.great_hunger.bite`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/bite
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/bite.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.great_hunger.death
- **SoundEvent ID**: `kruemblegard:entity.great_hunger.death`
- **ModSounds field**: `GREAT_HUNGER_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.great_hunger.death`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.great_hunger.hurt
- **SoundEvent ID**: `kruemblegard:entity.great_hunger.hurt`
- **ModSounds field**: `GREAT_HUNGER_HURT`
- **Subtitle key**: `subtitles.kruemblegard.great_hunger.hurt`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.great_hunger.step
- **SoundEvent ID**: `kruemblegard:entity.great_hunger.step`
- **ModSounds field**: `GREAT_HUNGER_STEP`
- **Subtitle key**: `subtitles.kruemblegard.great_hunger.step`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/step
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/step.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.kruemblegard.cast
- **SoundEvent ID**: `kruemblegard:entity.kruemblegard.cast`
- **ModSounds field**: `KRUEMBLEGARD_CAST`
- **Subtitle key**: `subtitles.kruemblegard.kruemblegard.cast`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/pulse
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/pulse.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.kruemblegard.hurt
- **SoundEvent ID**: `kruemblegard:entity.kruemblegard.hurt`
- **ModSounds field**: `KRUEMBLEGARD_HURT`
- **Subtitle key**: `subtitles.kruemblegard.kruemblegard.hurt`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.kruemblegard.roar
- **SoundEvent ID**: `kruemblegard:entity.kruemblegard.roar`
- **ModSounds field**: `KRUEMBLEGARD_ROAR`
- **Subtitle key**: `subtitles.kruemblegard.kruemblegard.roar`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/slam
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/slam.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.kruemblegard.step
- **SoundEvent ID**: `kruemblegard:entity.kruemblegard.step`
- **ModSounds field**: `KRUEMBLEGARD_STEP`
- **Subtitle key**: `subtitles.kruemblegard.kruemblegard.step`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/step
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/step.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.pebblit.ambient
- **SoundEvent ID**: `kruemblegard:entity.pebblit.ambient`
- **ModSounds field**: `PEBBLIT_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.pebblit.ambient`
- **sounds.json name(s)**: kruemblegard:entity/pebblit/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/pebblit/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.pebblit.death
- **SoundEvent ID**: `kruemblegard:entity.pebblit.death`
- **ModSounds field**: `PEBBLIT_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.pebblit.death`
- **sounds.json name(s)**: kruemblegard:entity/pebblit/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/pebblit/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.pebblit.hurt
- **SoundEvent ID**: `kruemblegard:entity.pebblit.hurt`
- **ModSounds field**: `PEBBLIT_HURT`
- **Subtitle key**: `subtitles.kruemblegard.pebblit.hurt`
- **sounds.json name(s)**: kruemblegard:entity/pebblit/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/pebblit/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.pebblit.perch
- **SoundEvent ID**: `kruemblegard:entity.pebblit.perch`
- **ModSounds field**: `PEBBLIT_PERCH`
- **Subtitle key**: `subtitles.kruemblegard.pebblit.perch`
- **sounds.json name(s)**: kruemblegard:entity/pebblit/perch
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/pebblit/perch.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.pebblit.step
- **SoundEvent ID**: `kruemblegard:entity.pebblit.step`
- **ModSounds field**: `PEBBLIT_STEP`
- **Subtitle key**: `subtitles.kruemblegard.pebblit.step`
- **sounds.json name(s)**: kruemblegard:entity/pebblit/step
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/pebblit/step.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.pebblit.tame
- **SoundEvent ID**: `kruemblegard:entity.pebblit.tame`
- **ModSounds field**: `PEBBLIT_TAME`
- **Subtitle key**: `subtitles.kruemblegard.pebblit.tame`
- **sounds.json name(s)**: kruemblegard:entity/pebblit/tame
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/pebblit/tame.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.scaralon_beetle.ambient
- **SoundEvent ID**: `kruemblegard:entity.scaralon_beetle.ambient`
- **ModSounds field**: `SCARALON_BEETLE_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.scaralon_beetle.ambient`
- **sounds.json name(s)**: kruemblegard:entity/scaralon_beetle/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scaralon_beetle/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.scaralon_beetle.attack
- **SoundEvent ID**: `kruemblegard:entity.scaralon_beetle.attack`
- **ModSounds field**: `SCARALON_BEETLE_ATTACK`
- **Subtitle key**: `subtitles.kruemblegard.scaralon_beetle.attack`
- **sounds.json name(s)**: kruemblegard:entity/scaralon_beetle/attack
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scaralon_beetle/attack.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.scaralon_beetle.death
- **SoundEvent ID**: `kruemblegard:entity.scaralon_beetle.death`
- **ModSounds field**: `SCARALON_BEETLE_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.scaralon_beetle.death`
- **sounds.json name(s)**: kruemblegard:entity/scaralon_beetle/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scaralon_beetle/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.scaralon_beetle.hurt
- **SoundEvent ID**: `kruemblegard:entity.scaralon_beetle.hurt`
- **ModSounds field**: `SCARALON_BEETLE_HURT`
- **Subtitle key**: `subtitles.kruemblegard.scaralon_beetle.hurt`
- **sounds.json name(s)**: kruemblegard:entity/scaralon_beetle/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scaralon_beetle/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.scaralon_beetle.step
- **SoundEvent ID**: `kruemblegard:entity.scaralon_beetle.step`
- **ModSounds field**: `SCARALON_BEETLE_STEP`
- **Subtitle key**: `subtitles.kruemblegard.scaralon_beetle.step`
- **sounds.json name(s)**: kruemblegard:entity/scaralon_beetle/step
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scaralon_beetle/step.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.traprock.ambient
- **SoundEvent ID**: `kruemblegard:entity.traprock.ambient`
- **ModSounds field**: `TRAPROCK_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.traprock.ambient`
- **sounds.json name(s)**: kruemblegard:entity/traprock/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.traprock.attack
- **SoundEvent ID**: `kruemblegard:entity.traprock.attack`
- **ModSounds field**: `TRAPROCK_ATTACK`
- **Subtitle key**: `subtitles.kruemblegard.traprock.attack`
- **sounds.json name(s)**: kruemblegard:entity/traprock/attack
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/attack.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.traprock.awaken
- **SoundEvent ID**: `kruemblegard:entity.traprock.awaken`
- **ModSounds field**: `TRAPROCK_AWAKEN`
- **Subtitle key**: `subtitles.kruemblegard.traprock.awaken`
- **sounds.json name(s)**: kruemblegard:entity/traprock/awaken
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/awaken.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.traprock.death
- **SoundEvent ID**: `kruemblegard:entity.traprock.death`
- **ModSounds field**: `TRAPROCK_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.traprock.death`
- **sounds.json name(s)**: kruemblegard:entity/traprock/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.traprock.hurt
- **SoundEvent ID**: `kruemblegard:entity.traprock.hurt`
- **ModSounds field**: `TRAPROCK_HURT`
- **Subtitle key**: `subtitles.kruemblegard.traprock.hurt`
- **sounds.json name(s)**: kruemblegard:entity/traprock/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.traprock.sleep_ambient
- **SoundEvent ID**: `kruemblegard:entity.traprock.sleep_ambient`
- **ModSounds field**: `TRAPROCK_SLEEP_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.traprock.sleep_ambient`
- **sounds.json name(s)**: kruemblegard:entity/traprock/sleep_ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/sleep_ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.wyrdwing.ambient
- **SoundEvent ID**: `kruemblegard:entity.wyrdwing.ambient`
- **ModSounds field**: `WYRDWING_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.wyrdwing.ambient`
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.wyrdwing.attack
- **SoundEvent ID**: `kruemblegard:entity.wyrdwing.attack`
- **ModSounds field**: `WYRDWING_ATTACK`
- **Subtitle key**: `subtitles.kruemblegard.wyrdwing.attack`
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/attack
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/attack.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.wyrdwing.call
- **SoundEvent ID**: `kruemblegard:entity.wyrdwing.call`
- **ModSounds field**: `WYRDWING_CALL`
- **Subtitle key**: `subtitles.kruemblegard.wyrdwing.call`
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/call
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/call.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.wyrdwing.death
- **SoundEvent ID**: `kruemblegard:entity.wyrdwing.death`
- **ModSounds field**: `WYRDWING_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.wyrdwing.death`
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.wyrdwing.hurt
- **SoundEvent ID**: `kruemblegard:entity.wyrdwing.hurt`
- **ModSounds field**: `WYRDWING_HURT`
- **Subtitle key**: `subtitles.kruemblegard.wyrdwing.hurt`
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/hurt
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/hurt.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### entity.wyrdwing.step
- **SoundEvent ID**: `kruemblegard:entity.wyrdwing.step`
- **ModSounds field**: `WYRDWING_STEP`
- **Subtitle key**: `subtitles.kruemblegard.wyrdwing.step`
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/step
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/step.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_rune
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_rune`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_RUNE`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:entity/scattered_enderman/teleport
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scattered_enderman/teleport.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_slam
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_slam`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_SLAM`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:entity/traprock/awaken
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/awaken.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.attack_smash
- **SoundEvent ID**: `kruemblegard:kruemblegard.attack_smash`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK_SMASH`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/slam
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/slam.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.core_hum
- **SoundEvent ID**: `kruemblegard:kruemblegard.core_hum`
- **ModSounds field**: `KRUEMBLEGARD_CORE_HUM`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:entity/scattered_enderman/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scattered_enderman/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.radiant
- **SoundEvent ID**: `kruemblegard:kruemblegard.radiant`
- **ModSounds field**: `KRUEMBLEGARD_RADIANT`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:entity/wyrdwing/call
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/wyrdwing/call.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard.rise
- **SoundEvent ID**: `kruemblegard:kruemblegard.rise`
- **ModSounds field**: `KRUEMBLEGARD_RISE`
- **Subtitle key**: (none)
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/emerge
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/emerge.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_ambient
- **SoundEvent ID**: `kruemblegard:kruemblegard_ambient`
- **ModSounds field**: `KRUEMBLEGARD_AMBIENT`
- **Subtitle key**: `subtitles.kruemblegard.ambient`
- **sounds.json name(s)**: kruemblegard:entity/great_hunger/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/great_hunger/ambient.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_attack
- **SoundEvent ID**: `kruemblegard:kruemblegard_attack`
- **ModSounds field**: `KRUEMBLEGARD_ATTACK`
- **Subtitle key**: `subtitles.kruemblegard.attack`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/slam
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/slam.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_dash
- **SoundEvent ID**: `kruemblegard:kruemblegard_dash`
- **ModSounds field**: `KRUEMBLEGARD_DASH`
- **Subtitle key**: `subtitles.kruemblegard.dash`
- **sounds.json name(s)**: kruemblegard:entity/fault_crawler/pulse
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/fault_crawler/pulse.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_death
- **SoundEvent ID**: `kruemblegard:kruemblegard_death`
- **ModSounds field**: `KRUEMBLEGARD_DEATH`
- **Subtitle key**: `subtitles.kruemblegard.death`
- **sounds.json name(s)**: kruemblegard:entity/traprock/death
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/traprock/death.ogg = True
- **DurationSeconds**: `TBD`
- **DurationTicks**: `TBD` (round(seconds*20))

#### kruemblegard_storm
- **SoundEvent ID**: `kruemblegard:kruemblegard_storm`
- **ModSounds field**: `KRUEMBLEGARD_STORM`
- **Subtitle key**: `subtitles.kruemblegard.storm`
- **sounds.json name(s)**: kruemblegard:entity/scattered_enderman/ambient
- **Streamed**: `false`
- **OGG present**: src/main/resources/assets/kruemblegard/sounds/entity/scattered_enderman/ambient.ogg = True
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













