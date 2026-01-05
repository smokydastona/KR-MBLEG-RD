# Generates placeholder PNGs for any missing kruemblegard:item/* textures referenced by item model JSONs.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_missing_item_textures_from_models.ps1

param(
    [switch]$WhatIf
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.Drawing

$root = Resolve-Path '.'
$modelsDir = Join-Path $root 'src/main/resources/assets/kruemblegard/models/item'
$texDir    = Join-Path $root 'src/main/resources/assets/kruemblegard/textures/item'

if (-not (Test-Path -LiteralPath $modelsDir)) {
    throw "Missing models dir: $modelsDir"
}

if (-not (Test-Path -LiteralPath $texDir)) {
    if (-not $WhatIf) {
        New-Item -ItemType Directory -Path $texDir | Out-Null
    }
}

function Get-Seed([string]$s) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($s)
    $sha = [System.Security.Cryptography.SHA256]::Create()
    try {
        $hash = $sha.ComputeHash($bytes)
        return [BitConverter]::ToInt32($hash, 0)
    }
    finally {
        $sha.Dispose()
    }
}

function New-Texture16([string]$filePath, [scriptblock]$draw) {
    if (Test-Path -LiteralPath $filePath) {
        return
    }

    if ($WhatIf) {
        Write-Host "[WhatIf] Would create $filePath"
        return
    }

    $parent = Split-Path -Parent $filePath
    if (-not (Test-Path -LiteralPath $parent)) {
        New-Item -ItemType Directory -Path $parent | Out-Null
    }

    $bmp = New-Object System.Drawing.Bitmap 16, 16, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        & $draw $bmp
        $bmp.Save($filePath, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $bmp.Dispose()
    }
}

function P([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [System.Drawing.Color]$c) {
    if ($x -ge 0 -and $x -lt 16 -and $y -ge 0 -and $y -lt 16) {
        $bmp.SetPixel($x, $y, $c)
    }
}

function Make-Color([int]$seed, [int]$offset) {
    $rng = New-Object System.Random ($seed + $offset)
    $r = 60 + $rng.Next(0, 160)
    $g = 60 + $rng.Next(0, 160)
    $b = 60 + $rng.Next(0, 160)
    return [System.Drawing.Color]::FromArgb(255, $r, $g, $b)
}

$missing = New-Object System.Collections.Generic.HashSet[string]

Get-ChildItem -LiteralPath $modelsDir -Filter '*.json' -Recurse | ForEach-Object {
    $jsonText = Get-Content -LiteralPath $_.FullName -Raw
    if (-not $jsonText) { return }

    try { $model = $jsonText | ConvertFrom-Json }
    catch { return }

    $texturesProp = $model.PSObject.Properties.Match('textures') | Select-Object -First 1
    if ($null -eq $texturesProp) { return }

    $textures = $texturesProp.Value
    if ($null -eq $textures) { return }

    $textures.PSObject.Properties | ForEach-Object {
        $val = $_.Value
        if ($val -isnot [string]) { return }

        if ($val -match '^kruemblegard:item/(.+)$') {
            $pngPath = Join-Path $texDir ($Matches[1] + '.png')
            if (-not (Test-Path -LiteralPath $pngPath)) {
                $null = $missing.Add($pngPath)
            }
        }
    }
}

$paths = New-Object string[] $missing.Count
$missing.CopyTo($paths)
$paths = @($paths | Sort-Object)

$created = 0
foreach ($pngPath in $paths) {
    $name = [IO.Path]::GetFileNameWithoutExtension($pngPath)
    $seed = Get-Seed $name
    $bg = Make-Color $seed 1
    $fg = Make-Color $seed 2

    New-Texture16 $pngPath {
        param($bmp)

        # background
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $c = if ((($x + $y) % 2) -eq 0) { $bg } else { $fg }
                P $bmp $x $y $c
            }
        }

        # simple border for readability
        $border = [System.Drawing.Color]::FromArgb(255, 20, 20, 20)
        for ($x = 0; $x -lt 16; $x++) { P $bmp $x 0 $border; P $bmp $x 15 $border }
        for ($y = 0; $y -lt 16; $y++) { P $bmp 0 $y $border; P $bmp 15 $y $border }
    }

    if (-not $WhatIf) { $created++ }
}

Write-Host ("Missing item textures (referenced by models): {0}" -f $paths.Count)
Write-Host ("Generated placeholder item textures: {0}" -f $created)
$paths | Select-Object -First 100
