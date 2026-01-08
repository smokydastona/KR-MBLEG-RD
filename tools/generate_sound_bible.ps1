param(
    [string]$WorkspaceRoot = (Resolve-Path "$PSScriptRoot\.." | Select-Object -ExpandProperty Path)
)

$ErrorActionPreference = 'Stop'

$ModId = 'kruemblegard'

$SoundsJsonPath = Join-Path $WorkspaceRoot 'src/main/resources/assets/kruemblegard/sounds.json'
$ModSoundsPath  = Join-Path $WorkspaceRoot 'src/main/java/com/kruemblegard/registry/ModSounds.java'
$ModItemsPath   = Join-Path $WorkspaceRoot 'src/main/java/com/kruemblegard/registry/ModItems.java'
$SoundBiblePath = Join-Path $WorkspaceRoot 'docs/Sound_Bible.md'
$SoundsDir      = Join-Path $WorkspaceRoot 'src/main/resources/assets/kruemblegard/sounds'

function Replace-Section {
    param(
        [Parameter(Mandatory=$true)][string]$FilePath,
        [Parameter(Mandatory=$true)][string]$StartMarker,
        [Parameter(Mandatory=$true)][string]$EndMarker,
        [Parameter(Mandatory=$true)][string]$NewContent
    )

    $text = Get-Content -Raw -Encoding UTF8 $FilePath

    $startIndex = $text.IndexOf($StartMarker)
    $endIndex = $text.IndexOf($EndMarker)

    if ($startIndex -lt 0 -or $endIndex -lt 0 -or $endIndex -le $startIndex) {
        throw "Markers not found or out of order in $FilePath"
    }

    $before = $text.Substring(0, $startIndex + $StartMarker.Length)
    $after  = $text.Substring($endIndex)

    $replacement = "`r`n`r`n$NewContent`r`n`r`n"
    Set-Content -Encoding UTF8 -NoNewline -Path $FilePath -Value ($before + $replacement + $after)
}

function Parse-ModSounds {
    $text = Get-Content -Raw -Encoding UTF8 $ModSoundsPath

    $results = @()

    foreach ($m in [regex]::Matches(
        $text,
        'public\s+static\s+final\s+RegistryObject<SoundEvent>\s+(?<field>[A-Z0-9_]+)\s*=\s*SOUNDS\.register\(\s*"(?<id>[^"]+)"',
        [System.Text.RegularExpressions.RegexOptions]::Singleline
    )) {
        $results += [pscustomobject]@{ Field = $m.Groups['field'].Value; Id = $m.Groups['id'].Value; Source = 'SOUNDS.register' }
    }

    foreach ($m in [regex]::Matches(
        $text,
        'public\s+static\s+final\s+RegistryObject<SoundEvent>\s+(?<field>[A-Z0-9_]+)\s*=\s*register\(\s*"(?<id>[^"]+)"\s*\)',
        [System.Text.RegularExpressions.RegexOptions]::Singleline
    )) {
        $results += [pscustomobject]@{ Field = $m.Groups['field'].Value; Id = $m.Groups['id'].Value; Source = 'register() helper' }
    }

    $results | Sort-Object Id, Field
}

function Parse-SoundsJson {
    $json = Get-Content -Raw -Encoding UTF8 $SoundsJsonPath | ConvertFrom-Json

    $map = @{}

    foreach ($prop in $json.PSObject.Properties) {
        $key = $prop.Name
        $value = $prop.Value

        $subtitle = $null
        if ($value.PSObject.Properties.Name -contains 'subtitle') {
            $subtitle = $value.subtitle
        }

        $soundEntries = @()
        if ($value.PSObject.Properties.Name -contains 'sounds') {
            foreach ($s in $value.sounds) {
                $name = $null
                $stream = $false

                if ($s -is [string]) {
                    $name = $s
                } else {
                    $name = $s.name
                    if ($s.PSObject.Properties.Name -contains 'stream') {
                        $stream = [bool]$s.stream
                    }
                }

                $soundEntries += [pscustomobject]@{
                    Name = $name
                    Stream = $stream
                }
            }
        }

        $map[$key] = [pscustomobject]@{
            Subtitle = $subtitle
            Sounds = $soundEntries
        }
    }

    return $map
}

function Parse-RecordItemsSoundLengths {
    if (-not (Test-Path $ModItemsPath)) {
        return @{}
    }

    $text = Get-Content -Raw -Encoding UTF8 $ModItemsPath
    $map = @{}

    foreach ($m in [regex]::Matches($text, 'new\s+RecordItem\(\s*(?<comp>\d+)\s*,\s*(?<snd>[A-Za-z0-9_.]+)\s*,[\s\S]*?,\s*(?<len>\d+)\s*\)', [System.Text.RegularExpressions.RegexOptions]::Singleline)) {
        $soundToken = $m.Groups['snd'].Value
        $len = [int]$m.Groups['len'].Value

        # soundToken can be ModSounds.WAYFALL_MUSIC or similar
        if (-not $map.ContainsKey($soundToken)) {
            $map[$soundToken] = $len
        }
    }

    return $map
}

function Resolve-OggPathsFromName {
    param([Parameter(Mandatory=$true)][string]$SoundName)

    # SoundName is typically like "kruemblegard:kruemblegard_ambient" or "kruemblegard:attack_smash"
    $parts = $SoundName.Split(':', 2)
    if ($parts.Length -ne 2) {
        return @()
    }

    $namespace = $parts[0]
    $path = $parts[1]

    if ($namespace -ne $ModId) {
        return @()
    }

    $candidate = Join-Path $SoundsDir ($path + '.ogg')
    return @($candidate)
}

function Render-SoundsMarkdown {
    param(
        [Parameter(Mandatory=$true)]$AllSoundIds,
        [Parameter(Mandatory=$true)]$ModSoundsById,
        [Parameter(Mandatory=$true)]$SoundsJsonMap,
        [Parameter(Mandatory=$true)]$RecordSoundLengths
    )

    $tick = '`'
    $lines = New-Object System.Collections.Generic.List[string]
    $lines.Add('### Sounds (All Registered / Declared)')
    $lines.Add('')

    foreach ($id in ($AllSoundIds | Sort-Object)) {
        $lines.Add("#### $id")
        $lines.Add(("- **SoundEvent ID**: {0}{1}:{2}{0}" -f $tick, $ModId, $id))

        if ($ModSoundsById.ContainsKey($id)) {
            $field = $ModSoundsById[$id].Field
            $lines.Add(("- **ModSounds field**: {0}{1}{0}" -f $tick, $field))
        } else {
            $lines.Add('- **ModSounds field**: (not registered in ModSounds)')
        }

        $jsonEntry = $null
        if ($SoundsJsonMap.ContainsKey($id)) {
            $jsonEntry = $SoundsJsonMap[$id]
        }

        if ($null -ne $jsonEntry -and $jsonEntry.Subtitle) {
            $lines.Add(("- **Subtitle key**: {0}{1}{0}" -f $tick, $jsonEntry.Subtitle))
        } else {
            $lines.Add('- **Subtitle key**: (none)')
        }

        $streamFlags = @()
        $soundNames = @()
        $fileStates = @()

        if ($null -ne $jsonEntry) {
            foreach ($s in $jsonEntry.Sounds) {
                if ($s.Name) { $soundNames += $s.Name }
                $streamFlags += [bool]$s.Stream

                foreach ($p in (Resolve-OggPathsFromName -SoundName $s.Name)) {
                    $exists = Test-Path $p
                    $rel = $p.Substring($WorkspaceRoot.Length).TrimStart([char[]]@('\','/')) -replace '\\','/'
                    $fileStates += "$rel = $exists"
                }
            }
        }

        if ($soundNames.Count -gt 0) {
            $lines.Add(("- **sounds.json name(s)**: {0}" -f ($soundNames -join ', ')))
        } else {
            $lines.Add('- **sounds.json name(s)**: (missing in sounds.json)')
        }

        if ($streamFlags.Count -gt 0) {
            $anyStream = ($streamFlags | Where-Object { $_ } | Measure-Object).Count -gt 0
            $lines.Add(("- **Streamed**: {0}{1}{0}" -f $tick, ($(if ($anyStream) { 'true' } else { 'false' }))))
        } else {
            $lines.Add(("- **Streamed**: {0}TBD{0}" -f $tick))
        }

        if ($fileStates.Count -gt 0) {
            $lines.Add(("- **OGG present**: {0}" -f ($fileStates -join '; ')))
        } else {
            $lines.Add('- **OGG present**: (unknown)')
        }

        # Exact length requirements (placeholders to be filled by hand)
        $lines.Add(("- **DurationSeconds**: {0}TBD{0}" -f $tick))
        $lines.Add(("- **DurationTicks**: {0}TBD{0} (round(seconds*20))" -f $tick))

        # If this sound is used by a RecordItem, show expected lengthTicks
        if ($ModSoundsById.ContainsKey($id)) {
            $field = $ModSoundsById[$id].Field
            $token = "ModSounds.$field"
            if ($RecordSoundLengths.ContainsKey($token)) {
                $lines.Add(("- **RecordItem lengthTicks**: {0}{1}{0}" -f $tick, $RecordSoundLengths[$token]))
            }
        }

        $lines.Add('')
    }

    return ($lines -join "`r`n")
}

if (-not (Test-Path $SoundsJsonPath)) { throw "Missing $SoundsJsonPath" }
if (-not (Test-Path $ModSoundsPath))  { throw "Missing $ModSoundsPath" }
if (-not (Test-Path $SoundBiblePath)) { throw "Missing $SoundBiblePath" }

$modSounds = Parse-ModSounds
$soundsJsonMap = Parse-SoundsJson

$allIds = New-Object System.Collections.Generic.HashSet[string]
foreach ($s in $modSounds) { [void]$allIds.Add($s.Id) }
foreach ($k in $soundsJsonMap.Keys) { [void]$allIds.Add($k) }

$modSoundsById = @{}
foreach ($s in $modSounds) {
    if (-not $modSoundsById.ContainsKey($s.Id)) {
        $modSoundsById[$s.Id] = $s
    }
}

$recordSoundLengths = Parse-RecordItemsSoundLengths

$md = Render-SoundsMarkdown -AllSoundIds ($allIds | Sort-Object) -ModSoundsById $modSoundsById -SoundsJsonMap $soundsJsonMap -RecordSoundLengths $recordSoundLengths

Replace-Section -FilePath $SoundBiblePath -StartMarker '<!-- AUTO-GENERATED:SOUNDS:START -->' -EndMarker '<!-- AUTO-GENERATED:SOUNDS:END -->' -NewContent $md

Write-Host "Updated: $SoundBiblePath"
