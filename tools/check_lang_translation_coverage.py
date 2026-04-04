from __future__ import annotations

import argparse
import re
import sys

from sync_lang_locales import SOURCE_PATH, SUPPORTED_LOCALES, load_json, locale_path


EXEMPT_LOCALES = {
    "en_au",
    "en_ca",
    "en_gb",
    "en_nz",
    "en_pt",
    "en_ud",
    "en_us",
    "enp",
    "enws",
    "jbo_en",
    "lol_us",
    "qya_aa",
    "tlh_aa",
    "tok",
}

ZERO_WIDTH_SPACE = "\u200B"

PROTECTED_TERMS = {
    "krümblegård",
    "cephalari",
    "wayfall",
    "telekinesis",
    "moogloom",
    "scaralon",
    "wyrdwing",
    "driftwhale",
    "pebble wren",
    "mossback tortoise",
    "grave cairn",
    "trader beetle",
    "spiral strider",
    "drift skimmer",
    "treadwinder",
    "echo harness",
    "ashbloom",
    "ashmoss",
    "ashspire",
    "cairnroot",
    "echocap",
    "echowood",
    "driftwood",
    "driftwillow",
    "paleweft",
    "remnant",
    "runegrowth",
    "pebblit",
    "traprock",
    "waystone",
    "waystones",
    "scarsteel",
}

# Optional stricter gate: require specific mod term display-name values to be translated.
# This is intentionally value-based (matches exact en_us values) rather than substring-based,
# so it targets name keys like entity/block/item display names without overreaching into sentences.
REQUIRED_TRANSLATED_SOURCE_VALUES = {
    "Cephalari",
    "Wayfall",
    "Telekinesis",
    "Moogloom",
    "Scaralon",
    "Wyrdwing",
    "Driftwhale",
    "Pebble Wren",
    "Mossback Tortoise",
    "Grave Cairn",
    "Trader Beetle",
    "Spiral Strider",
    "Drift Skimmer",
    "Treadwinder",
    "Echo Harness",
    "Ashbloom",
    "Ashmoss",
    "Ashspire",
    "Cairnroot",
    "Echocap",
    "Echowood",
    "Driftwood",
    "Driftwillow",
    "Faultwood",
    "Paleweft",
    "Remnant",
    "Runegrowth",
    "Pebblit",
    "Traprock",
    "Waystone",
    "Waystones",
    "Scarsteel",
}


COMPOUND_SUFFIXES = [
    "stones",
    "stone",
    "wood",
    "moss",
    "bloom",
    "spire",
    "root",
    "cap",
    "steel",
    "fall",
    "wing",
    "whale",
    "winder",
    "skimmer",
    "strider",
    "beetle",
    "tortoise",
    "wren",
    "harness",
    "growth",
    "willow",
    "weft",
    "rock",
]


ENGLISH_REQUIRED_TERM_SEEDS = {
    # Must match the translation-input policy used by tools/translate_lang_locales.py.
    "Cephalari": "squid people",
    "Moogloom": "sad mushroom cow",
    "Scaralon": "serious",
    "Wyrdwing": "wierd wing",
    "Pebblit": "rock child",
    "Telekinesis": "mind movement",
}


def split_compound_term(term: str) -> str | None:
    if " " in term:
        return None
    if not term.isalpha():
        return None
    if not term.isascii():
        return None

    lower = term.lower()
    for suffix in sorted(COMPOUND_SUFFIXES, key=len, reverse=True):
        if not lower.endswith(suffix):
            continue
        split_at = len(term) - len(suffix)
        if split_at < 3 or split_at >= len(term) - 2:
            continue
        left = term[:split_at]
        right = term[split_at:]
        return f"{left} {right}"
    return None


def english_seed_for_required_term(source_value: str) -> str:
    seeded = ENGLISH_REQUIRED_TERM_SEEDS.get(source_value)
    if seeded:
        return seeded
    hinted = split_compound_term(source_value)
    return hinted or source_value

WORD_RE = re.compile(r"[a-zA-Z][a-zA-Z'-]+")
PLACEHOLDER_RE = re.compile(r"%\d*\$?[sdif]|\{[^}]+\}|<[^>]+>|§.")


def normalize_for_detection(text: str) -> str:
    stripped = PLACEHOLDER_RE.sub(" ", text)
    lowered = stripped.lower()
    for term in sorted(PROTECTED_TERMS, key=len, reverse=True):
        lowered = lowered.replace(term, " ")
    lowered = re.sub(r"[^a-z\s]", " ", lowered)
    lowered = re.sub(r"\s+", " ", lowered).strip()
    return lowered


def strip_translation_markers(text: str) -> str:
    return text.replace(ZERO_WIDTH_SPACE, "")


def is_effective_fallback(locale_value: str, source_value: str) -> bool:
    if locale_value == source_value:
        return True
    return strip_translation_markers(locale_value) == source_value


def is_effective_english_seed(locale_value: str, source_value: str) -> bool:
    seed = english_seed_for_required_term(source_value)
    cleaned = strip_translation_markers(locale_value).strip()
    return cleaned.casefold() == seed.casefold()


def looks_like_obvious_english_fallback(source_value: str, locale_value: str) -> bool:
    if locale_value != source_value:
        return False

    normalized = normalize_for_detection(source_value)
    if not normalized:
        return False

    words = WORD_RE.findall(normalized)
    if not words:
        return False

    if len(words) == 1 and len(words[0]) <= 3:
        return False

    return True


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Fail when non-exempt locale files still contain obvious English fallback strings."
    )
    parser.add_argument(
        "--max-suspicious-per-locale",
        type=int,
        default=0,
        help="Allowed number of suspicious English fallback values per non-exempt locale before failing.",
    )
    parser.add_argument(
        "--require-mod-term-translations",
        dest="require_mod_term_translations",
        action="store_true",
        default=None,
        help=(
            "Enable strict mod-term translation coverage (this is the default). Fails when keys whose en_us value is a "
            "mod name-term (e.g., 'Waystone') remain effectively English in non-exempt locales (exact match, or only differs "
            "by zero-width-space coverage markers)."
        ),
    )
    parser.add_argument(
        "--no-require-mod-term-translations",
        dest="require_mod_term_translations",
        action="store_false",
        default=None,
        help=(
            "Disable strict mod-term translation coverage (not recommended). This keeps only the 'obvious English fallback' gate."
        ),
    )
    args = parser.parse_args()

    require_mod_term_translations = True if args.require_mod_term_translations is None else args.require_mod_term_translations

    source = load_json(SOURCE_PATH)
    failures: list[tuple[str, list[str]]] = []
    strict_failures: list[tuple[str, list[tuple[str, str, str, str]]]] = []

    for locale_code in SUPPORTED_LOCALES:
        if locale_code in EXEMPT_LOCALES:
            continue

        locale_data = load_json(locale_path(locale_code))
        suspicious_keys = [
            key
            for key, source_value in source.items()
            if looks_like_obvious_english_fallback(source_value, locale_data.get(key, ""))
        ]

        strict_items: list[tuple[str, str, str, str]] = []
        if require_mod_term_translations:
            for key, source_value in source.items():
                if source_value not in REQUIRED_TRANSLATED_SOURCE_VALUES:
                    continue
                locale_value = locale_data.get(key, "")
                if is_effective_fallback(locale_value, source_value):
                    strict_items.append((key, "equals_en_us", locale_value, source_value))
                    continue
                if is_effective_english_seed(locale_value, source_value):
                    strict_items.append((key, "equals_english_seed", locale_value, english_seed_for_required_term(source_value)))

        if len(suspicious_keys) > args.max_suspicious_per_locale:
            failures.append((locale_code, suspicious_keys))

        if strict_items:
            strict_failures.append((locale_code, strict_items))

    if not failures and not strict_failures:
        print("Verified locale translation coverage; no obvious English fallback content found in non-exempt locales.")
        if require_mod_term_translations:
            print("Verified strict mod-term translation coverage; required mod name-terms are not effectively English.")
        return 0

    if failures:
        print("Locales with obvious English fallback content still present:")
        for locale_code, suspicious_keys in failures:
            print(f"- {locale_code}.json: {len(suspicious_keys)} suspicious values")
            for key in suspicious_keys[:10]:
                print(f"  - {key}: {source[key]}")
            if len(suspicious_keys) > 10:
                print(f"  - ... {len(suspicious_keys) - 10} more")

    if strict_failures:
        print("Locales with required mod name-terms still effectively English:")
        for locale_code, strict_items in strict_failures:
            print(f"- {locale_code}.json: {len(strict_items)} strict failures")
            for key, reason, locale_value, expected in strict_items[:10]:
                print(f"  - {key}: {reason}")
                print(f"    en_us:   {source[key]}")
                print(f"    locale:  {locale_value}")
                print(f"    expect≠: {expected}")
            if len(strict_items) > 10:
                print(f"  - ... {len(strict_items) - 10} more")
    return 1


if __name__ == "__main__":
    sys.exit(main())