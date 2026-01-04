# Changelog

All notable changes to this project will be documented in this file.

Changelog entries are grouped by the exact mod version embedded in the built jar.

## Unreleased
- (Nothing notable yet.)

## 1.0.102 (2026-01-04)
- Animations: add missing `attack_*` boss attack animation entries (placeholders) for Blockbench editing.
- Boss/docs: switch boss attack animations from `boss_attack_*` back to the `attack_*` keys.

## 1.0.101 (2026-01-03)
- Animations: rename boss attack animation keys to a consistent `boss_attack_*` naming scheme.
- Boss/docs: update references to the renamed animations.

## 1.0.100 (2026-01-03)
- Animations: fix duplicate animation-key collisions by renaming the four overlapping attack animations to unique boss-specific keys.

## 1.0.99 (2026-01-03)
- Animations: rename the 12 boss attack animations to ability-specific keys (e.g. `attack_cleave`, `attack_rune_bolt`, etc.).
- Boss/docs: update code + documentation to reference the renamed animations.

## 1.0.98 (2026-01-03)
- Docs: clarify how `totalTicks`/`impactAt` maps to Blockbench impact frames.

## 1.0.97 (2026-01-03)
- Docs: refresh config snippet and correct changelog date.
- Animations: update the JSON controller example to reference the new attack animation names.

## 1.0.96 (2026-01-03)
- Boss: add Phase 4 and expand to 12 unique attacks (fast/heavy/ranged per phase).
- Boss: add 12 new GeckoLib attack animations.
- Docs: update boss attack timings/animation names.

## 1.0.95 (2026-01-03)
- Boss: each phase now uses a fast/heavy/ranged attack kit (less close-range bias).

## 1.0.94 (2026-01-03)
- Boss: minor geo + animation JSON tweaks.

## 1.0.93 (2026-01-03)
- Update Kruemblegård boss GeckoLib animation definitions.
- Docs: restore missing changelog version sections.

## 1.0.92 (2026-01-03)
- CI/versioning: ensure jar version matches GitHub Actions by using full-history checkout and a numeric `1.0.<commitCount>` version.

## 1.0.91 (2026-01-03)
- Versioning/docs: add versioned changelog sections and deterministic jar versioning.

## 1.0.90 (2026-01-03)
- Fix `kruemblegard.animation.json` malformed JSON that could crash client startup (GeckoLib animation loader).
- Update Kruemblegård boss model UVs/armor geometry and refresh the entity texture.
- Workflow: clarify that “push” includes stage+commit+push when the working tree is dirty.
- Further tweak Kruemblegård boss geo UV mappings/armor pieces.
- Update Kruemblegård boss GeckoLib animation definitions.

