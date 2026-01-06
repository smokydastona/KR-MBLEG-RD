param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path,
    [string]$PlantBiblePath = "docs/Plant_Material_Bible.md",
    [string]$FloraReferencePath = "docs/FLORA_REFERENCE.md",
    [string]$AssetsTexturesDir = "src/main/resources/assets/kruemblegard/textures"
)

$ErrorActionPreference = 'Stop'

function Get-TextContent {
    param([string]$path)
    $full = Join-Path $RepoRoot $path
    if (-not (Test-Path -LiteralPath $full)) {
        throw "Missing file: $path ($full)"
    }
    return Get-Content -LiteralPath $full -Raw
}

$bible = Get-TextContent $PlantBiblePath
$flora = Get-TextContent $FloraReferencePath

# Extract texture references from the bible.
$texturePattern = 'assets/kruemblegard/textures/(block|item)/[A-Za-z0-9_./-]+\.png'
$textureRefs = [regex]::Matches($bible, $texturePattern) | ForEach-Object { $_.Value } | Sort-Object -Unique

Write-Host ("Found {0} unique texture references in {1}" -f $textureRefs.Count, $PlantBiblePath)

# Verify referenced textures exist on disk.
$assetsRoot = Join-Path $RepoRoot $AssetsTexturesDir
$missingTextures = @()
foreach ($ref in $textureRefs) {
    $rel = $ref -replace '^assets/kruemblegard/textures/', ''
    $fullPath = Join-Path $assetsRoot $rel
    if (-not (Test-Path -LiteralPath $fullPath)) {
        $missingTextures += $ref
    }
}

# Verify referenced texture stems exist somewhere in FLORA_REFERENCE (helps prevent doc drift).
$missingInFlora = @()
foreach ($ref in $textureRefs) {
    $stem = [System.IO.Path]::GetFileNameWithoutExtension($ref)
    if ($flora -notmatch [regex]::Escape($stem)) {
        $missingInFlora += $stem
    }
}
$missingInFlora = $missingInFlora | Sort-Object -Unique

if ($missingTextures.Count -gt 0) {
    Write-Host "\nMISSING (asset file not found):"
    foreach ($m in $missingTextures) { Write-Host (" - {0}" -f $m) }
}

if ($missingInFlora.Count -gt 0) {
    Write-Host "\nPOTENTIALLY MISSING IN FLORA_REFERENCE (no text match for texture stem):"
    foreach ($m in $missingInFlora) { Write-Host (" - {0}" -f $m) }
    Write-Host "\nNote: This is a heuristic check (text match), not a registry scan."
}

if ($missingTextures.Count -eq 0 -and $missingInFlora.Count -eq 0) {
    Write-Host "\nOK: Plant Material Bible matches on-disk textures and FLORA_REFERENCE mentions." 
    exit 0
}

if ($missingTextures.Count -gt 0) { exit 2 }
exit 1
