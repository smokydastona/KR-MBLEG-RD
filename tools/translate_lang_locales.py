from __future__ import annotations

import argparse
import concurrent.futures
import re
import sys
import threading
import time
from pathlib import Path

import requests
from deep_translator import GoogleTranslator
from deep_translator.exceptions import TranslationNotFound
from requests.exceptions import RequestException

from check_lang_translation_coverage import REQUIRED_TRANSLATED_SOURCE_VALUES
from sync_lang_locales import SOURCE_PATH, SUPPORTED_LOCALES, dump_json, load_json, merge_locale


TARGET_LANGUAGE_BY_LOCALE = {
    "af_za": "af", "ar_sa": "ar", "ast_es": "es", "az_az": "az", "ba_ru": "ru", "bar": "de",
    "be_by": "be", "bg_bg": "bg", "br_fr": "fr", "brb": "nl", "bs_ba": "bs", "ca_es": "ca",
    "cs_cz": "cs", "cy_gb": "cy", "da_dk": "da", "de_at": "de", "de_ch": "de", "de_de": "de",
    "el_gr": "el", "en_au": "en", "en_ca": "en", "en_gb": "en", "en_nz": "en", "en_pt": "en",
    "en_ud": "en", "enp": "en", "enws": "en", "eo_uy": "eo", "es_ar": "es", "es_cl": "es",
    "es_ec": "es", "es_es": "es", "es_mx": "es", "es_uy": "es", "es_ve": "es", "esan": "es",
    "et_ee": "et", "eu_es": "eu", "fa_ir": "fa", "fi_fi": "fi", "fil_ph": "tl", "fo_fo": "da",
    "fr_ca": "fr", "fr_fr": "fr", "fra_de": "de", "fur_it": "it", "fy_nl": "fy", "ga_ie": "ga",
    "gd_gb": "gd", "gl_es": "gl", "haw_us": "haw", "he_il": "iw", "hi_in": "hi", "hr_hr": "hr",
    "hu_hu": "hu", "hy_am": "hy", "id_id": "id", "ig_ng": "ig", "io_en": "eo", "is_is": "is",
    "isv": "pl", "it_it": "it", "ja_jp": "ja", "jbo_en": "en", "ka_ge": "ka", "kk_kz": "kk",
    "kn_in": "kn", "ko_kr": "ko", "ksh": "de", "kw_gb": "cy", "la_la": "la", "lb_lu": "de",
    "li_li": "nl", "lmo": "it", "lo_la": "lo", "lol_us": "en", "lt_lt": "lt", "lv_lv": "lv",
    "lzh": "zh-TW", "mk_mk": "mk", "mn_mn": "mn", "ms_my": "ms", "mt_mt": "mt", "nah": "es",
    "nds_de": "de", "nl_be": "nl", "nl_nl": "nl", "nn_no": "no", "no_no": "no", "oc_fr": "fr",
    "ovd": "sv", "pl_pl": "pl", "pt_br": "pt", "pt_pt": "pt", "qya_aa": "en", "ro_ro": "ro",
    "rpr": "ru", "ru_ru": "ru", "ry_ua": "uk", "sah_sah": "ru", "se_no": "no", "sk_sk": "sk",
    "sl_si": "sl", "so_so": "so", "sq_al": "sq", "sr_cs": "sr", "sr_sp": "sr", "sv_se": "sv",
    "sxu": "de", "szl": "pl", "ta_in": "ta", "th_th": "th", "tl_ph": "tl", "tlh_aa": "en",
    "tok": "en", "tr_tr": "tr", "tt_ru": "ru", "uk_ua": "uk", "val_es": "ca", "vec_it": "it",
    "vi_vn": "vi", "yi_de": "yi", "yo_ng": "yo", "zh_cn": "zh-CN", "zh_hk": "zh-TW",
    "zh_tw": "zh-TW", "zlm_arab": "ms",
}


DONOR_LOCALE_BY_TARGET_LANGUAGE = {
    "ca": "ca_es",
    "de": "de_de",
    "es": "es_es",
    "fr": "fr_fr",
    "it": "it_it",
    "ja": "ja_jp",
    "ko": "ko_kr",
    "nl": "nl_nl",
    "no": "no_no",
    "pl": "pl_pl",
    "pt": "pt_br",
    "ru": "ru_ru",
    "sv": "sv_se",
    "uk": "uk_ua",
    "zh-CN": "zh_cn",
    "zh-TW": "zh_tw",
}


ALWAYS_PROTECTED_TERMS = [
    "Krümblegård",
]


MOD_TERM_SUBSTITUTIONS = {
    # Translation hints for better draft output (applied before translation).
    # These do not affect en_us.json; they only influence the machine-draft translator input.
    "Cephalari": "squid people",
    "Moogloom": "sad mushroom cow",
    "Scaralon": "serious",
    "Wyrdwing": "wierd wing",
    "Pebblit": "rock child",
    "Telekinesis": "mind movement",
}


def english_seed_for_required_term(source_value: str, *, hint_compounds: bool) -> str:
    seed = apply_mod_term_policy(source_value, hint_compounds=hint_compounds)
    return seed


MOD_TRANSLATABLE_TERMS = [
    "Wayfall",
    "Telekinesis",
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
    "Traprock",
    "Waystone",
    "Waystones",
    "Scarsteel",
]


UPSIDE_DOWN_MAP = {
    ord("a"): "ɐ", ord("b"): "q", ord("c"): "ɔ", ord("d"): "p", ord("e"): "ǝ", ord("f"): "ɟ",
    ord("g"): "ᵷ", ord("h"): "ɥ", ord("i"): "ᴉ", ord("j"): "ɾ", ord("k"): "ʞ", ord("l"): "ꞁ",
    ord("m"): "ɯ", ord("n"): "u", ord("o"): "o", ord("p"): "d", ord("q"): "b", ord("r"): "ɹ",
    ord("s"): "s", ord("t"): "ʇ", ord("u"): "n", ord("v"): "ʌ", ord("w"): "ʍ", ord("x"): "x",
    ord("y"): "ʎ", ord("z"): "z", ord("A"): "∀", ord("B"): "𐐒", ord("C"): "Ɔ", ord("D"): "◖",
    ord("E"): "Ǝ", ord("F"): "Ⅎ", ord("G"): "⅁", ord("H"): "H", ord("I"): "I", ord("J"): "ſ",
    ord("K"): "⋊", ord("L"): "⅂", ord("M"): "W", ord("N"): "N", ord("O"): "O", ord("P"): "Ԁ",
    ord("Q"): "Q", ord("R"): "ᴚ", ord("S"): "S", ord("T"): "⊥", ord("U"): "∩", ord("V"): "Λ",
    ord("W"): "M", ord("X"): "X", ord("Y"): "⅄", ord("Z"): "Z", ord("0"): "0", ord("1"): "⇂",
    ord("2"): "ᄅ", ord("3"): "Ɛ", ord("4"): "ㄣ", ord("5"): "ϛ", ord("6"): "9", ord("7"): "ㄥ",
    ord("8"): "8", ord("9"): "6", ord("!"): "¡", ord("?"): "¿", ord("."): "˙", ord(","): "'",
    ord("\""): ",,", ord("("): ")", ord(")"): "(", ord("["): "]", ord("]"): "[", ord("{"): "}", ord("}"): "{",
}


PLACEHOLDER_ARTIFACT_RE = re.compile(r"ZZKRGTERM\d+KRZZ|__[^\n]*__")
REQUEST_TIMEOUT_SECONDS = 20
MAX_TRANSLATION_RETRIES = 3
TRANSLATION_WORKERS = 10
LOCALE_WORKERS = 4
TRANSLATION_CACHE_DIR = Path(__file__).resolve().parent / "_reports" / "translation_cache"
FILE_REPLACE_RETRIES = 10
FILE_REPLACE_RETRY_DELAY_SECONDS = 0.5
TRANSLATION_CACHE_LOCKS: dict[str, threading.Lock] = {}
TRANSLATION_CACHE_LOCKS_GUARD = threading.Lock()

ZERO_WIDTH_SPACE = "\u200B"


def strip_translation_markers(text: str) -> str:
    return text.replace(ZERO_WIDTH_SPACE, "")


def is_effective_fallback(locale_value: str | None, source_value: str) -> bool:
    if locale_value is None:
        return True
    if locale_value == source_value:
        return True
    return strip_translation_markers(locale_value) == source_value


def is_effective_english_seed(locale_value: str | None, source_value: str, *, hint_compounds: bool) -> bool:
    if locale_value is None:
        return False
    seed = english_seed_for_required_term(source_value, hint_compounds=hint_compounds)
    cleaned = strip_translation_markers(locale_value).strip()
    return cleaned.casefold() == seed.strip().casefold()


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


def split_compound_term(term: str) -> str | None:
    if " " in term:
        return None
    if not term.isalpha():
        return None
    # Avoid splitting non-ASCII proper nouns like Krümblegård.
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


def build_compound_hint_map(terms: list[str]) -> dict[str, str]:
    hints: dict[str, str] = {}
    for term in terms:
        hint = split_compound_term(term)
        if hint:
            hints[term] = hint
    return hints


def apply_compound_hints(text: str, hints: dict[str, str]) -> str:
    output = text
    for term in sorted(hints.keys(), key=len, reverse=True):
        if term in output:
            output = output.replace(term, hints[term])
    return output


def apply_mod_term_substitutions(text: str) -> str:
    output = text
    for term, substitute in sorted(MOD_TERM_SUBSTITUTIONS.items(), key=lambda item: len(item[0]), reverse=True):
        if term in output:
            output = output.replace(term, substitute)
    return output


def apply_mod_term_policy(text: str, *, hint_compounds: bool) -> str:
    output = apply_mod_term_substitutions(text)
    if not hint_compounds:
        return output

    hint_terms = MOD_TRANSLATABLE_TERMS + list(MOD_TERM_SUBSTITUTIONS.keys())
    compound_hints = build_compound_hint_map(hint_terms)
    if not compound_hints:
        return output
    return apply_compound_hints(output, compound_hints)


def locale_path(locale_code: str) -> Path:
    return SOURCE_PATH.parent / f"{locale_code}.json"


def write_locale_json(path: Path, data: dict[str, str]) -> None:
    temp_path = path.with_suffix(f"{path.suffix}.tmp")
    temp_path.write_text(dump_json(data), encoding="utf-8", newline="\n")
    for attempt in range(FILE_REPLACE_RETRIES):
        try:
            temp_path.replace(path)
            return
        except PermissionError:
            if attempt == FILE_REPLACE_RETRIES - 1:
                raise
            time.sleep(FILE_REPLACE_RETRY_DELAY_SECONDS * (attempt + 1))


def translation_cache_path(target_language: str) -> Path:
    safe_language = target_language.replace("/", "_")
    return TRANSLATION_CACHE_DIR / f"{safe_language}.json"


def load_translation_cache(target_language: str) -> dict[str, str]:
    path = translation_cache_path(target_language)
    if not path.exists():
        return {}
    return load_json(path)


def write_translation_cache(target_language: str, cache: dict[str, str]) -> None:
    TRANSLATION_CACHE_DIR.mkdir(parents=True, exist_ok=True)
    path = translation_cache_path(target_language)
    temp_path = path.with_suffix(f"{path.suffix}.tmp")
    temp_path.write_text(dump_json(cache), encoding="utf-8", newline="\n")
    for attempt in range(FILE_REPLACE_RETRIES):
        try:
            temp_path.replace(path)
            return
        except PermissionError:
            if attempt == FILE_REPLACE_RETRIES - 1:
                raise
            time.sleep(FILE_REPLACE_RETRY_DELAY_SECONDS * (attempt + 1))


def translation_cache_lock(target_language: str) -> threading.Lock:
    with TRANSLATION_CACHE_LOCKS_GUARD:
        return TRANSLATION_CACHE_LOCKS.setdefault(target_language, threading.Lock())


def suspicious_fallback_count(locale_data: dict[str, str], source: dict[str, str]) -> int:
    return sum(1 for key, value in source.items() if is_effective_fallback(locale_data.get(key), value))


def donor_locale_data(locale_code: str, source: dict[str, str]) -> dict[str, str]:
    target_language = TARGET_LANGUAGE_BY_LOCALE[locale_code]
    candidate_locales = [
        candidate
        for candidate, candidate_language in TARGET_LANGUAGE_BY_LOCALE.items()
        if candidate != locale_code and candidate_language == target_language
    ]
    preferred_donor = DONOR_LOCALE_BY_TARGET_LANGUAGE.get(target_language)
    if preferred_donor in candidate_locales:
        candidate_locales.remove(preferred_donor)
        candidate_locales.insert(0, preferred_donor)

    ranked_donors: list[tuple[int, str, dict[str, str]]] = []
    for candidate_locale in candidate_locales:
        candidate_path = locale_path(candidate_locale)
        if not candidate_path.exists():
            continue

        candidate_data = load_json(candidate_path)
        ranked_donors.append((suspicious_fallback_count(candidate_data, source), candidate_locale, candidate_data))

    ranked_donors.sort(key=lambda item: (item[0], item[1]))

    merged_donor_data: dict[str, str] = {}
    for _, _, candidate_data in ranked_donors:
        for key, source_value in source.items():
            donor_value = candidate_data.get(key)
            if donor_value and donor_value != source_value and key not in merged_donor_data:
                merged_donor_data[key] = donor_value

    return merged_donor_data


def protect_terms(text: str) -> tuple[str, dict[str, str]]:
    placeholders: dict[str, str] = {}
    protected = text
    for index, term in enumerate(sorted(ALWAYS_PROTECTED_TERMS, key=len, reverse=True)):
        token = f"ZZKRGTERM{index}KRZZ"
        if term in protected:
            placeholders[token] = term
            protected = protected.replace(term, token)
    return protected, placeholders


def restore_terms(text: str, placeholders: dict[str, str]) -> str:
    restored = text
    for token, term in placeholders.items():
        restored = restored.replace(token, term)
    return restored


def maybe_capitalize_like(source_value: str, translated_value: str) -> str:
    if not source_value or not translated_value:
        return translated_value
    if source_value[:1].isupper() and translated_value[:1].islower():
        return translated_value[:1].upper() + translated_value[1:]
    return translated_value


_WORD_SPLIT_RE = re.compile(r"(\s+)")


def translate_word_by_word(text: str, target_language: str) -> str:
    """Translate a short English phrase token-by-token.

    This is a fallback for mod name-terms that sometimes come back unchanged when translated as a full phrase.
    """
    translator = GoogleTranslator(source="en", target=target_language)
    parts = _WORD_SPLIT_RE.split(text)
    translated_parts: list[str] = []
    for part in parts:
        if not part:
            continue
        if part.isspace():
            translated_parts.append(part)
            continue
        translated_parts.append(translate_value(translator, part))
    return "".join(translated_parts)


def stylize_english_variant(locale_code: str, value: str) -> str:
    if locale_code == "en_ud":
        return value.translate(UPSIDE_DOWN_MAP)[::-1]
    if locale_code == "en_pt":
        output = re.sub(r"\byou\b", "ye", value, flags=re.IGNORECASE)
        output = re.sub(r"\byour\b", "yer", output, flags=re.IGNORECASE)
        output = re.sub(r"\bare\b", "be", output, flags=re.IGNORECASE)
        return output
    if locale_code == "lol_us":
        output = re.sub(r"\bthe\b", "teh", value, flags=re.IGNORECASE)
        output = re.sub(r"\byou\b", "u", output, flags=re.IGNORECASE)
        output = re.sub(r"\bhave\b", "haz", output, flags=re.IGNORECASE)
        return output
    if locale_code == "enws":
        output = re.sub(r"\byou\b", "thou", value, flags=re.IGNORECASE)
        output = re.sub(r"\byour\b", "thy", output, flags=re.IGNORECASE)
        output = re.sub(r"\bare\b", "art", output, flags=re.IGNORECASE)
        return output
    if locale_code == "enp":
        output = re.sub(r"\binventory\b", "holding", value, flags=re.IGNORECASE)
        output = re.sub(r"\bdrops\b", "fallings", output, flags=re.IGNORECASE)
        output = re.sub(r"\bbutton\b", "press-stud", output, flags=re.IGNORECASE)
        return output
    return value


def _requests_get_with_timeout(*args, **kwargs):
    kwargs.setdefault("timeout", REQUEST_TIMEOUT_SECONDS)
    return requests.api.get(*args, **kwargs)


requests.get = _requests_get_with_timeout


def translate_value(translator: GoogleTranslator, value: str) -> str:
    for attempt in range(MAX_TRANSLATION_RETRIES):
        try:
            translated_value = translator.translate(value)
        except TranslationNotFound:
            return value
        except RequestException:
            if attempt == MAX_TRANSLATION_RETRIES - 1:
                return value
            time.sleep(1.0 * (attempt + 1))
            continue

        if translated_value is None:
            return value
        return translated_value

    return value


def translate_values(values: list[str], target_language: str) -> list[str]:
    translator = GoogleTranslator(source="en", target=target_language)
    translated_values: list[str] = []
    for start in range(0, len(values), 50):
        chunk = values[start:start + 50]
        # NOTE: deep_translator.GoogleTranslator is not thread-safe; concurrent usage can corrupt results.
        translated_chunk = [translate_value(translator, value) for value in chunk]
        translated_values.extend(translated_chunk)
        time.sleep(0.2)
    return translated_values


def translate_chunk(values: list[str], target_language: str) -> list[str]:
    if not values:
        return []

    with translation_cache_lock(target_language):
        translated_by_value = load_translation_cache(target_language)
        unique_values = list(dict.fromkeys(values))
        # Purge cached entries that are clearly "stuck" (cached translation equals the English input).
        # These frequently come from transient request failures; keeping them prevents any future retries.
        if target_language != "en":
            stuck_values = [
                value
                for value in unique_values
                if value in translated_by_value
                and isinstance(translated_by_value.get(value), str)
                and translated_by_value[value].strip().casefold() == value.strip().casefold()
            ]
            if stuck_values:
                for value in stuck_values:
                    translated_by_value.pop(value, None)
                write_translation_cache(target_language, translated_by_value)

        missing_values = [value for value in unique_values if value not in translated_by_value]
        translated_missing_by_value: dict[str, str] = {}

        if missing_values:
            translator = GoogleTranslator(source="en", target=target_language)
            # NOTE: deep_translator.GoogleTranslator is not thread-safe; concurrent usage can corrupt results.
            translated_missing_values = [translate_value(translator, value) for value in missing_values]

            translated_missing_by_value = dict(zip(missing_values, translated_missing_values))

            if target_language != "en":
                unchanged = [
                    original
                    for original, translated in translated_missing_by_value.items()
                    if translated is None or translated.strip().casefold() == original.strip().casefold()
                ]
                if unchanged:
                    # Retry a couple times with a slower, sequential approach; this often fixes transient request issues.
                    retry_translator = GoogleTranslator(source="en", target=target_language)
                    for attempt in range(2):
                        still = []
                        for original in unchanged:
                            new_value = translate_value(retry_translator, original)
                            translated_missing_by_value[original] = new_value
                            if new_value is None or new_value.strip().casefold() == original.strip().casefold():
                                still.append(original)
                        unchanged = still
                        if not unchanged:
                            break
                        time.sleep(0.5 * (attempt + 1))

            # Only cache results that are genuinely different from the English input.
            # If we cache unchanged values, we permanently lock in failures.
            updates: dict[str, str] = {}
            for original, translated in translated_missing_by_value.items():
                if translated is None:
                    continue
                if target_language != "en" and translated.strip().casefold() == original.strip().casefold():
                    continue
                updates[original] = translated

            if updates:
                translated_by_value.update(updates)
                write_translation_cache(target_language, translated_by_value)

        translated_chunk: list[str] = []
        for value in values:
            cached = translated_by_value.get(value)
            if cached is not None:
                translated_chunk.append(cached)
                continue
            translated = translated_missing_by_value.get(value)
            if translated is None:
                translated_chunk.append(value)
            else:
                translated_chunk.append(translated)

    time.sleep(0.2)
    return translated_chunk


def translate_locale(
    locale_code: str,
    source: dict[str, str],
    *,
    hint_compounds: bool,
    only_required_mod_terms: bool,
) -> dict[str, str]:
    if locale_code == "en_us":
        return source

    keys_to_translate = list(source.keys())
    if only_required_mod_terms:
        keys_to_translate = [
            key
            for key, value in source.items()
            if value in REQUIRED_TRANSLATED_SOURCE_VALUES
        ]

    path = locale_path(locale_code)
    current = load_json(path) if path.exists() else {}
    donor_data = donor_locale_data(locale_code, source)

    updated = dict(current)

    suspicious_required_keys: set[str] = set()
    if only_required_mod_terms:
        # Heuristic to repair previously-corrupted locale entries:
        # if two *different* required source values share the exact same locale value,
        # it's overwhelmingly likely a cache-poison / mapping swap artifact.
        #
        # Note: It's normal for multiple keys to share the same source value (e.g., block+item both "Ash moss"),
        # so we only flag duplicates across *different* source values.
        cleaned_to_source_values: dict[str, set[str]] = {}
        cleaned_to_keys: dict[str, list[str]] = {}
        for key in keys_to_translate:
            source_value = source[key]
            locale_value = updated.get(key)
            if not isinstance(locale_value, str):
                continue
            cleaned = strip_translation_markers(locale_value).strip()
            if not cleaned:
                continue
            cleaned_cf = cleaned.casefold()
            cleaned_to_keys.setdefault(cleaned_cf, []).append(key)
            cleaned_to_source_values.setdefault(cleaned_cf, set()).add(source_value)

        for cleaned_cf, source_values in cleaned_to_source_values.items():
            if len(source_values) <= 1:
                continue
            suspicious_required_keys.update(cleaned_to_keys.get(cleaned_cf, []))

    required_seeds_casefold = {
        english_seed_for_required_term(value, hint_compounds=hint_compounds).strip().casefold()
        for value in REQUIRED_TRANSLATED_SOURCE_VALUES
    }

    def is_wrong_required_seed(locale_value: str | None, source_value: str) -> bool:
        if not locale_value:
            return False
        cleaned = locale_value.replace(ZERO_WIDTH_SPACE, "").strip().casefold()
        if cleaned not in required_seeds_casefold:
            return False
        # If it's equal to this term's own seed, that's handled by is_effective_english_seed.
        own_seed = english_seed_for_required_term(source_value, hint_compounds=hint_compounds).strip().casefold()
        return cleaned != own_seed
    for key in keys_to_translate:
        value = source[key]
        if not is_effective_fallback(updated.get(key), value) and not (
            only_required_mod_terms
            and value in REQUIRED_TRANSLATED_SOURCE_VALUES
            and (
                is_effective_english_seed(updated.get(key), value, hint_compounds=hint_compounds)
                or is_wrong_required_seed(updated.get(key), value)
                or key in suspicious_required_keys
            )
        ):
            continue

        donor_value = donor_data.get(key)
        if donor_value and donor_value != value:
            updated[key] = donor_value

    pending_keys = [
        key
        for key in keys_to_translate
        if is_effective_fallback(updated.get(key), source[key])
        or (
            only_required_mod_terms
            and source[key] in REQUIRED_TRANSLATED_SOURCE_VALUES
            and (
                is_effective_english_seed(updated.get(key), source[key], hint_compounds=hint_compounds)
                or is_wrong_required_seed(updated.get(key), source[key])
                or key in suspicious_required_keys
            )
        )
        or (
            isinstance(updated.get(key), str)
            and PLACEHOLDER_ARTIFACT_RE.search(updated[key]) is not None
        )
    ]
    if not pending_keys:
        return merge_locale(source, updated)

    target_language = TARGET_LANGUAGE_BY_LOCALE[locale_code]
    if target_language == "en":
        translated_values = [stylize_english_variant(locale_code, source[key]) for key in pending_keys]
        for key, value in zip(pending_keys, translated_values):
            updated[key] = value
    else:
        if only_required_mod_terms:
            # Translate required mod terms one-by-one without using the shared cached chunk translation path.
            # This avoids thread-safety issues and prevents poisoned cache entries from propagating.
            translator = GoogleTranslator(source="en", target=target_language)
            for key in pending_keys:
                source_value = source[key]
                seed = english_seed_for_required_term(source_value, hint_compounds=hint_compounds).lower()
                protected, placeholders = protect_terms(seed)
                translated = translate_value(translator, protected)
                translated = restore_terms(translated, placeholders)
                translated = maybe_capitalize_like(source_value, translated)
                if is_effective_english_seed(translated, source_value, hint_compounds=hint_compounds):
                    fallback = translate_word_by_word(seed, target_language)
                    fallback = maybe_capitalize_like(source_value, fallback)
                    if not is_effective_english_seed(fallback, source_value, hint_compounds=hint_compounds):
                        translated = fallback
                updated[key] = translated

                # Persist each key so long-running locale seeding can resume after interruption.
                write_locale_json(path, merge_locale(source, updated))
            return merge_locale(source, updated)

        for start in range(0, len(pending_keys), 50):
            chunk_keys = pending_keys[start:start + 50]
            protected_values = []
            placeholder_maps = []
            for key in chunk_keys:
                source_value = source[key]
                value = apply_mod_term_policy(source_value, hint_compounds=hint_compounds)
                if source_value in REQUIRED_TRANSLATED_SOURCE_VALUES:
                    value = value.lower()
                protected, placeholders = protect_terms(value)
                protected_values.append(protected)
                placeholder_maps.append(placeholders)

            translated_chunk = translate_chunk(protected_values, target_language)
            translated_chunk = [
                restore_terms(value, placeholder_maps[index])
                for index, value in enumerate(translated_chunk)
            ]

            for key, value in zip(chunk_keys, translated_chunk):
                source_value = source[key]
                if source_value in REQUIRED_TRANSLATED_SOURCE_VALUES:
                    value = maybe_capitalize_like(source_value, value)
                    if is_effective_english_seed(value, source_value, hint_compounds=hint_compounds):
                        seed = english_seed_for_required_term(source_value, hint_compounds=hint_compounds).lower()
                        fallback = translate_word_by_word(seed, target_language)
                        fallback = maybe_capitalize_like(source_value, fallback)
                        if not is_effective_english_seed(fallback, source_value, hint_compounds=hint_compounds):
                            value = fallback
                updated[key] = value

            # Persist each completed chunk so long-running locale seeding can resume after interruption.
            write_locale_json(path, merge_locale(source, updated))

    return merge_locale(source, updated)


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Generate machine-drafted locale files for Kruemblegard while preserving fallback sync behavior."
    )
    parser.add_argument("locales", nargs="*", help="Optional locale codes to translate; defaults to all non-en_us locales.")
    parser.add_argument(
        "--hint-compounds",
        action="store_true",
        help="Split certain mod compound terms (e.g., 'Traprock' -> 'Trap rock') as a translation hint; does not affect en_us.json.",
    )
    parser.add_argument(
        "--only-required-mod-terms",
        action="store_true",
        help=(
            "Only translate keys whose en_us value is in the strict-gate required mod-term display-name list. "
            "This is intended for making the strict coverage gate pass without rewriting the whole locale."
        ),
    )
    args = parser.parse_args()

    source = load_json(SOURCE_PATH)
    locales = args.locales or [locale for locale in SUPPORTED_LOCALES if locale != "en_us"]

    for locale_code in locales:
        if locale_code not in TARGET_LANGUAGE_BY_LOCALE:
            raise KeyError(f"Missing translation mapping for {locale_code}")

    errors: list[tuple[str, BaseException]] = []
    errors_guard = threading.Lock()

    def translate_one_locale(locale_code: str) -> None:
        try:
            print(f"Translating {locale_code}...", flush=True)
            translated = translate_locale(
                locale_code,
                source,
                hint_compounds=args.hint_compounds,
                only_required_mod_terms=args.only_required_mod_terms,
            )
            write_locale_json(locale_path(locale_code), translated)
        except BaseException as exc:
            with errors_guard:
                errors.append((locale_code, exc))
            print(f"ERROR translating {locale_code}: {exc}", flush=True)

    with concurrent.futures.ThreadPoolExecutor(max_workers=min(LOCALE_WORKERS, len(locales) or 1)) as executor:
        list(executor.map(translate_one_locale, locales))

    if errors:
        print("\nTranslation failures:")
        for locale_code, exc in sorted(errors, key=lambda item: item[0]):
            print(f"- {locale_code}: {exc}")
        return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())
