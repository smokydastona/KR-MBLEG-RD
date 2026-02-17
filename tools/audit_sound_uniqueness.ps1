<#
.SYNOPSIS
  Audits custom sound usage across mobs.

.DESCRIPTION
  Checks that each mob's custom sounds don't reuse the same underlying sound resource as another mob
  (except allowlisted mobs), and verifies referenced OGG files exist in assets.

  This is intended as a shipping checklist helper whenever adding/changing entity sounds.

.EXAMPLE
  powershell -NoProfile -ExecutionPolicy Bypass -File tools\audit_sound_uniqueness.ps1
#>

$ErrorActionPreference = 'Stop'

$workspaceRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$soundsJsonPath = Join-Path $workspaceRoot 'src/main/resources/assets/kruemblegard/sounds.json'
$assetsRoot = Join-Path $workspaceRoot 'src/main/resources/assets/kruemblegard/sounds'

if (!(Test-Path $soundsJsonPath)) {
  throw "Missing sounds.json at: $soundsJsonPath"
}

$raw = Get-Content -Raw -Path $soundsJsonPath
$data = $raw | ConvertFrom-Json

# Mobs that intentionally keep vanilla audio (or otherwise excluded from uniqueness rules)
$excludedMobs = @('moogloom', 'scattered_enderman')

function TryGet-MobFromKey([string] $key) {
  if ($key -match '^entity\.([a-z0-9_]+)\.') {
    return $Matches[1]
  }

  # Boss top-level events live outside entity.* in this mod
  if ($key -match '^kruemblegard(_|\.)') {
    return 'kruemblegard'
  }

  return $null
}

function Get-SoundNames($soundsArray) {
  $names = @()
  foreach ($entry in $soundsArray) {
    if ($null -eq $entry) { continue }

    # Most entries are objects like {"name": "kruemblegard:entity/..."}
    if ($entry.PSObject.Properties.Name -contains 'name') {
      $names += [string]$entry.name
      continue
    }

    # Some packs use plain strings
    if ($entry -is [string]) {
      $names += $entry
    }
  }
  return $names
}

function Resolve-AssetPathFromResource([string] $resourceName) {
  # Only validate mod-local assets
  if ($resourceName -notmatch '^kruemblegard:') { return $null }

  $path = $resourceName.Substring('kruemblegard:'.Length)
  # sounds.json paths use forward slashes; assets on disk use backslashes
  $path = $path -replace '/', '\\'
  return Join-Path $assetsRoot ($path + '.ogg')
}

$resourceToMobs = @{}
$missingAssets = New-Object System.Collections.Generic.List[string]

foreach ($prop in $data.PSObject.Properties) {
  $key = $prop.Name
  $value = $prop.Value

  $mob = TryGet-MobFromKey $key
  if ($null -eq $mob) { continue }
  if ($excludedMobs -contains $mob) { continue }

  if ($null -eq $value.sounds) { continue }
  $names = Get-SoundNames $value.sounds

  foreach ($name in $names) {
    if ([string]::IsNullOrWhiteSpace($name)) { continue }

    if (!$resourceToMobs.ContainsKey($name)) {
      $resourceToMobs[$name] = New-Object System.Collections.Generic.HashSet[string]
    }
    [void]$resourceToMobs[$name].Add($mob)

    $assetPath = Resolve-AssetPathFromResource $name
    if ($null -ne $assetPath -and !(Test-Path $assetPath)) {
      $missingAssets.Add("$key -> $name (missing $assetPath)")
    }
  }
}

$duplicates = @()
foreach ($kvp in $resourceToMobs.GetEnumerator()) {
  $mobs = @($kvp.Value)
  if ($mobs.Count -gt 1) {
    $duplicates += [PSCustomObject]@{
      Resource = $kvp.Key
      Mobs = ($mobs | Sort-Object) -join ', '
    }
  }
}

Write-Host "Sound audit: $(Get-Date -Format s)" -ForegroundColor Cyan
Write-Host "- sounds.json: $soundsJsonPath"
Write-Host "- assets root: $assetsRoot"

if ($missingAssets.Count -gt 0) {
  Write-Host "\nMissing OGG assets:" -ForegroundColor Red
  foreach ($m in $missingAssets) { Write-Host "- $m" }
}

if ($duplicates.Count -gt 0) {
  Write-Host "\nDuplicate sound resource usage across mobs:" -ForegroundColor Yellow
  foreach ($d in $duplicates | Sort-Object Resource) {
    Write-Host "- $($d.Resource) used by: $($d.Mobs)"
  }
}

if ($missingAssets.Count -eq 0 -and $duplicates.Count -eq 0) {
  Write-Host "\nOK: No missing assets and no cross-mob duplicates (excluding: $($excludedMobs -join ', '))." -ForegroundColor Green
  exit 0
}

Write-Host "\nFAIL: Fix issues above before shipping." -ForegroundColor Red
exit 1
