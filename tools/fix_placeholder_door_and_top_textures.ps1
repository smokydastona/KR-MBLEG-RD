# Fixes placeholder door textures that were copied from planks (which makes door UVs look like the texture is on the edge).
# Also adds simple "*_log_top" / "*_stem_top" placeholder textures when missing.
#
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/fix_placeholder_door_and_top_textures.ps1
#
# Notes:
# - Door models use vanilla templates which reserve a 2px strip on the left of the texture for the door edge.
#   This script generates a basic placeholder layout from the matching plank texture.

[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSUseApprovedVerbs', '', Justification = 'Local helper functions in this script are not exported cmdlets; keep names concise.')]
param(
    [switch]$WhatIf
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.Drawing

$root = (Resolve-Path '.')
$texBlock = Join-Path $root 'src/main/resources/assets/kruemblegard/textures/block'
$modelBlock = Join-Path $root 'src/main/resources/assets/kruemblegard/models/block'

function Get-Sha256([string]$path) {
    if (-not (Test-Path -LiteralPath $path)) {
        return $null
    }
    return (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash
}

function Darken([System.Drawing.Color]$c, [double]$factor) {
    $r = [int]([Math]::Max(0, [Math]::Min(255, [Math]::Round($c.R * $factor))))
    $g = [int]([Math]::Max(0, [Math]::Min(255, [Math]::Round($c.G * $factor))))
    $b = [int]([Math]::Max(0, [Math]::Min(255, [Math]::Round($c.B * $factor))))
    return [System.Drawing.Color]::FromArgb($c.A, $r, $g, $b)
}

function New-DoorTextureFromPlanks([string]$planksPath, [string]$doorPath) {
    if ($WhatIf) {
        Write-Host "[WhatIf] Write $doorPath from $planksPath"
        return
    }

    $src = New-Object System.Drawing.Bitmap $planksPath
    try {
        $src16 = $src
        if ($src.Width -ne 16 -or $src.Height -ne 16) {
            $src16 = New-Object System.Drawing.Bitmap $src, 16, 16
        }

        $dst = New-Object System.Drawing.Bitmap 16, 16, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        try {
            for ($y = 0; $y -lt 16; $y++) {
                for ($x = 0; $x -lt 16; $x++) {
                    if ($x -lt 2) {
                        $c = $src16.GetPixel($x, $y)
                        $dst.SetPixel($x, $y, (Darken $c 0.70))
                    }
                    else {
                        # Shift the face region left so the 14px face (u=2..16) uses full plank content.
                        $dst.SetPixel($x, $y, $src16.GetPixel($x - 2, $y))
                    }
                }
            }

            $parent = Split-Path -Parent $doorPath
            if (-not (Test-Path -LiteralPath $parent)) {
                New-Item -ItemType Directory -Path $parent | Out-Null
            }

            $tmp = $doorPath + '.tmp'
            if (Test-Path -LiteralPath $tmp) {
                Remove-Item -LiteralPath $tmp -Force
            }

            $dst.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png)
        }
        finally {
            $dst.Dispose()
            if ($src16 -ne $src) {
                $src16.Dispose()
            }
        }

        # Move into place after all bitmaps are disposed.
        if (-not $WhatIf) {
            # NOTE: Do NOT overwrite existing textures here. OneDrive/AV can lock the destination.
            # Callers should provide a new destination path when generating "fixed" textures.
            if (-not (Test-Path -LiteralPath $doorPath)) {
                Move-Item -LiteralPath $tmp -Destination $doorPath
            }
            else {
                Remove-Item -LiteralPath $tmp -Force
            }
        }
    }
    finally {
        $src.Dispose()
    }
}

function Copy-IfMissing([string]$src, [string]$dst) {
    if (Test-Path -LiteralPath $dst) {
        return $false
    }
    if (-not (Test-Path -LiteralPath $src)) {
        return $false
    }

    if ($WhatIf) {
        Write-Host "[WhatIf] Copy $src -> $dst"
        return $true
    }

    Copy-Item -LiteralPath $src -Destination $dst
    return $true
}

function Write-TextUtf8NoBom([string]$path, [string]$text) {
    if ($WhatIf) {
        Write-Host "[WhatIf] Write $path"
        return
    }
    $enc = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($path, $text, $enc)
}

$created = 0
$updated = 0
$skipped = 0

# --- Doors: generate *_door_{top,bottom}_fixed.png and rewire models when textures are plank-copy placeholders ---
$doorBottomLeftModels = Get-ChildItem -LiteralPath $modelBlock -Filter '*_door_bottom_left.json' -File
foreach ($m in $doorBottomLeftModels) {
    $base = $m.BaseName -replace '_door_bottom_left$', ''
    $planks = Join-Path $texBlock ($base + '_planks.png')
    if (-not (Test-Path -LiteralPath $planks)) {
        $skipped++
        continue
    }

    $plankHash = Get-Sha256 $planks
    $doorBottom = Join-Path $texBlock ($base + '_door_bottom.png')
    $doorTop = Join-Path $texBlock ($base + '_door_top.png')

    $needsFix = $false
    if ($plankHash) {
        $bottomHash = Get-Sha256 $doorBottom
        $topHash = Get-Sha256 $doorTop
        if (($bottomHash -and $bottomHash -eq $plankHash) -or ($topHash -and $topHash -eq $plankHash)) {
            $needsFix = $true
        }
    }

    if (-not $needsFix) {
        $skipped++
        continue
    }

    $fixedBottom = Join-Path $texBlock ($base + '_door_bottom_fixed.png')
    $fixedTop = Join-Path $texBlock ($base + '_door_top_fixed.png')
    if (-not (Test-Path -LiteralPath $fixedBottom)) {
        New-DoorTextureFromPlanks $planks $fixedBottom
        $created++
    }
    if (-not (Test-Path -LiteralPath $fixedTop)) {
        New-DoorTextureFromPlanks $planks $fixedTop
        $created++
    }

    $doorModelFiles = Get-ChildItem -LiteralPath $modelBlock -Filter ($base + '_door_*.json') -File
    foreach ($f in $doorModelFiles) {
        $raw = Get-Content -LiteralPath $f.FullName -Raw
        $next = $raw.Replace("kruemblegard:block/$base`_door_bottom", "kruemblegard:block/$base`_door_bottom_fixed")
        $next = $next.Replace("kruemblegard:block/$base`_door_top", "kruemblegard:block/$base`_door_top_fixed")
        if ($next -ne $raw) {
            Write-TextUtf8NoBom $f.FullName $next
            $updated++
        }
    }
}

# --- Logs / stems: add missing _top textures as simple copies ---
$textureFiles = Get-ChildItem -LiteralPath $texBlock -Filter '*.png' -File
foreach ($f in $textureFiles) {
    $name = $f.BaseName

    if ($name -match '^(?<base>.+)_log$') {
        $base = $Matches.base
        $src = $f.FullName
        $dst = Join-Path $texBlock ($base + '_log_top.png')
        if (Copy-IfMissing $src $dst) { $created++ }
        continue
    }

    if ($name -match '^(?<base>.+)_stem$') {
        $base = $Matches.base
        $src = $f.FullName
        $dst = Join-Path $texBlock ($base + '_stem_top.png')
        if (Copy-IfMissing $src $dst) { $created++ }
        continue
    }
}

Write-Host "CREATED=$created UPDATED=$updated SKIPPED=$skipped"
