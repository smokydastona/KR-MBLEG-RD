[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$autoBegin = '<!-- BEGIN AUTO-GENERATED: wayfall_biome_vegetation -->'
$autoEnd = '<!-- END AUTO-GENERATED: wayfall_biome_vegetation -->'

function Get-RepoRoot {
    $root = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')
    return $root.Path
}

function Read-JsonFile {
    param(
        [Parameter(Mandatory = $true)][string]$Path
    )

    $raw = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
    return $raw | ConvertFrom-Json
}

function Resolve-Tag {
    param(
        [Parameter(Mandatory = $true)][hashtable]$Tags,
        [Parameter(Mandatory = $true)][string]$Tag,
        [Parameter()][hashtable]$Seen
    )

    if ($null -eq $Seen) {
        $Seen = @{}
    }

    if ($Seen.ContainsKey($Tag)) {
        return @()
    }
    $Seen[$Tag] = $true

    $values = @()
    if ($Tags.ContainsKey($Tag)) {
        $values = @($Tags[$Tag])
    }

    $out = New-Object System.Collections.Generic.List[string]
    foreach ($v in $values) {
        if ($v -is [string] -and $v.StartsWith('#')) {
            foreach ($x in (Resolve-Tag -Tags $Tags -Tag $v -Seen $Seen)) {
                [void]$out.Add($x)
            }
        } elseif ($v -is [string]) {
            [void]$out.Add($v)
        }
    }
    return $out.ToArray()
}

function Resolve-Biomes {
    param(
        [Parameter(Mandatory = $true)][hashtable]$Tags,
        [Parameter(Mandatory = $true)][hashtable]$BiomeTemps,
        [Parameter()][object]$Spec
    )

    if ($null -eq $Spec) {
        return @()
    }

    if ($Spec -is [System.Collections.IEnumerable] -and -not ($Spec -is [string])) {
        $out = New-Object System.Collections.Generic.List[string]
        foreach ($s in $Spec) {
            foreach ($b in (Resolve-Biomes -Tags $Tags -BiomeTemps $BiomeTemps -Spec $s)) {
                [void]$out.Add($b)
            }
        }
        return $out.ToArray()
    }

    if (-not ($Spec -is [string])) {
        return @()
    }

    if ($Spec.StartsWith('#')) {
        $resolved = Resolve-Tag -Tags $Tags -Tag $Spec
        return @($resolved | Where-Object { $BiomeTemps.ContainsKey($_) })
    }

    if ($BiomeTemps.ContainsKey($Spec)) {
        return @($Spec)
    }

    return @()
}

function Replace-BetweenMarkers {
    param(
        [Parameter(Mandatory = $true)][string]$Text,
        [Parameter(Mandatory = $true)][string]$Begin,
        [Parameter(Mandatory = $true)][string]$End,
        [Parameter(Mandatory = $true)][string]$Replacement
    )

    $beginIdx = $Text.IndexOf($Begin)
    $endIdx = $Text.IndexOf($End)

    if ($beginIdx -lt 0 -or $endIdx -lt 0 -or $endIdx -lt $beginIdx) {
        throw "Could not find expected markers in docs file: $Begin / $End"
    }

    $beginClose = $beginIdx + $Begin.Length
    return $Text.Substring(0, $beginClose) + "`n" + $Replacement + $Text.Substring($endIdx)
}

$repoRoot = Get-RepoRoot

$dataRoot = Join-Path $repoRoot 'src\main\resources\data\kruemblegard'
$biomeDir = Join-Path $dataRoot 'worldgen\biome'
$tagDir = Join-Path $dataRoot 'tags\worldgen\biome'
$modifierDir = Join-Path $dataRoot 'forge\biome_modifier'

$outDoc = Join-Path $repoRoot 'docs\WAYFALL_BIOMES.md'

if (-not (Test-Path -LiteralPath $outDoc)) {
    throw "Missing output doc: $outDoc"
}

# Load biome temps.
$biomeTemps = @{}
Get-ChildItem -LiteralPath $biomeDir -Filter '*.json' | Sort-Object Name | ForEach-Object {
    $biomeId = 'kruemblegard:' + $_.BaseName
    $data = Read-JsonFile -Path $_.FullName
    $biomeTemps[$biomeId] = $data.temperature
}

# Load tags.
$tags = @{}
Get-ChildItem -LiteralPath $tagDir -Filter '*.json' | Sort-Object Name | ForEach-Object {
    $tagId = '#kruemblegard:' + $_.BaseName
    $data = Read-JsonFile -Path $_.FullName
    $vals = @()
    if ($null -ne $data.values) {
        $vals = @($data.values)
    }
    $tags[$tagId] = $vals
}

# Load vegetation features injected via biome modifiers.
$vegByBiome = @{}
foreach ($b in $biomeTemps.Keys) {
    $vegByBiome[$b] = New-Object System.Collections.Generic.List[string]
}

Get-ChildItem -LiteralPath $modifierDir -Filter '*.json' | Sort-Object Name | ForEach-Object {
    $data = Read-JsonFile -Path $_.FullName

    if ($data.type -ne 'forge:add_features') {
        return
    }
    if ($data.step -ne 'vegetal_decoration') {
        return
    }

    $features = @()
    if ($null -ne $data.features) {
        $features = @($data.features)
    }
    if ($features.Count -eq 0) {
        return
    }

    $resolvedBiomes = Resolve-Biomes -Tags $tags -BiomeTemps $biomeTemps -Spec $data.biomes
    foreach ($biomeId in $resolvedBiomes) {
        foreach ($f in $features) {
            if ($f -is [string]) {
                [void]$vegByBiome[$biomeId].Add($f)
            }
        }
    }
}

# De-dupe per biome (preserve order).
foreach ($biomeId in @($vegByBiome.Keys)) {
    $seen = @{}
    $dedup = New-Object System.Collections.Generic.List[string]
    foreach ($f in $vegByBiome[$biomeId]) {
        if ($seen.ContainsKey($f)) {
            continue
        }
        $seen[$f] = $true
        [void]$dedup.Add($f)
    }
    $vegByBiome[$biomeId] = $dedup
}

# Buckets from tags (Void overrides if present).
$biomeBucket = @{}
$bucketTags = @{
    Cold = '#kruemblegard:wayfall_cold'
    Temperate = '#kruemblegard:wayfall_temperate'
    Warm = '#kruemblegard:wayfall_warm'
    Void = '#kruemblegard:wayfall_void'
}

foreach ($bucket in @('Cold', 'Temperate', 'Warm', 'Void')) {
    $tag = $bucketTags[$bucket]
    foreach ($b in (Resolve-Tag -Tags $tags -Tag $tag)) {
        if (-not $biomeTemps.ContainsKey($b)) {
            continue
        }

        if ($bucket -eq 'Void') {
            $biomeBucket[$b] = 'Void'
        } elseif (-not $biomeBucket.ContainsKey($b)) {
            $biomeBucket[$b] = $bucket
        }
    }
}

$treeRegex = [regex]'(tree|sapling|saplings|huge_mushroom|mushroom|giant_|mega_)'

function Format-Temp {
    param([Parameter()][object]$Temp)
    if ($Temp -is [double] -or $Temp -is [float] -or $Temp -is [int] -or $Temp -is [decimal]) {
        return ([double]$Temp).ToString('0.00')
    }
    return '?'
}

$today = (Get-Date).ToString('yyyy-MM-dd')

$rows = foreach ($biomeId in $biomeTemps.Keys) {
    $temp = $biomeTemps[$biomeId]
    [pscustomobject]@{
        BiomeId      = $biomeId
        Temperature  = $temp
        _TempIsNull  = [int]($null -eq $temp)
        _TempSortVal = if ($null -eq $temp) { 0.0 } else { [double]$temp }
    }
}

$rows = @($rows) | Sort-Object _TempIsNull, _TempSortVal, BiomeId

$lines = New-Object System.Collections.Generic.List[string]
[void]$lines.Add("Generated on $today from worldgen JSON.")
[void]$lines.Add('')
[void]$lines.Add('Notes: this lists placed features injected via Forge biome modifiers in the vegetal_decoration step.')
[void]$lines.Add('Classification is best-effort: anything with tree, sapling, mushroom, giant_, or mega_ is listed under Trees.')
[void]$lines.Add('')

foreach ($row in $rows) {
    $biomeId = $row.BiomeId
    $tempS = Format-Temp -Temp $row.Temperature
    $bucket = 'Unbucketed'
    if ($biomeBucket.ContainsKey($biomeId)) {
        $bucket = $biomeBucket[$biomeId]
    }

    $feats = @($vegByBiome[$biomeId])
    $trees = @($feats | Where-Object { $treeRegex.IsMatch($_) })
    $plants = @($feats | Where-Object { -not $treeRegex.IsMatch($_) })

    [void]$lines.Add("### $biomeId - temperature $tempS ($bucket)")

    if ($trees.Count -gt 0) {
        [void]$lines.Add('- Trees')
        foreach ($t in $trees) {
            [void]$lines.Add("  - $t")
        }
    } else {
        [void]$lines.Add('- Trees: (none injected)')
    }

    if ($plants.Count -gt 0) {
        [void]$lines.Add('- Plants')
        foreach ($p in $plants) {
            [void]$lines.Add("  - $p")
        }
    } else {
        [void]$lines.Add('- Plants: (none injected)')
    }

    [void]$lines.Add('')
}

$generated = ($lines -join "`n").TrimEnd() + "`n"

$original = Get-Content -LiteralPath $outDoc -Raw -Encoding UTF8
$updated = Replace-BetweenMarkers -Text $original -Begin $autoBegin -End $autoEnd -Replacement $generated

if ($updated -ne $original) {
    Set-Content -LiteralPath $outDoc -Value $updated -Encoding UTF8
}
