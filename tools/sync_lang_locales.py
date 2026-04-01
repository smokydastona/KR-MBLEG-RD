from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path


SUPPORTED_LOCALES = [
    "af_za",
    "ar_sa",
    "ast_es",
    "az_az",
    "ba_ru",
    "bar",
    "be_by",
    "bg_bg",
    "br_fr",
    "brb",
    "bs_ba",
    "ca_es",
    "cs_cz",
    "cy_gb",
    "da_dk",
    "de_at",
    "de_ch",
    "de_de",
    "el_gr",
    "en_au",
    "en_ca",
    "en_gb",
    "en_nz",
    "en_pt",
    "en_ud",
    "en_us",
    "enp",
    "enws",
    "eo_uy",
    "es_ar",
    "es_cl",
    "es_ec",
    "es_es",
    "es_mx",
    "es_uy",
    "es_ve",
    "esan",
    "et_ee",
    "eu_es",
    "fa_ir",
    "fi_fi",
    "fil_ph",
    "fo_fo",
    "fr_ca",
    "fr_fr",
    "fra_de",
    "fur_it",
    "fy_nl",
    "ga_ie",
    "gd_gb",
    "gl_es",
    "haw_us",
    "he_il",
    "hi_in",
    "hr_hr",
    "hu_hu",
    "hy_am",
    "id_id",
    "ig_ng",
    "io_en",
    "is_is",
    "isv",
    "it_it",
    "ja_jp",
    "jbo_en",
    "ka_ge",
    "kk_kz",
    "kn_in",
    "ko_kr",
    "ksh",
    "kw_gb",
    "la_la",
    "lb_lu",
    "li_li",
    "lmo",
    "lo_la",
    "lol_us",
    "lt_lt",
    "lv_lv",
    "lzh",
    "mk_mk",
    "mn_mn",
    "ms_my",
    "mt_mt",
    "nah",
    "nds_de",
    "nl_be",
    "nl_nl",
    "nn_no",
    "no_no",
    "oc_fr",
    "ovd",
    "pl_pl",
    "pt_br",
    "pt_pt",
    "qya_aa",
    "ro_ro",
    "rpr",
    "ru_ru",
    "ry_ua",
    "sah_sah",
    "se_no",
    "sk_sk",
    "sl_si",
    "so_so",
    "sq_al",
    "sr_cs",
    "sr_sp",
    "sv_se",
    "sxu",
    "szl",
    "ta_in",
    "th_th",
    "tl_ph",
    "tlh_aa",
    "tok",
    "tr_tr",
    "tt_ru",
    "uk_ua",
    "val_es",
    "vec_it",
    "vi_vn",
    "yi_de",
    "yo_ng",
    "zh_cn",
    "zh_hk",
    "zh_tw",
    "zlm_arab",
]


REPO_ROOT = Path(__file__).resolve().parents[1]
LANG_DIR = REPO_ROOT / "src" / "main" / "resources" / "assets" / "kruemblegard" / "lang"
SOURCE_PATH = LANG_DIR / "en_us.json"


def load_json(path: Path) -> dict[str, str]:
    return json.loads(path.read_text(encoding="utf-8"))


def dump_json(data: dict[str, str]) -> str:
    return json.dumps(data, ensure_ascii=False, indent=4, sort_keys=True) + "\n"


def locale_path(locale_code: str) -> Path:
    return LANG_DIR / f"{locale_code}.json"


def normalized_locale_code(locale_code: str) -> str:
    return locale_code.lower().replace("-", "_")


def normalize_supported_locale_files() -> int:
    renamed = 0
    for path in sorted(LANG_DIR.glob("*.json")):
        normalized_code = normalized_locale_code(path.stem)
        if normalized_code not in SUPPORTED_LOCALES:
            continue

        target = locale_path(normalized_code)
        if path == target:
            continue
        if target.exists():
            continue

        if path.name.lower() == target.name.lower():
            temp = path.with_name(f".__normalize__.{path.name}")
            path.replace(temp)
            temp.replace(target)
        else:
            path.replace(target)
        renamed += 1

    return renamed


def existing_locale_codes() -> set[str]:
    return {path.stem for path in LANG_DIR.glob("*.json")}


def merge_locale(source: dict[str, str], current: dict[str, str]) -> dict[str, str]:
    merged: dict[str, str] = {}
    for key, fallback_value in source.items():
        translated_value = current.get(key)
        if isinstance(translated_value, str) and translated_value:
            merged[key] = translated_value
        else:
            merged[key] = fallback_value
    return merged


def expected_locale_output(locale_code: str, source: dict[str, str], current: dict[str, str]) -> str:
    merged = source if locale_code == "en_us" else merge_locale(source, current)
    return dump_json(merged)


def sync_locales() -> int:
    if not SOURCE_PATH.exists():
        raise FileNotFoundError(f"Missing source language file: {SOURCE_PATH}")

    normalized = normalize_supported_locale_files()
    source = load_json(SOURCE_PATH)
    created = 0
    updated = 0

    for locale_code in SUPPORTED_LOCALES:
        path = locale_path(locale_code)
        current = load_json(path) if path.exists() else {}
        output = expected_locale_output(locale_code, source, current)
        existing_text = path.read_text(encoding="utf-8") if path.exists() else None
        if existing_text != output:
            path.write_text(output, encoding="utf-8", newline="\n")
            if existing_text is None:
                created += 1
            else:
                updated += 1

    extra_codes = sorted(existing_locale_codes() - set(SUPPORTED_LOCALES))
    print(f"Synced {len(SUPPORTED_LOCALES)} locale files from {SOURCE_PATH.name}.")
    if normalized:
        print(f"Normalized locale filenames: {normalized}")
    print(f"Created: {created} | Updated: {updated}")
    if extra_codes:
        print("Unsupported extra locale files detected:")
        for locale_code in extra_codes:
            print(f"- {locale_code}.json")
    return 0


def verify_locales() -> int:
    if not SOURCE_PATH.exists():
        raise FileNotFoundError(f"Missing source language file: {SOURCE_PATH}")

    normalize_supported_locale_files()
    source = load_json(SOURCE_PATH)
    missing = []
    invalid = []
    key_mismatches = []
    rewrite_needed = []

    source_keys = list(source.keys())

    for locale_code in SUPPORTED_LOCALES:
        path = locale_path(locale_code)
        if not path.exists():
            missing.append(locale_code)
            continue
        try:
            locale_data = load_json(path)
        except json.JSONDecodeError:
            invalid.append(locale_code)
            continue

        locale_keys = list(locale_data.keys())
        if locale_keys != source_keys:
            key_mismatches.append(locale_code)
            continue

        expected_output = expected_locale_output(locale_code, source, locale_data)
        existing_text = path.read_text(encoding="utf-8")
        if existing_text != expected_output:
            rewrite_needed.append(locale_code)

    extras = sorted(existing_locale_codes() - set(SUPPORTED_LOCALES))

    if not missing and not invalid and not key_mismatches and not rewrite_needed and not extras:
        print(f"Verified {len(SUPPORTED_LOCALES)} locale files; all are present and in clean sync with {SOURCE_PATH.name}.")
        return 0

    if missing:
        print("Missing locale files:")
        for locale_code in missing:
            print(f"- {locale_code}.json")
    if invalid:
        print("Invalid JSON locale files:")
        for locale_code in invalid:
            print(f"- {locale_code}.json")
    if key_mismatches:
        print("Locale files with stale or mismatched key sets:")
        for locale_code in key_mismatches:
            print(f"- {locale_code}.json")
    if rewrite_needed:
        print("Locale files that would be rewritten by sync_locales():")
        for locale_code in rewrite_needed:
            print(f"- {locale_code}.json")
    if extras:
        print("Unsupported extra locale files:")
        for locale_code in extras:
            print(f"- {locale_code}.json")
    return 1


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Keep Kruemblegard locale files aligned with en_us.json while preserving translated values and filling missing keys from English."
    )
    parser.add_argument(
        "--verify",
        action="store_true",
        help="Fail if any supported locale file is missing, invalid, or has a stale key set.",
    )
    args = parser.parse_args()
    return verify_locales() if args.verify else sync_locales()


if __name__ == "__main__":
    sys.exit(main())