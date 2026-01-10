# Tools

This folder contains project maintenance scripts.

Tracked:
- `tools/*.ps1` scripts
- This `tools/README.md`

Not tracked:
- Generated reports/CSVs/text outputs produced by scripts (these should go to `build/` when possible).

## Keep tools relevant

- If a script is obsolete or duplicated, delete it.
- If asset/worldgen conventions change, update the affected scripts.
- Prefer scripts that are deterministic and safe to run multiple times.

## Doc generators

- `tools/generate_material_bibles.ps1` updates:
  - `docs/Block_Material_Bible.md`
  - `docs/Item_Material_Bible.md`

- `tools/generate_sound_bible.ps1` updates:
  - `docs/Sound_Bible.md`

- `tools/generate_loot_table_bible.ps1` updates:
  - `docs/Loot_Table_Bible.md`
