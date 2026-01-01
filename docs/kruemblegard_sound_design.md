# Krümblegård — Sound Design Spec

This document captures the sound requirements for Krümblegård and the expected asset locations.

## Export Requirements (All Files)
- Format: OGG Vorbis
- Sample rate: 44.1 kHz
- Channels: Mono
- Bitrate: 128–192 kbps
- Volume: Normalized to –1 dBFS
- Mastering:
  - Remove DC offset
  - High‑pass filter at 30 Hz

## Expected Asset Paths
Place the exported `.ogg` files here:
- `src/main/resources/assets/kruemblegard/sounds/kruemblegard_ambient.ogg`
- `src/main/resources/assets/kruemblegard/sounds/kruemblegard_death.ogg`
- `src/main/resources/assets/kruemblegard/sounds/kruemblegard_attack.ogg`
- `src/main/resources/assets/kruemblegard/sounds/kruemblegard_dash.ogg`
- `src/main/resources/assets/kruemblegard/sounds/kruemblegard_storm.ogg`

These are referenced by `src/main/resources/assets/kruemblegard/sounds.json`.

## 1) Ambient Loop (Required)
- File: `kruemblegard_ambient.ogg`
- Length: 4–6 seconds
- Loopable: Yes (seamless)
- Purpose: Plays periodically while Krümblegård is alive; ancient, heavy, magical, stone.

Layers
1. Low stone rumble
   - 40–120 Hz, slow tectonic movement, emphasize sub‑bass
2. Arcane hum
   - 300–600 Hz, slight vibrato (±10 cents), pulse ~1.5s
3. Air resonance / wind-through-stone
   - 1–3 kHz, subtle
4. Occasional rune crackle
   - soft electrical pops, 1–2 per loop

Mixing
- Rumble: –6 dB
- Hum: –10 dB
- Air: –18 dB
- Crackle: –20 dB

Export
- Fade in/out: 10 ms
- Loop point: zero‑crossing

## 2) Death (Required)
- File: `kruemblegard_death.ogg`
- Length: 2–4 seconds
- Loopable: No
- Purpose: Collapsing monument.

Layers
1. Massive stone fracture (200–800 Hz emphasis)
2. Falling boulders (random impacts, light L/R pan)
3. Arcane discharge (3–8 kHz crackle, quick decay)
4. Low sub‑drop (80 Hz → 20 Hz sweep, ~1s)

Mixing
- Fracture: –3 dB
- Boulders: –6 dB
- Discharge: –12 dB
- Sub‑drop: –4 dB

Export
- Hard transient at start
- Tail fades to silence

## 3) Attack Swing (Required)
- File: `kruemblegard_attack.ogg`
- Length: 0.4–0.7 seconds
- Loopable: No

Layers
1. Heavy whoosh (200–800 Hz)
2. Stone scrape (1–3 kHz)
3. Rune flare (600–1200 Hz)

Mixing
- Whoosh: –6 dB
- Scrape: –12 dB
- Rune flare: –10 dB

## 4) Dash Attack (Required)
- File: `kruemblegard_dash.ogg`
- Length: 0.6–1.0 seconds
- Loopable: No

Layers
1. Wind rush (300–2000 Hz)
2. Stone resonance (150–400 Hz, slight tremolo)
3. Arcane surge (1–4 kHz pulse)

Mixing
- Wind: –6 dB
- Stone: –10 dB
- Arcane: –12 dB

## 5) Arcane Storm (Required)
- File: `kruemblegard_storm.ogg`
- Length: 1.0–1.5 seconds
- Loopable: No

Layers
1. Magical hiss (3–10 kHz)
2. Rune pulse (500–900 Hz, repeats 3–4 times)
3. Thunder crackle (200–800 Hz)

Mixing
- Hiss: –14 dB
- Rune pulse: –8 dB
- Thunder: –6 dB

## Subtitle Keys
English subtitles live in `src/main/resources/assets/kruemblegard/lang/en_us.json`.
- `subtitles.kruemblegard.ambient`
- `subtitles.kruemblegard.death`
- `subtitles.kruemblegard.attack`
- `subtitles.kruemblegard.dash`
- `subtitles.kruemblegard.storm`

## Notes / Tools
Recommended sources/tools:
- Freesound.org (stone/rumble/wind)
- Audacity (editing)
- Vital / Surge XT (arcane tones)
