# Contributing

## Asset sourcing
- Do **not** copy textures, models, sounds, music, or other assets from other mods.
- Even when code patterns are reusable, assets are often under different licenses.
- Prefer creating original assets or using assets you have explicit permission/license to use.

## Commits
- Prefer **Conventional Commits** for commit messages (see `.githooks/commit-msg`).

## Worldgen changes
- Keep worldgen data-driven under `src/main/resources/data/kruemblegard/worldgen/**`.
- Prefer tags under `data/kruemblegard/tags/worldgen/biome/**` to avoid hardcoded biome lists.
- If you add/rename worldgen IDs, update the corresponding keys in `src/main/java/com/kruemblegard/worldgen/ModWorldgenKeys.java`.

## Tools (maintenance scripts)

This repo contains PowerShell scripts under `tools/` used to audit and generate placeholder assets / reports.

Rules:
- Scripts (`tools/*.ps1`) are **tracked** in git; tool outputs/reports should generally **not** be committed.
- If you add a new script, ensure it is still relevant:
	- Has a clear name (what it audits/fixes/generates)
	- Has a short description at the top of the file
	- Produces output files in a predictable place (prefer `build/`)
- If a script becomes obsolete, broken, or duplicated, **delete it** rather than keeping dead weight.
- If you change asset/model conventions, update any affected audit scripts so they remain accurate.

Suggested hygiene pass (periodic):
- Run the commonly used audits and ensure they still work on a clean checkout.
- Remove scripts that no longer apply to current assets/worldgen.
- Keep doc generators in sync:
	- `tools/generate_material_bibles.ps1`
	- `tools/generate_sound_bible.ps1`
	- `tools/audit_sound_uniqueness.ps1`

## Adding a new mob (required: sounds + credits)

When adding a new entity mob, it must ship with its sound set from the start.

Checklist:
- Register the `SoundEvent`s in `src/main/java/com/kruemblegard/registry/ModSounds.java`.
- Add entries to `src/main/resources/assets/kruemblegard/sounds.json`.
- Add subtitles to `src/main/resources/assets/kruemblegard/lang/en_us.json`.
- Add the `.ogg` assets under `src/main/resources/assets/kruemblegard/sounds/entity/<mob>/`.
- Hook the entity to use them (ambient/hurt/death/step + any special one-shots).
- Add/extend attributions in `docs/SOUND_CREDITS.md` for every new sound clip used.
- Regenerate `docs/Sound_Bible.md`: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\generate_sound_bible.ps1`
- Run the uniqueness audit: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\audit_sound_uniqueness.ps1`
