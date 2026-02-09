[CmdletBinding()]
param(
    # Where to write the human-readable markdown output.
    [Parameter(Mandatory = $false)][string]$OutDoc = "",

    # Where to write the S-NDB-UND JSON manifest output.
    [Parameter(Mandatory = $false)][string]$OutManifest = "",

    # Include music.* events in the manifest (diffusers by default).
    [Parameter(Mandatory = $false)][switch]$IncludeMusic,

    # Default number of variants for non-streamed SFX.
    [Parameter(Mandatory = $false)][int]$DefaultVariants = 3
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Get-RepoRoot {
    $root = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')
    return $root.Path
}

function Get-ModSoundsRegistrations {
    param(
        [Parameter(Mandatory = $true)][string]$JavaText
    )

    $map = @{}

    $patternSOUNDSRegister = '(?s)public\s+static\s+final\s+RegistryObject<SoundEvent>\s+([A-Za-z0-9_]+)\s*=\s*SOUNDS\.register\(\s*"([^"]+)"'
    foreach ($m in [regex]::Matches($JavaText, $patternSOUNDSRegister)) {
        $field = $m.Groups[1].Value
        $id = $m.Groups[2].Value
        $map[$id] = $field
    }

    $patternHelperRegister = '(?s)public\s+static\s+final\s+RegistryObject<SoundEvent>\s+([A-Za-z0-9_]+)\s*=\s*register\(\s*"([^"]+)"\s*\)\s*;'
    foreach ($m in [regex]::Matches($JavaText, $patternHelperRegister)) {
        $field = $m.Groups[1].Value
        $id = $m.Groups[2].Value
        $map[$id] = $field
    }

    return $map
}

function Get-SoundsJsonEntries {
    param(
        [Parameter(Mandatory = $true)][string]$JsonPath
    )

    $raw = Get-Content -LiteralPath $JsonPath -Raw -Encoding UTF8
    $obj = $raw | ConvertFrom-Json

    $entries = @{}
    foreach ($name in $obj.PSObject.Properties.Name) {
        $entries[$name] = $obj.$name
    }

    return $entries
}

function Get-LangEntries {
    param(
        [Parameter(Mandatory = $true)][string]$LangPath
    )

    $raw = Get-Content -LiteralPath $LangPath -Raw -Encoding UTF8
    $obj = $raw | ConvertFrom-Json

    $entries = @{}
    foreach ($name in $obj.PSObject.Properties.Name) {
        $entries[$name] = [string]$obj.$name
    }

    return $entries
}

function Get-StreamedFlag {
    param(
        [Parameter(Mandatory = $true)]$SoundsEntry
    )

    if ($null -eq $SoundsEntry) { return $false }

    $soundsProp = $SoundsEntry.PSObject.Properties['sounds']
    if ($null -eq $soundsProp -or $null -eq $soundsProp.Value) { return $false }

    foreach ($s in $soundsProp.Value) {
        if ($s -isnot [string]) {
            $streamProp = $s.PSObject.Properties['stream']
            if ($null -ne $streamProp -and $null -ne $streamProp.Value -and [bool]$streamProp.Value) {
                return $true
            }
        }
    }

    return $false
}

function Get-SoundNames {
    param(
        [Parameter(Mandatory = $true)]$SoundsEntry
    )

    $names = @()
    if ($null -eq $SoundsEntry) { return $names }

    $soundsProp = $SoundsEntry.PSObject.Properties['sounds']
    if ($null -eq $soundsProp -or $null -eq $soundsProp.Value) { return $names }

    foreach ($s in $soundsProp.Value) {
        if ($s -is [string]) {
            $names += $s
            continue
        }

        $nameProp = $s.PSObject.Properties['name']
        if ($null -ne $nameProp -and $null -ne $nameProp.Value) {
            $names += [string]$nameProp.Value
        }
    }

    return $names
}

function Get-SubtitleKey {
    param(
        [Parameter(Mandatory = $true)]$SoundsEntry
    )

    if ($null -eq $SoundsEntry) { return $null }

    $subtitleProp = $SoundsEntry.PSObject.Properties['subtitle']
    if ($null -eq $subtitleProp -or $null -eq $subtitleProp.Value) { return $null }

    $s = [string]$subtitleProp.Value
    if ([string]::IsNullOrWhiteSpace($s)) { return $null }

    return $s
}

function Get-FriendlySubtitle {
    param(
        [Parameter(Mandatory = $true)][string]$EventKey
    )

    $base = $EventKey
    if ($base.StartsWith('music.')) {
        $base = $base.Substring('music.'.Length)
    }
    if ($base.StartsWith('kruemblegard_')) {
        $base = $base.Substring('kruemblegard_'.Length)
    }
    if ($base.StartsWith('kruemblegard.')) {
        $base = $base.Substring('kruemblegard.'.Length)
    }

    $base = $base.Replace('.', ' ').Replace('_', ' ').Trim()
    if ([string]::IsNullOrWhiteSpace($base)) {
        return "Kruemblegard sound"
    }

    # Title-case-ish without needing culture-sensitive rules.
    $words = $base -split '\s+'
    $fixed = @()
    foreach ($w in $words) {
        if ($w.Length -le 1) { $fixed += $w.ToUpperInvariant(); continue }
        $fixed += ($w.Substring(0, 1).ToUpperInvariant() + $w.Substring(1).ToLowerInvariant())
    }

    return "Kruemblegard: " + ($fixed -join ' ')
}

function Get-RecommendedPrompt {
    param(
        [Parameter(Mandatory = $true)][string]$EventKey,
        [Parameter(Mandatory = $false)][string]$SubtitleText
    )

    $k = $EventKey.ToLowerInvariant()

    if ($k.StartsWith('music.')) {
        if ($k -eq 'music.kruemblegard') {
            return "dark atmospheric boss music, eerie drones, low strings, ominous"
        }
        if ($k -eq 'music.wayfall') {
            return "mysterious ambient exploration music, airy pads, subtle percussion, otherworldly"
        }
        return "ambient game music loop, atmospheric, subtle"
    }

    if ($k -match 'ambient') {
        return "deep stone rumble ambience, cavernous, ominous, low drone"
    }
    if ($k -match 'core_hum') {
        return "mystical core hum, low drone, subtle energy crackle"
    }
    if ($k -match 'rise') {
        return "ancient stone rising, grinding rock, heavy rumble"
    }
    if ($k -match 'attack_smash') {
        return "stone smash impact, heavy hit, debris"
    }
    if ($k -match 'attack_slam') {
        return "heavy slam impact, thud, stone"
    }
    if ($k -match 'attack_rune') {
        return "rune magic pulse, arcane whoosh, sparkle"
    }
    if ($k -match 'attack') {
        return "heavy swing whoosh, stone weapon, impact"
    }
    if ($k -match 'dash') {
        return "fast dash whoosh, scraping stone, short"
    }
    if ($k -match 'storm') {
        return "summon storm, magical wind gust, thunderous rumble"
    }
    if ($k -match 'death') {
        return "massive stone collapse, debris falling, heavy rumble"
    }
    if ($k -match 'radiant') {
        return "radiant magic burst, shimmering energy, short"
    }

    if (-not [string]::IsNullOrWhiteSpace($SubtitleText)) {
        # Fall back to subtitle meaning, but keep it prompt-y.
        return ($SubtitleText + ", short game sound effect").Trim()
    }

    return "TODO: describe sound for event '" + $EventKey + "'"
}

function Get-RecommendedSoundPath {
    param(
        [Parameter(Mandatory = $true)][string]$EventKey
    )

    # Match S-NDB-UND default behavior, but keep it explicit for clarity.
    $safe = $EventKey.Replace(':', '.').Replace('.', '/')
    return "generated/" + $safe
}

$repoRoot = Get-RepoRoot

if ([string]::IsNullOrWhiteSpace($OutDoc)) {
    $OutDoc = (Join-Path $repoRoot 'docs\pregen_sound_bible.md')
}
if ([string]::IsNullOrWhiteSpace($OutManifest)) {
    $OutManifest = (Join-Path $repoRoot 'docs\pregen_sound_manifest.json')
}

$modSoundsPath = Join-Path $repoRoot 'src\main\java\com\kruemblegard\registry\ModSounds.java'
$soundsJsonPath = Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\sounds.json'
$langPath = Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\lang\en_us.json'

if (-not (Test-Path -LiteralPath $modSoundsPath)) { throw "Missing: $modSoundsPath" }
if (-not (Test-Path -LiteralPath $soundsJsonPath)) { throw "Missing: $soundsJsonPath" }
if (-not (Test-Path -LiteralPath $langPath)) { throw "Missing: $langPath" }

$javaText = Get-Content -LiteralPath $modSoundsPath -Raw -Encoding UTF8
$registrations = Get-ModSoundsRegistrations -JavaText $javaText
$soundsJson = Get-SoundsJsonEntries -JsonPath $soundsJsonPath
$lang = Get-LangEntries -LangPath $langPath

$includeMusicEffective = $IncludeMusic.IsPresent

$allKeys = New-Object System.Collections.Generic.HashSet[string]
foreach ($k in $registrations.Keys) { [void]$allKeys.Add($k) }
foreach ($k in $soundsJson.Keys) { [void]$allKeys.Add($k) }

$sortedKeys = $allKeys | Sort-Object

$items = New-Object System.Collections.Generic.List[object]

$docLines = New-Object System.Collections.Generic.List[string]
$docLines.Add('# Pregen Sound Bible (S-NDB-UND)')
$docLines.Add('')
$docLines.Add('This file is auto-generated by `tools/generate_pregen_sound_bible.ps1`.')
$docLines.Add('')
$docLines.Add('Purpose: generate a batch manifest for **S-NDB-UND** so you can export `.ogg` files directly into this mod (`--mc-target forge`).')
$docLines.Add('')
$docLines.Add('## Quick start')
$docLines.Add('')
$docLines.Add('1) Generate the manifest + this doc:')
$docLines.Add('')
$docLines.Add('```powershell')
$docLines.Add('.\\tools\\generate_pregen_sound_bible.ps1')
$docLines.Add('```')
$docLines.Add('')
$docLines.Add('2) Run S-NDB-UND batch export (example):')
$docLines.Add('')
$docLines.Add('```powershell')
$docLines.Add('python -m soundgen.batch `')
$docLines.Add('  --manifest "docs\\pregen_sound_manifest.json" `')
$docLines.Add('  --pack-root "src\\main\\resources" `')
$docLines.Add('  --mc-target forge')
$docLines.Add('```')
$docLines.Add('')
$docLines.Add('Notes:')
$docLines.Add('- This will create/update:')
$docLines.Add('  - `src/main/resources/assets/kruemblegard/sounds.json`')
$docLines.Add('  - `src/main/resources/assets/kruemblegard/lang/en_us.json` (only if subtitle text is provided)')
$docLines.Add('  - `.ogg` files under `src/main/resources/assets/kruemblegard/sounds/`')
$docLines.Add('- Prompts here are starter text; tune them before generating final assets.')
$docLines.Add('')
$docLines.Add('## Entries')
$docLines.Add('')

foreach ($k in $sortedKeys) {
    if (-not $includeMusicEffective -and $k.StartsWith('music.')) {
        continue
    }

    $soundsEntry = $null
    if ($soundsJson.ContainsKey($k)) {
        $soundsEntry = $soundsJson[$k]
    }

    $regField = '(not registered)'
    if ($registrations.ContainsKey($k)) {
        $regField = $registrations[$k]
    }

    $streamed = Get-StreamedFlag -SoundsEntry $soundsEntry

    $subtitleKey = Get-SubtitleKey -SoundsEntry $soundsEntry
    $subtitleText = $null
    if ($subtitleKey -and $lang.ContainsKey($subtitleKey)) {
        $subtitleText = $lang[$subtitleKey]
    }

    $suggestedSubtitleKey = $subtitleKey
    $suggestedSubtitleText = $subtitleText

    if (-not $suggestedSubtitleKey -and -not $k.StartsWith('music.')) {
        $shortName = $k
        if ($shortName.StartsWith('kruemblegard_')) { $shortName = $shortName.Substring('kruemblegard_'.Length) }
        if ($shortName.StartsWith('kruemblegard.')) { $shortName = $shortName.Substring('kruemblegard.'.Length) }
        $suggestedSubtitleKey = "subtitles.kruemblegard.$shortName"

        if ($lang.ContainsKey($suggestedSubtitleKey)) {
            $suggestedSubtitleText = $lang[$suggestedSubtitleKey]
        } else {
            $suggestedSubtitleText = Get-FriendlySubtitle -EventKey $k
        }
    }

    $engine = 'rfxgen'
    $seconds = 3.0
    $variants = [Math]::Max(1, $DefaultVariants)

    if ($k.StartsWith('music.')) {
        $engine = 'diffusers'
        $seconds = 12.0
        $variants = 1
    } elseif ($streamed) {
        $engine = 'diffusers'
        $seconds = 6.0
        $variants = 1
    } elseif ($k -match 'core_hum|radiant') {
        $variants = 1
    }

    $prompt = Get-RecommendedPrompt -EventKey $k -SubtitleText $suggestedSubtitleText
    if ([string]::IsNullOrWhiteSpace($prompt)) {
        $prompt = "TODO: describe sound for event '$k'"
    }

    $soundPath = Get-RecommendedSoundPath -EventKey $k

    $tags = New-Object System.Collections.Generic.List[string]
    $tags.Add('kruemblegard')
    $tags.Add('sound')
    if ($k.StartsWith('music.')) {
        $tags.Add('music')
    } elseif ($streamed) {
        $tags.Add('streamed')
    } else {
        $tags.Add('sfx')
    }

    $item = [ordered]@{
        engine = $engine
        namespace = 'kruemblegard'
        event = $k
        prompt = $prompt
        sound_path = $soundPath
        variants = $variants
        weight = 1
        volume = 1.0
        pitch = 1.0
        post = $true
        tags = $tags.ToArray()
    }

    if ($engine -eq 'diffusers') {
        $item.seconds = $seconds
    }

    if (-not $k.StartsWith('music.')) {
        if ($suggestedSubtitleText) { $item.subtitle = $suggestedSubtitleText }
        if ($suggestedSubtitleKey) { $item.subtitle_key = $suggestedSubtitleKey }
    }

    $items.Add($item)

    $names = @(Get-SoundNames -SoundsEntry $soundsEntry)
    $namesDisplay = '(none)'
    if ($names.Count -gt 0) {
        $namesDisplay = ($names -join ', ')
    }

    $placeholder = $false
    if ($names.Count -gt 0 -and @($names | Select-Object -Unique).Count -eq 1 -and $names[0] -match 'horror-background-atmosphere-09_universfield') {
        $placeholder = $true
    }

    $docLines.Add("### $k")
    $docLines.Add('')
    $docLines.Add('- **SoundEvent**: `kruemblegard:' + $k + '`')
    $docLines.Add(('- **ModSounds field**: `{0}`' -f $regField))
    $docLines.Add("- **Current sounds.json**: $namesDisplay" + $(if ($placeholder) { ' (PLACEHOLDER)' } else { '' }))
    $docLines.Add('- **Streamed**: `' + ([bool]$streamed) + '`')

    if ($subtitleKey) {
            $docLines.Add(('- **Existing subtitle key**: `{0}`' -f $subtitleKey) + $(if ($subtitleText) { ' ("' + $subtitleText + '")' } else { '' }))
    } elseif ($suggestedSubtitleKey) {
            $docLines.Add(('- **Suggested subtitle key**: `{0}`' -f $suggestedSubtitleKey) + $(if ($suggestedSubtitleText) { ' ("' + $suggestedSubtitleText + '")' } else { '' }))
    } else {
        $docLines.Add('- **Subtitle**: (none)')
    }

        $docLines.Add('- **Manifest**: engine=`' + $engine + '`, variants=`' + $variants + '`, sound_path=`' + $soundPath + '`' + $(if ($engine -eq 'diffusers') { ', seconds=`' + $seconds + '`' } else { '' }))
        $docLines.Add('- **Prompt**: "' + $prompt + '"')
    $docLines.Add('')
}

# Write manifest (stable formatting)
$manifestJson = $items | ConvertTo-Json -Depth 12
$manifestJson = $manifestJson.TrimEnd() + "`r`n"

$manifestDir = Split-Path -Parent $OutManifest
if (-not (Test-Path -LiteralPath $manifestDir)) {
    New-Item -ItemType Directory -Path $manifestDir | Out-Null
}
Set-Content -LiteralPath $OutManifest -Value $manifestJson -Encoding UTF8

# Write doc
$docDir = Split-Path -Parent $OutDoc
if (-not (Test-Path -LiteralPath $docDir)) {
    New-Item -ItemType Directory -Path $docDir | Out-Null
}
$docText = ($docLines -join "`r`n").TrimEnd() + "`r`n"
Set-Content -LiteralPath $OutDoc -Value $docText -Encoding UTF8

Write-Host "Wrote: $OutManifest" -ForegroundColor Green
Write-Host "Wrote: $OutDoc" -ForegroundColor Green
