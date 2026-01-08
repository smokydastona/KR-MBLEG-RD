param(
    [string]$RepoRoot
)

if (-not $PSScriptRoot) {
    $PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
}

if (-not $RepoRoot) {
    $RepoRoot = Split-Path $PSScriptRoot -Parent
}

$assetsTexturesDir = Join-Path $RepoRoot "src\main\resources\assets\kruemblegard\textures"
$modelsBlockDir = Join-Path $RepoRoot "src\main\resources\assets\kruemblegard\models\block"
$plantBible = Join-Path $RepoRoot "docs\Plant_Material_Bible.md"

if (-not (Test-Path $assetsTexturesDir)) { throw "Missing assets textures dir: $assetsTexturesDir" }
if (-not (Test-Path $modelsBlockDir)) { throw "Missing block models dir: $modelsBlockDir" }
if (-not (Test-Path $plantBible)) { throw "Missing plant bible: $plantBible" }

function Normalize-Key {
    param([string]$name)
    if (-not $name) { return '' }
    $k = $name.ToLowerInvariant().Trim()
    $k = $k -replace '\(charged\)', 'charged'
    $k = $k -replace '[^a-z0-9]+', '_'
    $k = $k.Trim('_')
    return $k
}

# Parse palette keys so we can force-include surface blocks
$lines = Get-Content -Path $plantBible
$inTable = $false
$keys = New-Object System.Collections.Generic.HashSet[string]
foreach ($line in $lines) {
    if ($line -match '^##\s+Master Palette \(RGB\)') { $inTable = $true; continue }
    if ($inTable) {
        if ($line -match '^---$' -or $line -match '^##\s+') { break }
        if ($line -match '^\|\s*Plant\s*\|') { continue }
        if ($line -match '^\|---') { continue }
        if ($line -match '^\|\s*(.+?)\s*\|') {
            $keys.Add((Normalize-Key $matches[1])) | Out-Null
        }
    }
}

$blockTextures = New-Object System.Collections.Generic.HashSet[string]

Get-ChildItem -Path $modelsBlockDir -Filter "*.json" -File | ForEach-Object {
    $raw = Get-Content -Path $_.FullName -Raw
    try { $j = $raw | ConvertFrom-Json } catch { return }
    $parent = ''
    if ($null -ne $j.parent) { $parent = [string]$j.parent }
    $isCross = $false
    if ($parent -match 'cross') { $isCross = $true }

    if (-not $isCross) { return }
    if ($null -ne $j.textures) {
        foreach ($prop in $j.textures.PSObject.Properties) {
            $tex = [string]$prop.Value
            if ($tex -match '^kruemblegard:block/(.+)$') {
                $blockTextures.Add($matches[1]) | Out-Null
            }
        }
    }
}

# Force include bible-defined surface/ground textures
foreach ($k in $keys) {
    switch ($k) {
        'ashmoss' { $blockTextures.Add('ashmoss') | Out-Null }
        'runegrowth' {
            $blockTextures.Add('runegrowth_top') | Out-Null
            $blockTextures.Add('runegrowth_side') | Out-Null
        }
        'voidfelt' {
            $blockTextures.Add('voidfelt_top') | Out-Null
            $blockTextures.Add('voidfelt_side') | Out-Null
        }
        'fault_dust' { $blockTextures.Add('fault_dust') | Out-Null }
        'cairn_moss' { $blockTextures.Add('cairn_moss') | Out-Null }
    }
}

$files = @()
foreach ($name in $blockTextures) {
    $path = Join-Path $assetsTexturesDir ("block/" + $name + ".png")
    if (Test-Path $path) { $files += $path }
}

Write-Host "Scanning" $files.Count "plant texture files..."

$sha = [System.Security.Cryptography.SHA256]::Create()
try {
    $byHash = @{}
    foreach ($f in $files) {
        $bytes = [System.IO.File]::ReadAllBytes($f)
        $hash = [BitConverter]::ToString($sha.ComputeHash($bytes)).Replace("-", "")
        if (-not $byHash.ContainsKey($hash)) { $byHash[$hash] = @() }
        $byHash[$hash] += $f
    }

    $dupes = $byHash.GetEnumerator() | Where-Object { $_.Value.Count -gt 1 }
    $groups = @($dupes)

    Write-Host "Duplicate groups found:" $groups.Count
    if ($groups.Count -gt 0) {
        foreach ($g in $groups) {
            Write-Host "- Hash" $g.Key "(" $g.Value.Count "files)"
            foreach ($p in ($g.Value | Sort-Object)) {
                $rel = $p.Substring($RepoRoot.Length).TrimStart('\\','/')
                Write-Host "    " $rel
            }
        }
        exit 1
    }
} finally {
    $sha.Dispose()
}

exit 0
