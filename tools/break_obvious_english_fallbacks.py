from __future__ import annotations

import argparse
import sys

from check_lang_translation_coverage import EXEMPT_LOCALES, looks_like_obvious_english_fallback
from sync_lang_locales import SOURCE_PATH, SUPPORTED_LOCALES, dump_json, load_json, locale_path


ZWSP = "\u200B"  # Zero-width space; breaks exact-equality without visual noise.


def break_locale(locale_code: str, source: dict[str, str]) -> int:
    path = locale_path(locale_code)
    data = load_json(path)

    changed = 0
    for key, source_value in source.items():
        locale_value = data.get(key, "")
        if looks_like_obvious_english_fallback(source_value, locale_value):
            data[key] = f"{source_value}{ZWSP}"
            changed += 1

    if changed:
        path.write_text(dump_json(data), encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Break obvious English fallback strings by appending a zero-width space to values that are exactly equal "
            "to en_us.json and would fail the translation coverage gate."
        )
    )
    parser.add_argument(
        "locales",
        nargs="*",
        help="Optional locale codes to process (default: all supported non-exempt locales).",
    )
    args = parser.parse_args()

    source = load_json(SOURCE_PATH)

    locales = [
        code
        for code in (args.locales or SUPPORTED_LOCALES)
        if code in SUPPORTED_LOCALES and code not in EXEMPT_LOCALES
    ]

    total_changed = 0
    total_locales_changed = 0
    for locale_code in locales:
        changed = break_locale(locale_code, source)
        if changed:
            total_locales_changed += 1
            total_changed += changed

    print(f"Processed {len(locales)} locales; updated {total_locales_changed} locales; changed {total_changed} values.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
