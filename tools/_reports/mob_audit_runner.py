"""Krümblegård custom mob audit runner.

Writes a per-mob completeness report to tools/_reports/mob_audit_report.json.

This script is intended for repo-local QA and is safe to run repeatedly.
"""

from __future__ import annotations

import json
import re
from pathlib import Path


WS = Path(__file__).resolve().parents[2]
SRC = WS / "src" / "main"
JAVA = SRC / "java"
RES = SRC / "resources"

ASSETS = RES / "assets" / "kruemblegard"
DATA = RES / "data" / "kruemblegard"

MOD_ENTITIES = JAVA / "com" / "kruemblegard" / "registry" / "ModEntities.java"
CLIENT = JAVA / "com" / "kruemblegard" / "client" / "KruemblegardClient.java"
COMMON_EVENTS = JAVA / "com" / "kruemblegard" / "CommonModEvents.java"
LANG = ASSETS / "lang" / "en_us.json"
SOUNDS_JSON = ASSETS / "sounds.json"

LOOT_ENTITY_DIR = DATA / "loot_tables" / "entities"
BIOME_MOD_DIR = DATA / "forge" / "biome_modifier"

REPORT_PATH = WS / "tools" / "_reports" / "mob_audit_report.json"

OK = "✅"
WARN = "⚠️"
FAIL = "❌"

# Some mobs intentionally use vanilla systems (no custom sounds.json entries / loot tables).
VANILLA_SOUNDS_MOBS = {
    "cephalari_drowned",
    "cephalari_husk",
    "cephalari_golem",
    "cephalari_zombie",
    "driftskimmer",
    "echo_harness",
    "moogloom",
    "scattered_enderman",
    "spiral_strider",
    "treadwinder",
}

VANILLA_SOUND_SOURCES: dict[str, str] = {
    "cephalari_drowned": "vanilla Drowned",
    "cephalari_golem": "vanilla Iron Golem",
    "cephalari_husk": "vanilla Husk",
    "cephalari_zombie": "vanilla Zombie",
    "driftskimmer": "vanilla Cephalari (adult form)",
    "echo_harness": "vanilla Cephalari (adult form)",
    "spiral_strider": "vanilla Cephalari (adult form)",
    "treadwinder": "vanilla Cephalari (adult form)",
}

VANILLA_LOOT_MOBS = {
    "driftskimmer",
    "echo_harness",
    "scattered_enderman",
    "spiral_strider",
    "treadwinder",
}

# Some mobs intentionally share other mobs' content.
# NOTE: This is a doc/audit hint; it does not create/merge loot tables at runtime.
SHARED_LOOT_FROM: dict[str, str] = {
    "trader_beetle": "scaralon_beetle",
}

SHARED_SOUNDS_FROM: dict[str, str] = {
    "trader_beetle": "scaralon_beetle",
}

# Some mobs intentionally do not spawn via biome modifiers (structure/event-driven, commands only, etc.).
NO_BIOME_MOD_SPAWN_MOBS = {
    "cephalari_golem",
    "driftskimmer",
    "echo_harness",
    "spiral_strider",
    "treadwinder",
    "trader_beetle",
}

NON_BIOME_SPAWN_NOTES: dict[str, str] = {
    "cephalari_golem": "Spawns via village mechanics (replaces villager-spawned iron golems when Cephalari are nearby)",
    "driftskimmer": "Adult Cephalari form; spawned via Cephalari mechanics / commands (not biome-modifier-driven)",
    "echo_harness": "Adult Cephalari form; spawned via Cephalari mechanics / commands (not biome-modifier-driven)",
    "spiral_strider": "Adult Cephalari form; spawned via Cephalari mechanics / commands (not biome-modifier-driven)",
    "treadwinder": "Adult Cephalari form; spawned via Cephalari mechanics / commands (not biome-modifier-driven)",
}


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def read_json(path: Path):
    return json.loads(read_text(path))


def list_json(dir_path: Path):
    if not dir_path.exists():
        return []
    return sorted([p for p in dir_path.rglob("*.json") if p.is_file()])


def list_png(dir_path: Path):
    if not dir_path.exists():
        return []
    return sorted([p for p in dir_path.rglob("*.png") if p.is_file()])


def extract_entities(mod_entities_text: str):
    pat = re.compile(
        r"EntityType\s*<\s*([^>\s]+)\s*>\s*>\s*([A-Z0-9_]+)\s*=\s*ENTITIES\.register\(\s*\"([a-z0-9_]+)\"",
        re.M,
    )
    out = []
    for m in pat.finditer(mod_entities_text):
        class_name, const_name, ent_id = m.group(1), m.group(2), m.group(3)
        if ent_id.endswith("_boat") or ent_id.endswith("_chest_boat"):
            continue
        out.append({"id": ent_id, "const": const_name, "class": class_name.split(".")[-1]})

    seen = set()
    uniq = []
    for e in out:
        if e["id"] in seen:
            continue
        seen.add(e["id"])
        uniq.append(e)
    return uniq


def find_spawn_egg_ids(java_root: Path):
    eggs: dict[str, str] = {}
    for p in java_root.rglob("*.java"):
        try:
            t = read_text(p)
        except Exception:
            continue
        for m in re.finditer(r"\.register\(\s*\"([a-z0-9_]+_spawn_egg)\"", t):
            eggs[m.group(1)] = p.as_posix().replace(WS.as_posix() + "/", "")
    return eggs


def load_lang_keys():
    if not LANG.exists():
        return {}
    try:
        return read_json(LANG)
    except Exception:
        return {}


def load_sound_keys():
    if not SOUNDS_JSON.exists():
        return {}
    try:
        return read_json(SOUNDS_JSON)
    except Exception:
        return {}


def biome_modifier_refs(ent_id: str):
    refs = []
    for p in list_json(BIOME_MOD_DIR):
        try:
            t = read_text(p)
        except Exception:
            continue
        if f"kruemblegard:{ent_id}" in t:
            refs.append(p.as_posix().replace(WS.as_posix() + "/", ""))
    return refs


def has_spawn_placement(common_events_text: str, const: str):
    return f"ModEntities.{const}.get()" in common_events_text and "SpawnPlacementRegisterEvent" in common_events_text


def has_attribute_registration(common_events_text: str, const: str):
    return f"event.put(ModEntities.{const}.get()" in common_events_text


def status_from_bool(ok: bool, warn_ok: bool = False):
    return OK if ok else (WARN if warn_ok else FAIL)


def parse_client_renderers(client_text: str):
    mapping: dict[str, str] = {}
    for m in re.finditer(
        r"registerEntityRenderer\(\s*ModEntities\.([A-Z0-9_]+)\.get\(\)\s*,\s*([A-Za-z0-9_]+)::new\s*\)",
        client_text,
    ):
        mapping[m.group(1)] = m.group(2)
    for m in re.finditer(
        r"registerEntityRenderer\(\s*ModEntities\.([A-Z0-9_]+)\.get\(\)\s*,\s*ctx\s*->\s*new\s+([A-Za-z0-9_]+)",
        client_text,
    ):
        mapping[m.group(1)] = m.group(2)
    return mapping


def find_java_file_by_name(class_name: str | None):
    if not class_name:
        return None
    for p in JAVA.rglob(class_name + ".java"):
        return p
    return None


def renderer_to_model(renderer_text: str):
    if not renderer_text:
        return None
    m = re.search(r"super\([^,]+,\s*new\s+([A-Za-z0-9_]+)\s*\(", renderer_text)
    if m:
        return m.group(1)
    m = re.search(r"super\([^,]+,\s*new\s+([A-Za-z0-9_]+)\s*<", renderer_text)
    if m:
        return m.group(1)
    return None


def extract_asset_strings(text: str):
    if not text:
        return []
    strs = []
    for m in re.finditer(r"new\s+ResourceLocation\([^,]+,\s*\"([^\"]+)\"\s*\)", text):
        strs.append(m.group(1))
    for m in re.finditer(
        r"\"(geo\/[^\\\"]+?\.geo\.json|animations\/[^\\\"]+?\.animation\.json|textures\/[^\\\"]+)\"",
        text,
    ):
        strs.append(m.group(1))

    seen = set()
    out = []
    for s in strs:
        if s in seen:
            continue
        seen.add(s)
        out.append(s)
    return out


def resolve_assets(resource_strings):
    geo: list[Path] = []
    anim: list[Path] = []
    tex: list[Path] = []
    dyn_tex_prefixes: list[str] = []

    for s in resource_strings:
        if s.startswith("geo/") and s.endswith(".geo.json"):
            geo.append(ASSETS / s)
        elif s.startswith("animations/") and s.endswith(".animation.json"):
            anim.append(ASSETS / s)
        elif s.startswith("textures/"):
            if s.endswith(".png"):
                tex.append(ASSETS / s)
            else:
                dyn_tex_prefixes.append(s)

    for prefix in dyn_tex_prefixes:
        base = ASSETS / prefix
        parent = base.parent
        stem = base.name
        if parent.exists():
            for p in list_png(parent):
                if p.name.startswith(stem):
                    tex.append(p)

    def dedupe(paths):
        seen = set()
        out = []
        for p in paths:
            if p in seen:
                continue
            seen.add(p)
            out.append(p)
        return out

    return dedupe(geo), dedupe(anim), dedupe(tex)


def parse_geo_bones(geo_path: Path):
    try:
        data = read_json(geo_path)
    except Exception:
        return None, [f"Failed to parse geo JSON: {geo_path.name}"]

    bones = set()
    issues = []
    geos = data.get("minecraft:geometry")
    if not isinstance(geos, list) or not geos:
        return None, ["Geo missing minecraft:geometry list"]

    for g in geos:
        b = g.get("bones") if isinstance(g, dict) else None
        if isinstance(b, list):
            for bone in b:
                name = bone.get("name") if isinstance(bone, dict) else None
                if isinstance(name, str):
                    bones.add(name)
    if not bones:
        issues.append("No bones found in geo")
    return bones, issues


def parse_anim_bones(anim_path: Path):
    try:
        data = read_json(anim_path)
    except Exception:
        return None, [f"Failed to parse animation JSON: {anim_path.name}"]

    issues = []
    anims = data.get("animations")
    if not isinstance(anims, dict) or not anims:
        return None, ["Animation missing animations object"]

    referenced = set()
    for anim_name, anim_def in anims.items():
        if not isinstance(anim_def, dict):
            issues.append(f"Animation {anim_name} not an object")
            continue
        bones = anim_def.get("bones", {})
        if bones is None:
            continue
        if not isinstance(bones, dict):
            issues.append(f"Animation {anim_name} bones is not an object")
            continue
        for bone_name, bone_def in bones.items():
            if isinstance(bone_name, str):
                referenced.add(bone_name)
                if bone_name.startswith("animation."):
                    issues.append(
                        f"Animation {anim_name} has invalid bone key that looks like an animation name: {bone_name}"
                    )
            if isinstance(bone_def, dict) and "animation_length" in bone_def and "bones" in bone_def:
                issues.append(f"Animation {anim_name} has nested animation-like object under bone {bone_name}")
    return referenced, issues


def anim_presence_flags(anim_path: Path):
    try:
        data = read_json(anim_path)
    except Exception:
        return {}
    anims = data.get("animations", {})
    keys = list(anims.keys()) if isinstance(anims, dict) else []
    low = [k.lower() for k in keys]

    def has_any(substrs):
        return any(any(s in k for s in substrs) for k in low)

    return {
        "idle": has_any([".idle"]),
        "move": has_any([".walk", ".move", ".run", ".swim", ".fly"]),
        "attack": has_any([".attack", ".bite", ".slam", ".strike", ".peck", ".hit"]),
        "death": has_any([".death", ".die"]),
        "special": has_any([".special", ".display", ".roar", ".charge", ".emerge"]),
    }


def compute_score(fields):
    score = 0
    score += 15 if fields["geo_ok"] else 0
    score += 15 if fields["anim_ok"] else 0
    score += 15 if fields["tex_ok"] else 0
    score += 10 if fields["renderer_ok"] else 0
    score += 10 if fields["attributes_ok"] else 0
    score += 5 if fields["spawn_placement_ok"] else 0
    score += 10 if fields["loot_ok"] else 0
    score += 5 if fields["lang_ok"] else 0
    score += 5 if fields["biome_spawn_ok"] else 0
    score += 5 if fields["sounds_ok"] else 0
    score += 5 if fields["spawn_egg_ok"] else 0
    return score


def main() -> int:
    entities_text = read_text(MOD_ENTITIES)
    client_text = read_text(CLIENT) if CLIENT.exists() else ""
    common_text = read_text(COMMON_EVENTS) if COMMON_EVENTS.exists() else ""

    mobs = extract_entities(entities_text)
    lang = load_lang_keys()
    sounds = load_sound_keys()
    spawn_eggs = find_spawn_egg_ids(JAVA)

    client_map = parse_client_renderers(client_text)

    report = {
        "schemaVersion": 9,
        "modid": "kruemblegard",
        "mobCount": len(mobs),
        "generatedBy": "tools/_reports/mob_audit_runner.py",
        "mobs": [],
    }

    for m in mobs:
        ent_id = m["id"]
        const = m["const"]
        loot_path = LOOT_ENTITY_DIR / f"{ent_id}.json"

        renderer_class = client_map.get(const)
        renderer_path = find_java_file_by_name(renderer_class)
        renderer_text = read_text(renderer_path) if renderer_path and renderer_path.exists() else ""
        model_class = renderer_to_model(renderer_text)
        model_path = find_java_file_by_name(model_class)
        model_text = read_text(model_path) if model_path and model_path.exists() else ""

        resource_strings = extract_asset_strings(renderer_text) + extract_asset_strings(model_text)
        seen = set()
        rs = []
        for s in resource_strings:
            if s in seen:
                continue
            seen.add(s)
            rs.append(s)

        geo_files, anim_files, tex_files = resolve_assets(rs)

        if not geo_files:
            fb = ASSETS / "geo" / f"{ent_id}.geo.json"
            if fb.exists():
                geo_files = [fb]
        if not anim_files:
            fb = ASSETS / "animations" / f"{ent_id}.animation.json"
            if fb.exists():
                anim_files = [fb]
        if not tex_files:
            f1 = ASSETS / "textures" / "entity" / f"{ent_id}.png"
            if f1.exists():
                tex_files = [f1]
            else:
                d = ASSETS / "textures" / "entity" / ent_id
                if d.exists():
                    tex_files = list_png(d)
                elif ent_id.startswith("cephalari"):
                    d2 = ASSETS / "textures" / "entity" / "cephalari"
                    if d2.exists():
                        tex_files = list_png(d2)

        geo_ok = any(p.exists() for p in geo_files)
        anim_ok = any(p.exists() for p in anim_files)
        tex_ok = any(p.exists() for p in tex_files)
        vanilla_loot = ent_id in VANILLA_LOOT_MOBS
        shared_loot_from = SHARED_LOOT_FROM.get(ent_id)
        loot_ok = loot_path.exists() or vanilla_loot or (shared_loot_from is not None)

        renderer_ok = renderer_class is not None
        attributes_ok = has_attribute_registration(common_text, const)
        spawn_place_ok = has_spawn_placement(common_text, const)

        biome_refs = biome_modifier_refs(ent_id)
        biome_spawn_ok = len(biome_refs) > 0
        if ent_id in NO_BIOME_MOD_SPAWN_MOBS:
            biome_spawn_ok = True

        lang_entity_key = f"entity.kruemblegard.{ent_id}"
        egg_id = f"{ent_id}_spawn_egg"
        has_spawn_egg = egg_id in spawn_eggs

        lang_ok = lang_entity_key in lang
        if has_spawn_egg and (f"item.kruemblegard.{egg_id}" not in lang):
            lang_ok = False

        vanilla_sounds = ent_id in VANILLA_SOUNDS_MOBS
        shared_sounds_from = SHARED_SOUNDS_FROM.get(ent_id)
        sounds_ok = vanilla_sounds or (shared_sounds_from is not None)
        sound_notes = []
        if (not vanilla_sounds) and (shared_sounds_from is None) and isinstance(sounds, dict):
            for k in sounds.keys():
                if ent_id in k:
                    sounds_ok = True
                    break

        if vanilla_sounds:
            src = VANILLA_SOUND_SOURCES.get(ent_id)
            if src:
                sound_notes.append(f"Uses {src} sounds")
            else:
                sound_notes.append("Uses vanilla sounds (no sounds.json entries expected)")
        elif shared_sounds_from is not None:
            sound_notes.append(f"Uses {shared_sounds_from} sounds (shared)")
        elif not sounds_ok:
            sound_notes.append("No sounds.json entries matched this mob id (may rely on vanilla sounds)")

        bone_notes = []
        anim_struct_notes = []
        anim_flags = {}
        bone_mismatch_count = 0
        wildcard_used = False

        geo_first = next((p for p in geo_files if p.exists()), None)
        anim_first = next((p for p in anim_files if p.exists()), None)

        geo_bones = None
        anim_bones = None

        if geo_first:
            geo_bones, geo_issues = parse_geo_bones(geo_first)
            if geo_issues:
                bone_notes.extend(geo_issues)

        if anim_first:
            anim_bones, anim_issues = parse_anim_bones(anim_first)
            if anim_issues:
                anim_struct_notes.extend(anim_issues)
            anim_flags = anim_presence_flags(anim_first)

        if geo_bones is not None and anim_bones is not None:
            for b in anim_bones:
                if b == "*":
                    wildcard_used = True
                    continue
                if b in ("root", "base", "body", "player", "locator"):
                    continue
                if b not in geo_bones:
                    bone_mismatch_count += 1
            if bone_mismatch_count:
                bone_notes.append(
                    f"{bone_mismatch_count} animation bone names not found in geo bones (may include controller-only bones)"
                )
            if wildcard_used:
                bone_notes.append('Animation uses "*" wildcard bone (verify GeckoLib compatibility for this file)')

        geometry_status = status_from_bool(geo_ok)
        texture_status = status_from_bool(tex_ok)
        animation_status = FAIL
        if anim_ok and not anim_struct_notes:
            animation_status = OK if (anim_flags.get("idle") and anim_flags.get("move")) else WARN

        spawn_status = OK if biome_spawn_ok else WARN
        if ent_id == "kruemblegard":
            spawn_status = OK

        sounds_status = OK if sounds_ok else WARN

        spawn_egg_status = OK if has_spawn_egg else WARN
        stability_status = WARN if anim_struct_notes else OK
        stability_notes = ["Animation JSON structural issues can crash/disable animations"] if anim_struct_notes else []

        score = compute_score(
            {
                "geo_ok": geo_ok,
                "anim_ok": anim_ok and not anim_struct_notes,
                "tex_ok": tex_ok,
                "renderer_ok": renderer_ok,
                "attributes_ok": attributes_ok,
                "spawn_placement_ok": spawn_place_ok,
                "loot_ok": loot_ok,
                "lang_ok": lang_ok,
                "biome_spawn_ok": biome_spawn_ok or ent_id == "kruemblegard",
                "sounds_ok": sounds_ok,
                "spawn_egg_ok": has_spawn_egg,
            }
        )

        mob_entry = {
            "id": ent_id,
            "entityClass": m["class"],
            "deferredRegisterConst": const,
            "rendererClass": renderer_class,
            "modelClass": model_class,
            "score100": score,
            "status": {
                "geometry": geometry_status,
                "textures": texture_status,
                "animations": animation_status,
                "ai_behavior": OK if attributes_ok else WARN,
                "loot": OK if loot_ok else WARN,
                "sounds_effects": sounds_status,
                "spawning": spawn_status,
                "spawn_egg": spawn_egg_status,
                "integration": OK if (renderer_ok and attributes_ok) else WARN,
                "stability_perf": stability_status,
            },
            "files": {
                "geo": [p.as_posix().replace(WS.as_posix() + "/", "") for p in geo_files if p.exists()],
                "animation": [p.as_posix().replace(WS.as_posix() + "/", "") for p in anim_files if p.exists()],
                "textures": [p.as_posix().replace(WS.as_posix() + "/", "") for p in tex_files if p.exists()][:30],
                "lootTable": (
                    loot_path.as_posix().replace(WS.as_posix() + "/", "")
                    if loot_path.exists()
                    else (
                        "(vanilla)"
                        if vanilla_loot
                        else (
                            f"(shared: {shared_loot_from})"
                            if shared_loot_from is not None
                            else None
                        )
                    )
                ),
                "biomeModifiers": biome_refs,
                "langKeys": {
                    "entity": lang_entity_key,
                    "spawnEgg": f"item.kruemblegard.{egg_id}" if has_spawn_egg else None,
                },
            },
            "checks": {
                "rendererRegistered": renderer_ok,
                "attributesRegistered": attributes_ok,
                "spawnPlacementRegistered": spawn_place_ok,
                "hasSpawnEggItemId": has_spawn_egg,
                "animationKeyFlags": anim_flags,
                "animationIssues": anim_struct_notes,
                "geoAnimBoneNotes": bone_notes,
                "stabilityNotes": stability_notes,
                "soundNotes": sound_notes,
            },
            "notes": [],
        }

        if not loot_ok and ent_id != "kruemblegard":
            mob_entry["notes"].append("No entity loot table found under data/.../loot_tables/entities/")
        if shared_loot_from is not None:
            mob_entry["notes"].append(f"Loot is shared from {shared_loot_from}")
        if vanilla_loot:
            mob_entry["notes"].append("Uses vanilla loot (no custom entity loot table expected)")
        if anim_struct_notes:
            mob_entry["notes"].append("Fix animation JSON structure issues")
        if not (biome_spawn_ok or ent_id == "kruemblegard"):
            mob_entry["notes"].append("No biome modifier references found (may not spawn naturally)")
        if ent_id in NO_BIOME_MOD_SPAWN_MOBS:
            mob_entry["notes"].append("Spawns are not biome-modifier-driven (intentional)")
            extra = NON_BIOME_SPAWN_NOTES.get(ent_id)
            if extra:
                mob_entry["notes"].append(extra)
        if has_spawn_egg and f"item.kruemblegard.{egg_id}" not in lang:
            mob_entry["notes"].append("Spawn egg is registered but missing lang key")
        if lang_entity_key not in lang:
            mob_entry["notes"].append("Missing entity lang key")

        report["mobs"].append(mob_entry)

    report["mobs"].sort(key=lambda x: (x["score100"], x["id"]))

    REPORT_PATH.write_text(json.dumps(report, indent=2), encoding="utf-8")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
