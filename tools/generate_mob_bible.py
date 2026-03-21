from __future__ import annotations

import json
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Any, Iterable


REPO_ROOT = Path(__file__).resolve().parents[1]
REPORT_PATH = REPO_ROOT / "tools" / "_reports" / "mob_audit_report.json"
OUT_PATH = REPO_ROOT / "docs" / "mob_bible.md"

START_MARKER = "<!-- AUTO-GENERATED:MOBS:START -->"
END_MARKER = "<!-- AUTO-GENERATED:MOBS:END -->"


@dataclass(frozen=True)
class MobRow:
    mob_id: str
    score100: int
    status: dict[str, str]
    entity_class: str | None
    renderer_class: str | None
    model_class: str | None
    geo_files: list[str]
    animation_files: list[str]
    texture_files: list[str]
    loot_table: str | None
    biome_modifiers: list[str]
    lang_entity_key: str | None
    lang_spawn_egg_key: str | None
    notes: list[str]
    sound_notes: list[str]
    stability_notes: list[str]
    animation_issues: list[str]
    geo_anim_bone_notes: list[str]


def _load_json(path: Path) -> dict[str, Any]:
    # utf-8-sig handles BOM if present
    return json.loads(path.read_text(encoding="utf-8-sig"))


def _as_list(value: Any) -> list[str]:
    if value is None:
        return []
    if isinstance(value, list):
        return [str(x) for x in value]
    return [str(value)]


def _to_mob_rows(report: dict[str, Any]) -> list[MobRow]:
    rows: list[MobRow] = []
    for m in report.get("mobs", []):
        files = m.get("files", {}) or {}
        checks = m.get("checks", {}) or {}
        lang = files.get("langKeys", {}) or {}

        rows.append(
            MobRow(
                mob_id=str(m.get("id")),
                score100=int(m.get("score100", 0)),
                status=dict(m.get("status", {}) or {}),
                entity_class=m.get("entityClass"),
                renderer_class=m.get("rendererClass"),
                model_class=m.get("modelClass"),
                geo_files=_as_list(files.get("geo")),
                animation_files=_as_list(files.get("animation")),
                texture_files=_as_list(files.get("textures")),
                loot_table=files.get("lootTable"),
                biome_modifiers=_as_list(files.get("biomeModifiers")),
                lang_entity_key=lang.get("entity"),
                lang_spawn_egg_key=lang.get("spawnEgg"),
                notes=_as_list(m.get("notes")),
                sound_notes=_as_list(checks.get("soundNotes")),
                stability_notes=_as_list(checks.get("stabilityNotes")),
                animation_issues=_as_list(checks.get("animationIssues")),
                geo_anim_bone_notes=_as_list(checks.get("geoAnimBoneNotes")),
            )
        )

    rows.sort(key=lambda r: r.mob_id)
    return rows


def _fmt_list_inline(items: Iterable[str]) -> str:
    items = list(items)
    if not items:
        return "(none)"
    return ", ".join(f"`{x}`" for x in items)


def _bucket(rows: list[MobRow], status_key: str, icon: str) -> list[str]:
    return [r.mob_id for r in rows if r.status.get(status_key) == icon]


def _render_summary(report: dict[str, Any], rows: list[MobRow]) -> str:
    schema = report.get("schemaVersion")
    mob_count = report.get("mobCount")
    generated_by = report.get("generatedBy")

    failures = [r for r in rows if r.status.get("animations") == "❌" or r.status.get("stability_perf") == "❌"]

    missing_loot = _bucket(rows, "loot", "⚠️")
    missing_spawns = _bucket(rows, "spawning", "⚠️")
    missing_sounds = _bucket(rows, "sounds_effects", "⚠️")
    missing_eggs = _bucket(rows, "spawn_egg", "⚠️")
    anim_warn = _bucket(rows, "animations", "⚠️")

    lines: list[str] = []
    lines.append(f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    lines.append("")
    lines.append("### Report Metadata")
    lines.append(f"- Source: `{REPORT_PATH.as_posix()}`")
    lines.append(f"- schemaVersion: `{schema}`")
    lines.append(f"- mobCount: `{mob_count}`")
    lines.append(f"- generatedBy: `{generated_by}`")
    lines.append("")

    lines.append("### Backlog (from audit)")
    lines.append(f"- Missing entity loot tables: {len(missing_loot)}")
    lines.append(f"  - {_fmt_list_inline(missing_loot)}")
    lines.append(f"- No biome modifier refs (may not spawn naturally): {len(missing_spawns)}")
    lines.append(f"  - {_fmt_list_inline(missing_spawns)}")
    lines.append(f"- No sounds.json matches (may rely on vanilla sounds): {len(missing_sounds)}")
    lines.append(f"  - {_fmt_list_inline(missing_sounds)}")
    lines.append(f"- Missing spawn eggs: {len(missing_eggs)}")
    lines.append(f"  - {_fmt_list_inline(missing_eggs)}")
    lines.append(f"- Animation warnings (non-fatal): {len(anim_warn)}")
    lines.append(f"  - {_fmt_list_inline(anim_warn)}")
    lines.append("")

    lines.append("### Confirmed Failures")
    if not failures:
        lines.append("- (none)")
    else:
        for r in failures:
            issues = r.animation_issues or r.notes
            issue_text = "; ".join(issues) if issues else "(no details in report)"
            anim_files = ", ".join(r.animation_files) if r.animation_files else "(none)"
            lines.append(f"- `{r.mob_id}`: animations={r.status.get('animations')} stability_perf={r.status.get('stability_perf')} | {issue_text}")
            lines.append(f"  - Animation file(s): `{anim_files}`")
    lines.append("")

    return "\n".join(lines)


def _render_mob_table(rows: list[MobRow]) -> str:
    headers = [
        "Mob",
        "Score",
        "Geo",
        "Tex",
        "Anim",
        "AI",
        "Loot",
        "Sounds",
        "Spawns",
        "Egg",
        "Integr",
        "Perf",
    ]

    lines: list[str] = []
    lines.append("### Roster Status Table")
    lines.append("")
    lines.append("| " + " | ".join(headers) + " |")
    lines.append("| " + " | ".join(["---"] * len(headers)) + " |")
    for r in rows:
        s = r.status
        lines.append(
            "| "
            + " | ".join(
                [
                    f"`{r.mob_id}`",
                    str(r.score100),
                    s.get("geometry", "?"),
                    s.get("textures", "?"),
                    s.get("animations", "?"),
                    s.get("ai_behavior", "?"),
                    s.get("loot", "?"),
                    s.get("sounds_effects", "?"),
                    s.get("spawning", "?"),
                    s.get("spawn_egg", "?"),
                    s.get("integration", "?"),
                    s.get("stability_perf", "?"),
                ]
            )
            + " |"
        )

    lines.append("")
    return "\n".join(lines)


def _render_per_mob(rows: list[MobRow]) -> str:
    lines: list[str] = []
    lines.append("### Per-Mob Detail")
    lines.append("")

    for r in rows:
        s = r.status
        lines.append(f"#### `{r.mob_id}`")
        lines.append(f"- Score: `{r.score100}`")
        lines.append(
            "- Status: "
            + " ".join(
                [
                    f"geo={s.get('geometry','?')}",
                    f"tex={s.get('textures','?')}",
                    f"anim={s.get('animations','?')}",
                    f"ai={s.get('ai_behavior','?')}",
                    f"loot={s.get('loot','?')}",
                    f"sounds={s.get('sounds_effects','?')}",
                    f"spawns={s.get('spawning','?')}",
                    f"egg={s.get('spawn_egg','?')}",
                    f"integration={s.get('integration','?')}",
                    f"perf={s.get('stability_perf','?')}"
                ]
            )
        )

        if r.entity_class or r.renderer_class or r.model_class:
            parts = []
            if r.entity_class:
                parts.append(f"Entity: `{r.entity_class}`")
            if r.renderer_class:
                parts.append(f"Renderer: `{r.renderer_class}`")
            if r.model_class:
                parts.append(f"Model: `{r.model_class}`")
            lines.append(f"- Classes: " + " | ".join(parts))

        lines.append(f"- Geo: {_fmt_list_inline(r.geo_files)}")
        lines.append(f"- Animations: {_fmt_list_inline(r.animation_files)}")
        lines.append(f"- Textures ({len(r.texture_files)}): " + (_fmt_list_inline(r.texture_files) if r.texture_files else "(none)"))

        lines.append(f"- Loot table: {('`'+r.loot_table+'`') if r.loot_table else '**MISSING**'}")
        if r.biome_modifiers:
            lines.append(f"- Biome modifiers: {_fmt_list_inline(r.biome_modifiers)}")
        else:
            lines.append("- Biome modifiers: **NONE FOUND**")

        if r.lang_entity_key or r.lang_spawn_egg_key:
            lines.append(
                "- Lang keys: "
                + " ".join(
                    [
                        f"entity={('`'+r.lang_entity_key+'`') if r.lang_entity_key else '(missing)'}",
                        f"spawnEgg={('`'+r.lang_spawn_egg_key+'`') if r.lang_spawn_egg_key else '(missing)'}",
                    ]
                )
            )

        todo: list[str] = []
        if s.get("animations") == "❌":
            todo.append("Fix broken animations")
        if s.get("loot") == "⚠️":
            todo.append("Add entity loot table")
        if s.get("spawning") == "⚠️":
            todo.append("Add biome modifier (if should spawn naturally)")
        if s.get("spawn_egg") == "⚠️":
            todo.append("Add spawn egg item")
        if s.get("sounds_effects") == "⚠️":
            todo.append("Confirm vanilla sounds or add custom sounds")
        if s.get("stability_perf") == "⚠️":
            todo.append("Resolve stability/perf notes")
        if todo:
            lines.append("- TODO: " + "; ".join(todo))

        extra_notes = []
        extra_notes.extend(r.animation_issues)
        extra_notes.extend(r.geo_anim_bone_notes)
        extra_notes.extend(r.sound_notes)
        extra_notes.extend(r.stability_notes)
        extra_notes.extend(r.notes)
        if extra_notes:
            lines.append("- Audit notes:")
            for n in extra_notes:
                lines.append(f"  - {n}")

        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


def _render_body(report: dict[str, Any]) -> str:
    rows = _to_mob_rows(report)
    parts = [
        _render_summary(report, rows),
        _render_mob_table(rows),
        _render_per_mob(rows),
    ]
    return "\n\n".join(p.strip() for p in parts if p.strip())


def _replace_section(text: str, body: str) -> str:
    start = text.find(START_MARKER)
    if start < 0:
        raise RuntimeError(f"Start marker not found: {START_MARKER}")

    end = text.find(END_MARKER)
    if end < 0:
        raise RuntimeError(f"End marker not found: {END_MARKER}")

    if end < start:
        raise RuntimeError("End marker appears before start marker")

    before = text[: start + len(START_MARKER)]
    after = text[end:]
    return before + "\n\n" + body.rstrip() + "\n\n" + after


def main() -> None:
    report = _load_json(REPORT_PATH)
    body = _render_body(report)

    doc_text = OUT_PATH.read_text(encoding="utf-8")
    updated = _replace_section(doc_text, body)
    OUT_PATH.write_text(updated, encoding="utf-8")


if __name__ == "__main__":
    main()
