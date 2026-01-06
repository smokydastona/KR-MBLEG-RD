# Audits generated Wayfall wood-family textures for exact duplicates (byte-identical PNGs).
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/audit_duplicate_wood_textures.ps1
#
# By default this only inspects the wood-family textures that the generator targets,
# based on the Master Palette families in docs/Wood_Material_Bible.md.

param(
    [string]$MaterialBiblePath = 'docs/Wood_Material_Bible.md',
    [string]$AssetsTexturesDir = 'src/main/resources/assets/kruemblegard/textures',
    [switch]$IncludeStripped,
    [int]$MaxGroups = 50
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path '.'
$MaterialBiblePath = Join-Path $repoRoot $MaterialBiblePath
$AssetsTexturesDir = Join-Path $repoRoot $AssetsTexturesDir

if (-not (Test-Path -LiteralPath $MaterialBiblePath)) {
    throw "Material bible not found: $MaterialBiblePath"
}
if (-not (Test-Path -LiteralPath $AssetsTexturesDir)) {
    throw "Assets textures dir not found: $AssetsTexturesDir"
}

# Parse Master Palette table to get family names
$lines = Get-Content -LiteralPath $MaterialBiblePath
$inTable = $false
$families = New-Object System.Collections.Generic.List[string]

foreach ($line in $lines) {
    if ($line -match '^##\s+Master Palette \(RGB\)') {
        $inTable = $true
        continue
    }

    if ($inTable) {
        if ($line -match '^---$' -or $line -match '^##\s+') { break }
        if ([string]::IsNullOrWhiteSpace($line)) { continue }
        if ($line.Trim() -like '|---*') { continue }
        if ($line.Trim().StartsWith('| Family')) { continue }
        if ($line -notmatch '^\|') { continue }

        $cells = $line.Trim('|').Split('|') | ForEach-Object { $_.Trim() }
        if ($cells.Count -lt 4) { continue }
        $family = $cells[0]
        if ($family) { $families.Add($family) | Out-Null }
    }
}

if ($families.Count -eq 0) {
    throw 'No families parsed from Master Palette table.'
}

$familyToPrefix = @{
    'Cairn' = 'cairn_tree'
    'Hollowway' = 'hollowway_tree'
    'Waytorch' = 'waytorch_tree'
    'Shardbark' = 'shardbark_pine'
    'Monument Oak' = 'monument_oak'
}

function Family-ToPrefix {
    param([string]$family)
    if ($familyToPrefix.ContainsKey($family)) { return $familyToPrefix[$family] }
    return ($family.ToLowerInvariant() -replace '[^a-z0-9]+', '_')
}

$blockDir = Join-Path $AssetsTexturesDir 'block'
$itemDir  = Join-Path $AssetsTexturesDir 'item'

$files = New-Object System.Collections.Generic.List[string]

foreach ($family in $families) {
    $prefix = Family-ToPrefix $family

    Get-ChildItem -LiteralPath $blockDir -Filter "${prefix}*.png" -File -ErrorAction SilentlyContinue | ForEach-Object { $files.Add($_.FullName) | Out-Null }
    Get-ChildItem -LiteralPath $itemDir -Filter "${prefix}*.png" -File -ErrorAction SilentlyContinue | ForEach-Object { $files.Add($_.FullName) | Out-Null }

    if ($IncludeStripped) {
        Get-ChildItem -LiteralPath $blockDir -Filter "stripped_${prefix}*.png" -File -ErrorAction SilentlyContinue | ForEach-Object { $files.Add($_.FullName) | Out-Null }
    } else {
        # Always include stripped logs (these are part of the normal generator target set)
        Get-ChildItem -LiteralPath $blockDir -Filter "stripped_${prefix}_log.png" -File -ErrorAction SilentlyContinue | ForEach-Object { $files.Add($_.FullName) | Out-Null }
    }
}

$files = @($files | Sort-Object -Unique)

Write-Host ("Scanning {0} wood-family texture files..." -f $files.Count)

$byHash = @{}
foreach ($path in $files) {
    $h = (Get-FileHash -Algorithm SHA256 -LiteralPath $path).Hash
    if (-not $byHash.ContainsKey($h)) {
        $byHash[$h] = New-Object System.Collections.Generic.List[string]
    }
    $byHash[$h].Add($path) | Out-Null
}

$dupeGroups = @(
    $byHash.GetEnumerator() |
        Where-Object { $_.Value.Count -gt 1 } |
        Sort-Object { $_.Value.Count } -Descending
)

Write-Host ("Duplicate groups found: {0}" -f $dupeGroups.Count)

if ($dupeGroups.Count -gt 0) {
    $shown = 0
    foreach ($g in $dupeGroups) {
        $shown++
        Write-Host "---"
        Write-Host ("Count: {0}" -f $g.Value.Count)
        $g.Value | ForEach-Object { Write-Host $_ }
        if ($shown -ge $MaxGroups) { break }
    }
}

# Exit code: 0 if no duplicates, 1 if duplicates exist.
if ($dupeGroups.Count -gt 0) { exit 1 }
exit 0
