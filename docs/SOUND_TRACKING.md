# Krümblegård — Sound Tracking (Design Intent + Current Assets)

This document is a **practical change log / tracker** for mob sounds.

For each mob sound, it records:
1) **What it should sound like** (design intent) based on that mob’s identity and the Wayfall tone.
2) **What is currently used** (exact `sounds.json` entry + `.ogg` path).
3) **Where the credit lives** so changes don’t drift out of sync.

Related docs:
- Credits: `docs/SOUND_CREDITS.md`
- Rules + generated inventory: `docs/Sound_Bible.md`

---

## How to use this doc when you change a sound

When you replace an `.ogg`:
- Keep the **SoundEvent ID** stable when possible (avoid breaking packs/worlds).
- Update `docs/SOUND_CREDITS.md` (add/remove/update the source + license as needed).
- Update this doc’s **Current asset** + **Credits** line for the sound you touched.
- Re-run `tools/audit_sound_uniqueness.ps1` (uniqueness + missing-asset check).

---

## Exceptions (vanilla by design)

### Moogloom (`moogloom`)
- Custom sounds: none (uses vanilla Mooshroom/Cow sounds).
- Credits: none.

### Scattered Enderman (`scattered_enderman`)
- Custom sounds: none (uses vanilla Enderman sounds).
- Credits: none.

---

## Boss: Krümblegård (`kruemblegard`)

Identity notes:
- A multi-phase, rune-driven guardian/war machine of the Wayfall.
- Sound palette should feel like **stone mass + rune energy + storm pressure**, not “animal”.
- “No music/instruments” rule applies to mob SFX; boss **music** is tracked separately below.

### `kruemblegard_ambient` (Boss ambient)
- Should sound like: distant stone pressure, sub-bass rumble, slow breathing-like grit; intimidating but not melodic.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard_ambient`
  - sounds.json: `kruemblegard:entity/kruemblegard/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/ambient.ogg`
- Credits: Procedural / ffmpeg (see `docs/SOUND_CREDITS.md`).

### `kruemblegard_death` (Boss death)
- Should sound like: collapsing monument; stone shear + low boom + dusty tail; clearly “fight ended”.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard_death`
  - sounds.json: `kruemblegard:entity/kruemblegard/death`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/death.ogg`
- Credits: Procedural / ffmpeg.

### `kruemblegard_attack` (Boss heavy hit)
- Should sound like: weight + impact; cracked-stone smack with a short sub hit; no tonal “clang”.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard_attack`
  - sounds.json: `kruemblegard:entity/kruemblegard/attack`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/attack.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `kruemblegard_dash` (Boss dash)
- Should sound like: sudden pressure release / gravel skid; whoosh that reads as mass moving fast.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard_dash`
  - sounds.json: `kruemblegard:entity/kruemblegard/dash`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/dash.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `kruemblegard_storm` (Boss storm)
- Should sound like: rune-storm surge; gritty wind + distant thunder pressure; short, punchy “storm cue”.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard_storm`
  - sounds.json: `kruemblegard:entity/kruemblegard/storm`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/storm.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.kruemblegard.hurt` (Boss hurt vocalization)
- Should sound like: an inhuman scream/strain that reads as pain through stone/runes (short, not comedic).
- Current asset:
  - SoundEvent: `kruemblegard:entity.kruemblegard.hurt`
  - sounds.json: `kruemblegard:entity/kruemblegard/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/hurt.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `entity.kruemblegard.roar` (Boss roar / intro / phase cue)
- Should sound like: a “wake-up” bellow that’s more quake than animal; short shock + tail.
- Current asset:
  - SoundEvent: `kruemblegard:entity.kruemblegard.roar`
  - sounds.json: `kruemblegard:entity/kruemblegard/roar`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/roar.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.kruemblegard.cast` (Boss cast / rune wind-up)
- Should sound like: rune ignition; crackle/static + wind-up swell; signals “spell is happening”.
- Current asset:
  - SoundEvent: `kruemblegard:entity.kruemblegard.cast`
  - sounds.json: `kruemblegard:entity/kruemblegard/cast`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/cast.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `entity.kruemblegard.step` (Boss step)
- Should sound like: heavy stone footfall with grit; short so it doesn’t spam.
- Current asset:
  - SoundEvent: `kruemblegard:entity.kruemblegard.step`
  - sounds.json: `kruemblegard:entity/kruemblegard/step`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/step.ogg`
- Credits: Procedural / ffmpeg.

### `kruemblegard.rise` (Fight cue: rise)
- Should sound like: big “stone wakes” cue; overlaps with roar/ambient but must read as one-shot.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard.rise`
  - sounds.json: `kruemblegard:entity/kruemblegard/roar`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/roar.ogg`
- Credits: Procedural / ffmpeg.

### `kruemblegard.core_hum` (Fight cue: core hum)
- Should sound like: sustained low hum; indicates “core is active”.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard.core_hum`
  - sounds.json: `kruemblegard:entity/kruemblegard/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/ambient.ogg`
- Credits: Procedural / ffmpeg.

### `kruemblegard.attack_smash` / `kruemblegard.attack_slam` (Fight cues: smash + slam)
- Should sound like: impact variations (optional future split) but currently can share a single “heavy hit” identity.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard.attack_smash`
  - sounds.json: `kruemblegard:entity/kruemblegard/attack`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/attack.ogg`
  - SoundEvent: `kruemblegard:kruemblegard.attack_slam`
  - sounds.json: `kruemblegard:entity/kruemblegard/attack`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/attack.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `kruemblegard.attack_rune` (Fight cue: rune attack)
- Should sound like: rune cast “snap”/charge; clear telegraph.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard.attack_rune`
  - sounds.json: `kruemblegard:entity/kruemblegard/cast`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/cast.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `kruemblegard.radiant` (Fight cue: radiant)
- Should sound like: bright energy surge without turning into music; stormy rune flare.
- Current asset:
  - SoundEvent: `kruemblegard:kruemblegard.radiant`
  - sounds.json: `kruemblegard:entity/kruemblegard/storm`
  - OGG: `assets/kruemblegard/sounds/entity/kruemblegard/storm.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `music.kruemblegard` (Boss music)
- Should sound like: oppressive atmosphere; supports boss pacing; separate from SFX rules.
- Current asset:
  - SoundEvent: `kruemblegard:music.kruemblegard`
  - sounds.json: `kruemblegard:horror-background-atmosphere-09_universfield` (streamed)
  - OGG: `assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg`
- Credits: tracked outside `docs/SOUND_CREDITS.md` (this track is not under `entity/**`).

---

## Traprock (`traprock`)

Identity notes:
- Dormant stone-creature ambush predator; it “pretends to be the path/stone” until you get close.
- Sound palette should be **subtle stone life** (sleep/idle) + **violent rock movement** (awaken/attack).

### `entity.traprock.sleep_ambient` (Dormant idle)
- Should sound like: barely-there “stone breathing”/grind; loopable; should not reveal itself loudly.
- Current asset:
  - SoundEvent: `kruemblegard:entity.traprock.sleep_ambient`
  - sounds.json: `kruemblegard:entity/traprock/sleep_ambient`
  - OGG: `assets/kruemblegard/sounds/entity/traprock/sleep_ambient.ogg`
- Credits: SoundMonster Public Domain (“snore loop”).

### `entity.traprock.awaken` (Wake trigger)
- Should sound like: sudden rock shift; a heavy knock/scrape that says “that stone moved”.
- Current asset:
  - SoundEvent: `kruemblegard:entity.traprock.awaken`
  - sounds.json: `kruemblegard:entity/traprock/awaken`
  - OGG: `assets/kruemblegard/sounds/entity/traprock/awaken.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.traprock.ambient` (Awake idle)
- Should sound like: low rumble + occasional grit/pebble movement; not musical, not animal.
- Current asset:
  - SoundEvent: `kruemblegard:entity.traprock.ambient`
  - sounds.json: `kruemblegard:entity/traprock/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/traprock/ambient.ogg`
- Credits: Procedural / ffmpeg.

### `entity.traprock.hurt` (Damage)
- Should sound like: strained crack + rocky exhale; short and sharp.
- Current asset:
  - SoundEvent: `kruemblegard:entity.traprock.hurt`
  - sounds.json: `kruemblegard:entity/traprock/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/traprock/hurt.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.traprock.death` (Death)
- Should sound like: stone collapse; crumble + dust tail; clearly final.
- Current asset:
  - SoundEvent: `kruemblegard:entity.traprock.death`
  - sounds.json: `kruemblegard:entity/traprock/death`
  - OGG: `assets/kruemblegard/sounds/entity/traprock/death.ogg`
- Credits: Procedural / ffmpeg.

### `entity.traprock.attack` (Attack)
- Should sound like: aggressive lunge/strike cue; sharp and threatening without “instrument clang”.
- Current asset:
  - SoundEvent: `kruemblegard:entity.traprock.attack`
  - sounds.json: `kruemblegard:entity/traprock/attack`
  - OGG: `assets/kruemblegard/sounds/entity/traprock/attack.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

---

## Pebblit (`pebblit`)

Identity notes:
- Small stone-ish creature; neutral/retaliatory; can be tamed, perched, and is “cute but scrappy”.
- Sound palette should be **small**, **percussive**, and **friendly** when tamed/perched.

### `entity.pebblit.ambient` (Idle)
- Should sound like: tiny chirps/chitters with a stony twist; non-threatening.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebblit.ambient`
  - sounds.json: `kruemblegard:entity/pebblit/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/pebblit/ambient.ogg`
- Credits: SoundMonster Public Domain (“laughter cute”).

### `entity.pebblit.hurt` (Damage)
- Should sound like: small yelp/cough; quick; not gross.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebblit.hurt`
  - sounds.json: `kruemblegard:entity/pebblit/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/pebblit/hurt.ogg`
- Credits: soundfx-master (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `entity.pebblit.death` (Death)
- Should sound like: small fade-out/bye; not overly dramatic.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebblit.death`
  - sounds.json: `kruemblegard:entity/pebblit/death`
  - OGG: `assets/kruemblegard/sounds/entity/pebblit/death.ogg`
- Credits: SoundMonster Public Domain (“bye bye”).

### `entity.pebblit.step` (Footstep)
- Should sound like: pebble taps; short, repeat-safe.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebblit.step`
  - sounds.json: `kruemblegard:entity/pebblit/step`
  - OGG: `assets/kruemblegard/sounds/entity/pebblit/step.ogg`
- Credits: Procedural / ffmpeg.

### `entity.pebblit.tame` (Tame confirmation)
- Should sound like: pleased “mmm” / happy acknowledgement.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebblit.tame`
  - sounds.json: `kruemblegard:entity/pebblit/tame`
  - OGG: `assets/kruemblegard/sounds/entity/pebblit/tame.ogg`
- Credits: soundfx-master (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.pebblit.perch` (Perch/shoulder)
- Should sound like: cute, affirmative one-shot; “I’m on your shoulder now”.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebblit.perch`
  - sounds.json: `kruemblegard:entity/pebblit/perch`
  - OGG: `assets/kruemblegard/sounds/entity/pebblit/perch.ogg`
- Credits: soundfx-master (CC0; see `docs/SOUND_CREDITS.md`).

---

## Pebble Wren (`pebble_wren`)

Identity notes:
- Small airborne songbird companion for Wayfall.
- Sound palette should stay tiny, dry, and bright, with more personality than a generic parrot and no long melodic phrases.

### `entity.pebble_wren.ambient` (Idle chirp)
- Should sound like: tiny woodland peep, dry and brief, easy to repeat without fatigue.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.ambient`
  - sounds.json: `kruemblegard:entity/pebble_wren/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/ambient.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs; see `docs/SOUND_CREDITS.md` and `docs/audio_licenses/pebble_wren/manifest.md`.

### `entity.pebble_wren.perch_call` (Initiating perch chirp)
- Should sound like: clearer attention cue, still soft but more forward than the idle chirp.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.perch_call`
  - sounds.json: `kruemblegard:entity/pebble_wren/perch_call`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/perch_call.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs.

### `entity.pebble_wren.perch_reply` (Answer chirp)
- Should sound like: softer conversational answer, shorter and less assertive than the perch call.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.perch_reply`
  - sounds.json: `kruemblegard:entity/pebble_wren/perch_reply`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/perch_reply.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs.

### `entity.pebble_wren.flourish` (Social flourish)
- Should sound like: decorative chirrup that reads as a tiny social flourish, not a danger cue.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.flourish`
  - sounds.json: `kruemblegard:entity/pebble_wren/flourish`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/flourish.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs.

### `entity.pebble_wren.hurt` (Damage)
- Should sound like: sharp stylized peep, short enough to avoid sounding like recorded animal distress.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.hurt`
  - sounds.json: `kruemblegard:entity/pebble_wren/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/hurt.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs.

### `entity.pebble_wren.death` (Death)
- Should sound like: a soft falling chirp with a short tail, stylized rather than grim.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.death`
  - sounds.json: `kruemblegard:entity/pebble_wren/death`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/death.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs.

### `entity.pebble_wren.flutter` (Takeoff / landing)
- Should sound like: a light, quick songbird flap burst with minimal low thump and a soft papery feather read, without vocal content.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.flutter`
  - sounds.json: `kruemblegard:entity/pebble_wren/flutter`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/flutter.ogg`
- Credits: Trimmed from `examples/_extracted/sound_libraies/psfx-main/psfx-main/library/creature/movement/flight/wings/beating-wings-small-001.ogg`, then high-passed, shortened, and pitched up for a lighter small-bird takeoff/landing cue.

### `entity.pebble_wren.ore_ping` (Ore-find cue)
- Should sound like: bright tonal trill with a slight airy shimmer so the success cue reads differently from social chatter.
- Current asset:
  - SoundEvent: `kruemblegard:entity.pebble_wren.ore_ping`
  - sounds.json: `kruemblegard:entity/pebble_wren/ore_ping`
  - OGG: `assets/kruemblegard/sounds/entity/pebble_wren/ore_ping.ogg`
- Credits: Procedurally synthesized in-house with `ffmpeg` chirp filter graphs and a short echo tail.

---

## Unkeeper (`unkeeper`)

Identity notes:
- Hostile GeckoLib monster; “hunger” implies consuming, chasing, relentless appetite.
- Sound palette should be **predatory**, **wet/strained** but kept within Wayfall’s “stone-warped” tone.

### `entity.unkeeper.ambient` (Idle)
- Should sound like: low, hungry breath; a pressure growl without musical tone.
- Current asset:
  - SoundEvent: `kruemblegard:entity.unkeeper.ambient`
  - sounds.json: `kruemblegard:entity/unkeeper/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/unkeeper/ambient.ogg`
- Credits: Procedural / ffmpeg.

### `entity.unkeeper.hurt` (Damage)
- Should sound like: pained scream/strain; quick; threatening.
- Current asset:
  - SoundEvent: `kruemblegard:entity.unkeeper.hurt`
  - sounds.json: `kruemblegard:entity/unkeeper/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/unkeeper/hurt.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `entity.unkeeper.death` (Death)
- Should sound like: final collapse; a deep exhale into rubble.
- Current asset:
  - SoundEvent: `kruemblegard:entity.unkeeper.death`
  - sounds.json: `kruemblegard:entity/unkeeper/death`
  - OGG: `assets/kruemblegard/sounds/entity/unkeeper/death.ogg`
- Credits: Procedural / ffmpeg.

### `entity.unkeeper.step` (Footstep)
- Should sound like: heavier stalking steps; stone-on-stone thuds; short.
- Current asset:
  - SoundEvent: `kruemblegard:entity.unkeeper.step`
  - sounds.json: `kruemblegard:entity/unkeeper/step`
  - OGG: `assets/kruemblegard/sounds/entity/unkeeper/step.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `entity.unkeeper.bite` (Bite attack)
- Should sound like: snap/crack; sharp, readable melee cue.
- Current asset:
  - SoundEvent: `kruemblegard:entity.unkeeper.bite`
  - sounds.json: `kruemblegard:entity/unkeeper/bite`
  - OGG: `assets/kruemblegard/sounds/entity/unkeeper/bite.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

---

## Fault Crawler (`fault_crawler`)

Identity notes:
- Spider-like creature formed from unstable stone plates; often buried; emerges near players.
- Sound palette should be **plate clacks**, **stone scrape**, and **fault pulses**.

### `entity.fault_crawler.ambient` (Idle)
- Should sound like: quiet plate shifting; a “living rock joint” noise; not constant hiss.
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.ambient`
  - sounds.json: `kruemblegard:entity/fault_crawler/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/ambient.ogg`
- Credits: Procedural / ffmpeg.

### `entity.fault_crawler.hurt` (Damage)
- Should sound like: brittle crack + chitter; short.
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.hurt`
  - sounds.json: `kruemblegard:entity/fault_crawler/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/hurt.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.fault_crawler.death` (Death)
- Should sound like: plates collapsing; multiple small cracks into a final crumble.
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.death`
  - sounds.json: `kruemblegard:entity/fault_crawler/death`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/death.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.fault_crawler.step` (Movement)
- Should sound like: light stone plate taps (not fleshy).
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.step`
  - sounds.json: `kruemblegard:entity/fault_crawler/step`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/step.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.fault_crawler.pulse` (Fault pulse)
- Should sound like: short shockwave; sub “thump” + dusty tail.
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.pulse`
  - sounds.json: `kruemblegard:entity/fault_crawler/pulse`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/pulse.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

### `entity.fault_crawler.slam` (Slam)
- Should sound like: heavier impact than a step; crack + thud.
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.slam`
  - sounds.json: `kruemblegard:entity/fault_crawler/slam`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/slam.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.fault_crawler.emerge` (Emerge from ground)
- Should sound like: rubble breakup + scrape; “something just came out of the floor”.
- Current asset:
  - SoundEvent: `kruemblegard:entity.fault_crawler.emerge`
  - sounds.json: `kruemblegard:entity/fault_crawler/emerge`
  - OGG: `assets/kruemblegard/sounds/entity/fault_crawler/emerge.ogg`
- Credits: Procedural / ffmpeg.

---

## Scaralon Beetle (`scaralon_beetle`)

Identity notes:
- Large rune-etched flying beetle mount; skittish in wild; tough, armored; heroic “rideable” vibe.
- Sound palette should be **chitin/wing** + **rune-etched heft**, not horse whinnies.

### `entity.scaralon_beetle.ambient` (Idle)
- Should sound like: low insect drone/wing rustle; occasional chitin clicks.
- Current asset:
  - SoundEvent: `kruemblegard:entity.scaralon_beetle.ambient`
  - sounds.json: `kruemblegard:entity/scaralon_beetle/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/scaralon_beetle/ambient.ogg`
- Credits: Procedural / ffmpeg.

### `entity.scaralon_beetle.hurt` (Damage)
- Should sound like: armored insect strain; sharp click + hiss (non-musical).
- Current asset:
  - SoundEvent: `kruemblegard:entity.scaralon_beetle.hurt`
  - sounds.json: `kruemblegard:entity/scaralon_beetle/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/scaralon_beetle/hurt.ogg`
- Credits: Procedural / ffmpeg.

### `entity.scaralon_beetle.death` (Death)
- Should sound like: final wing collapse + chitin crack; short.
- Current asset:
  - SoundEvent: `kruemblegard:entity.scaralon_beetle.death`
  - sounds.json: `kruemblegard:entity/scaralon_beetle/death`
  - OGG: `assets/kruemblegard/sounds/entity/scaralon_beetle/death.ogg`
- Credits: Procedural / ffmpeg.

### `entity.scaralon_beetle.step` (Ground movement)
- Should sound like: heavier chitin/hoof-like taps, but still “bug”; repeat-safe.
- Current asset:
  - SoundEvent: `kruemblegard:entity.scaralon_beetle.step`
  - sounds.json: `kruemblegard:entity/scaralon_beetle/step`
  - OGG: `assets/kruemblegard/sounds/entity/scaralon_beetle/step.ogg`
- Credits: Procedural / ffmpeg.

### `entity.scaralon_beetle.attack` (Attack)
- Should sound like: horn/chitin stab; a short aggressive cue.
- Current asset:
  - SoundEvent: `kruemblegard:entity.scaralon_beetle.attack`
  - sounds.json: `kruemblegard:entity/scaralon_beetle/attack`
  - OGG: `assets/kruemblegard/sounds/entity/scaralon_beetle/attack.ogg`
- Credits: SoundFxLibrary (CC BY 3.0; attribution required; see `docs/SOUND_CREDITS.md`).

---

## Wyrdwing (`wyrdwing`)

Identity notes:
- Yi qi-inspired gliding creature; airborne swoops; perches; scavenges Bug Meat; can be tamed.
- Sound palette should be **membrane wing**, **raptor-ish call**, **scratch/pounce energy**; not “songbird”.

### `entity.wyrdwing.ambient` (Idle)
- Should sound like: odd soft squeek/chatter; non-musical.
- Current asset:
  - SoundEvent: `kruemblegard:entity.wyrdwing.ambient`
  - sounds.json: `kruemblegard:entity/wyrdwing/ambient`
  - OGG: `assets/kruemblegard/sounds/entity/wyrdwing/ambient.ogg`
- Credits: User-provided clip (claimed Pixabay source); see `docs/SOUND_CREDITS.md`.

### `entity.wyrdwing.hurt` (Damage)
- Should sound like: sharp screech/yelp; quick.
- Current asset:
  - SoundEvent: `kruemblegard:entity.wyrdwing.hurt`
  - sounds.json: `kruemblegard:entity/wyrdwing/hurt`
  - OGG: `assets/kruemblegard/sounds/entity/wyrdwing/hurt.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.wyrdwing.death` (Death)
- Should sound like: final exhale + drop; short; not “melodic”.
- Current asset:
  - SoundEvent: `kruemblegard:entity.wyrdwing.death`
  - sounds.json: `kruemblegard:entity/wyrdwing/death`
  - OGG: `assets/kruemblegard/sounds/entity/wyrdwing/death.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.wyrdwing.step` (Ground movement)
- Should sound like: light claw taps; dry.
- Current asset:
  - SoundEvent: `kruemblegard:entity.wyrdwing.step`
  - sounds.json: `kruemblegard:entity/wyrdwing/step`
  - OGG: `assets/kruemblegard/sounds/entity/wyrdwing/step.ogg`
- Credits: SoundFxLibrary (CC0; see `docs/SOUND_CREDITS.md`).

### `entity.wyrdwing.attack` (Attack)
- Should sound like: swoop strike cue; sharp and quick.
- Current asset:
  - SoundEvent: `kruemblegard:entity.wyrdwing.attack`
  - sounds.json: `kruemblegard:entity/wyrdwing/attack`
  - OGG: `assets/kruemblegard/sounds/entity/wyrdwing/attack.ogg`
- Credits: SoundMonster Public Domain (assorted; see `docs/SOUND_CREDITS.md`).

### `entity.wyrdwing.call` (Call / flavor)
- Should sound like: a distinctive Wyrdwing cry/call; readable at distance; not musical.
- Current asset:
  - SoundEvent: `kruemblegard:entity.wyrdwing.call`
  - sounds.json: `kruemblegard:entity/wyrdwing/call`
  - OGG: `assets/kruemblegard/sounds/entity/wyrdwing/call.ogg`
- Credits: Original (user-made); see `docs/SOUND_CREDITS.md`.
