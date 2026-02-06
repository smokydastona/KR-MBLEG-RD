[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Get-RepoRoot {
    $root = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')
    return $root.Path
}

function Assert-PowerShellParses {
    param(
        [Parameter(Mandatory = $true)][string]$Path
    )

    $raw = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
    try {
        [void][scriptblock]::Create($raw)
    } catch {
        throw "PowerShell parse failed: $Path`n$($_.Exception.Message)"
    }
}

$repoRoot = Get-RepoRoot

$materialBibles = Join-Path $repoRoot 'tools\generate_material_bibles.ps1'
$soundBible = Join-Path $repoRoot 'tools\generate_sound_bible.ps1'
$lootBible = Join-Path $repoRoot 'tools\generate_loot_table_bible.ps1'
$wayfallBiomes = Join-Path $repoRoot 'tools\generate_wayfall_biome_vegetation_report.ps1'

Write-Host "Running generators..." -ForegroundColor Cyan

if (-not (Test-Path -LiteralPath $materialBibles)) {
    throw "Missing generator script: $materialBibles"
}

if (-not (Test-Path -LiteralPath $soundBible)) {
    throw "Missing generator script: $soundBible"
}

if (-not (Test-Path -LiteralPath $lootBible)) {
    throw "Missing generator script: $lootBible"
}

if (-not (Test-Path -LiteralPath $wayfallBiomes)) {
    throw "Missing generator script: $wayfallBiomes"
}

Assert-PowerShellParses -Path $materialBibles
Assert-PowerShellParses -Path $soundBible
Assert-PowerShellParses -Path $lootBible
Assert-PowerShellParses -Path $wayfallBiomes

& $materialBibles
& $soundBible
& $lootBible
& $wayfallBiomes

Write-Host "Done." -ForegroundColor Green
