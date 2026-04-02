from __future__ import annotations

import argparse
import re
import sys
import time
from pathlib import Path

import requests
from deep_translator import GoogleTranslator
from deep_translator.exceptions import TranslationNotFound
from requests.exceptions import RequestException

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
    "kn_in": "kn", "ko_kr": "ko", "ksh": "de", "kw_gb": "en", "la_la": "la", "lb_lu": "de",
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


PROTECTED_TERMS = [
    "Krümblegård", "Cephalari", "Wayfall", "Telekinesis", "Moogloom", "Scaralon", "Wyrdwing",
    "Driftwhale", "Pebble Wren", "Mossback Tortoise", "Grave Cairn", "Trader Beetle", "Spiral Strider",
    "Drift Skimmer", "Treadwinder", "Echo Harness", "Ashbloom", "Ashmoss", "Ashspire", "Cairnroot",
    "Echocap", "Echowood", "Driftwood", "Driftwillow", "Faultwood", "Paleweft", "Remnant", "Runegrowth",
    "Pebblit", "Traprock", "Waystone", "Waystones", "Scarsteel",
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


def locale_path(locale_code: str) -> Path:
    return SOURCE_PATH.parent / f"{locale_code}.json"


def donor_locale_data(locale_code: str) -> dict[str, str]:
    target_language = TARGET_LANGUAGE_BY_LOCALE[locale_code]
    donor_locale = DONOR_LOCALE_BY_TARGET_LANGUAGE.get(target_language)
    if donor_locale is None or donor_locale == locale_code:
        return {}

    donor_path = locale_path(donor_locale)
    if not donor_path.exists():
        return {}

    return load_json(donor_path)


def protect_terms(text: str) -> tuple[str, dict[str, str]]:
    placeholders: dict[str, str] = {}
    protected = text
    for index, term in enumerate(sorted(PROTECTED_TERMS, key=len, reverse=True)):
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
        translated_chunk = [translate_value(translator, value) for value in chunk]
        translated_values.extend(translated_chunk)
        time.sleep(0.2)
    return translated_values


def translate_locale(locale_code: str, source: dict[str, str]) -> dict[str, str]:
    if locale_code == "en_us":
        return source

    path = locale_path(locale_code)
    current = load_json(path) if path.exists() else {}
    donor_data = donor_locale_data(locale_code)

    updated = dict(current)
    for key, value in source.items():
        if updated.get(key, value) != value:
            continue

        donor_value = donor_data.get(key)
        if donor_value and donor_value != value:
            updated[key] = donor_value

    pending_keys = [
        key
        for key, value in source.items()
        if updated.get(key, value) == value
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
    else:
        protected_values = []
        placeholder_maps = []
        for key in pending_keys:
            protected, placeholders = protect_terms(source[key])
            protected_values.append(protected)
            placeholder_maps.append(placeholders)
        translated_values = translate_values(protected_values, target_language)
        translated_values = [restore_terms(value, placeholder_maps[index]) for index, value in enumerate(translated_values)]

    for key, value in zip(pending_keys, translated_values):
        updated[key] = value
    return merge_locale(source, updated)


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Generate machine-drafted locale files for Kruemblegard while preserving fallback sync behavior."
    )
    parser.add_argument("locales", nargs="*", help="Optional locale codes to translate; defaults to all non-en_us locales.")
    args = parser.parse_args()

    source = load_json(SOURCE_PATH)
    locales = args.locales or [locale for locale in SUPPORTED_LOCALES if locale != "en_us"]

    for locale_code in locales:
        if locale_code not in TARGET_LANGUAGE_BY_LOCALE:
            raise KeyError(f"Missing translation mapping for {locale_code}")
        print(f"Translating {locale_code}...")
        translated = translate_locale(locale_code, source)
        locale_path(locale_code).write_text(dump_json(translated), encoding="utf-8", newline="\n")

    return 0


if __name__ == "__main__":
    sys.exit(main())
