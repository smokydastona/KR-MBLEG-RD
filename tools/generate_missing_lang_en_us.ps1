param(
  [string]$ModId = "kruemblegard",
  [string]$Lang = "en_us"
)

$ErrorActionPreference = "Stop"

function ConvertTo-Hashtable {
  param([Parameter(Mandatory = $true)]$Object)

  if ($null -eq $Object) { return @{} }

  if ($Object -is [hashtable]) { return $Object }

  $ht = @{}
  foreach ($p in $Object.PSObject.Properties) {
    $ht[$p.Name] = $p.Value
  }
  return $ht
}

function Humanize-Id {
  param([Parameter(Mandatory = $true)][string]$Id)

  # Special cases
  if ($Id -match "^(.*)_spawn_egg$") {
    $base = $Matches[1]
    return (Humanize-Id $base) + " Spawn Egg"
  }

  # Default: replace underscores and title-case
  $words = $Id -split "_" | Where-Object { $_ -ne "" } | ForEach-Object {
    if ($_.Length -eq 1) { $_.ToUpperInvariant() }
    else { $_.Substring(0,1).ToUpperInvariant() + $_.Substring(1) }
  }

  return ($words -join " ")
}

$repoRoot = Split-Path -Parent $PSScriptRoot
$langPath = Join-Path $repoRoot ("src\\main\\resources\\assets\\{0}\\lang\\{1}.json" -f $ModId, $Lang)
$blockstatesDir = Join-Path $repoRoot ("src\\main\\resources\\assets\\{0}\\blockstates" -f $ModId)
$itemModelsDir = Join-Path $repoRoot ("src\\main\\resources\\assets\\{0}\\models\\item" -f $ModId)

if (!(Test-Path $blockstatesDir)) {
  throw "Blockstates folder not found: $blockstatesDir"
}

if (!(Test-Path $itemModelsDir)) {
  Write-Warning "Item models folder not found: $itemModelsDir (continuing with blocks only)"
}

$existing = @{}
if (Test-Path $langPath) {
  $raw = Get-Content -LiteralPath $langPath -Raw
  if ($raw.Trim().Length -gt 0) {
    $existingObj = $raw | ConvertFrom-Json
    $existing = ConvertTo-Hashtable $existingObj
  }
}

$added = 0

# Block translations (covers block items too)
$blockIds = Get-ChildItem -LiteralPath $blockstatesDir -File -Filter "*.json" | Select-Object -ExpandProperty BaseName
foreach ($id in $blockIds) {
  $key = "block.$ModId.$id"
  if (-not $existing.ContainsKey($key)) {
    $existing[$key] = Humanize-Id $id
    $added++
  }
}

# Item translations (non-block items + spawn eggs, etc)
if (Test-Path $itemModelsDir) {
  $itemIds = Get-ChildItem -LiteralPath $itemModelsDir -File -Filter "*.json" | Select-Object -ExpandProperty BaseName
  foreach ($id in $itemIds) {
    $key = "item.$ModId.$id"
    if (-not $existing.ContainsKey($key)) {
      $existing[$key] = Humanize-Id $id
      $added++
    }
  }
}

# Write sorted, stable output
$ordered = [ordered]@{}
foreach ($k in ($existing.Keys | Sort-Object)) {
  $ordered[$k] = $existing[$k]
}

$langDir = Split-Path -Parent $langPath
New-Item -ItemType Directory -Force -Path $langDir | Out-Null
($ordered | ConvertTo-Json -Depth 4) + "`n" | Set-Content -LiteralPath $langPath -Encoding UTF8

Write-Host "Updated $langPath"
Write-Host "Added $added missing translation entries"