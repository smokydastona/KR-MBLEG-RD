param(
    [string]$MaterialBiblePath,
    [ValidateSet('palettes','textures')]
    [string]$Mode = 'textures',
    [int]$TextureSize = 16,
    [string]$OutputDir,
    [switch]$WriteToAssets,
    [string]$AssetsTexturesDir,
    [string]$BackupDir,
    [switch]$DryRun
)

# Resolve default paths relative to repo root
if (-not $PSScriptRoot) {
    $PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
}

$repoRoot = Split-Path $PSScriptRoot -Parent

if (-not $MaterialBiblePath) {
    $preferred = Join-Path $repoRoot "docs\Wood_Material_Bible copy.md"
    $fallback = Join-Path $repoRoot "docs\Wood_Material_Bible.md"
    $MaterialBiblePath = if (Test-Path $preferred) { $preferred } else { $fallback }
}

if (-not $AssetsTexturesDir) {
    $AssetsTexturesDir = Join-Path $repoRoot "src\main\resources\assets\kruemblegard\textures"
}

if (-not $OutputDir) {
    if ($WriteToAssets) {
        $OutputDir = $AssetsTexturesDir
    } else {
        $OutputDir = Join-Path $repoRoot "build/generated/wood_textures"
    }
}

if (-not (Test-Path $MaterialBiblePath)) {
    throw "Material bible markdown not found at path: $MaterialBiblePath"
}

if (-not (Test-Path $AssetsTexturesDir)) {
    throw "Assets textures directory not found at path: $AssetsTexturesDir"
}

Write-Host "Mode:" $Mode
Write-Host "Material bible:" $MaterialBiblePath
Write-Host "Assets textures:" $AssetsTexturesDir
Write-Host "Output directory:" $OutputDir
if ($WriteToAssets) {
    Write-Host "WriteToAssets: enabled (will overwrite existing PNGs)"
}
if ($DryRun) {
    Write-Host "DryRun: enabled (no files will be written)"
}

if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

# Load System.Drawing for PNG generation (Windows)
Add-Type -AssemblyName System.Drawing

function Parse-RgbString {
    param([string]$rgb)
    if (-not $rgb -or $rgb -eq 'none') { return $null }
    $parts = $rgb.Split(',') | ForEach-Object { $_.Trim() }
    if ($parts.Count -ne 3) { return $null }
    return [pscustomobject]@{
        R = [int]$parts[0]
        G = [int]$parts[1]
        B = [int]$parts[2]
    }
}

function Clamp-Byte {
    param([int]$v)
    if ($v -lt 0) { return 0 }
    if ($v -gt 255) { return 255 }
    return $v
}

function Color-FromRgb {
    param($rgb, [int]$a = 255)
    if (-not $rgb) { return [System.Drawing.Color]::FromArgb($a, 128, 128, 128) }
    return [System.Drawing.Color]::FromArgb($a, (Clamp-Byte $rgb.R), (Clamp-Byte $rgb.G), (Clamp-Byte $rgb.B))
}

function Color-Shift {
    param([System.Drawing.Color]$c, [int]$delta)
    return [System.Drawing.Color]::FromArgb(
        $c.A,
        (Clamp-Byte ($c.R + $delta)),
        (Clamp-Byte ($c.G + $delta)),
        (Clamp-Byte ($c.B + $delta))
    )
}

function Hash-Seed {
    param([string]$text)
    $md5 = [System.Security.Cryptography.MD5]::Create()
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($text)
    $hash = $md5.ComputeHash($bytes)
    $md5.Dispose()
    # Use first 4 bytes as UInt32
    return [BitConverter]::ToUInt32($hash, 0)
}

function New-Rng {
    param([uint32]$seed)
    # Closure-captured state; ensure we emit a single value per call.
    [uint32]$state = $seed
    return {
        # xorshift32
        $state = [uint32]($state -bxor ((($state -shl 13) -band 0xFFFFFFFF)))
        $state = [uint32]($state -bxor ((($state -shr 17) -band 0xFFFFFFFF)))
        $state = [uint32]($state -bxor ((($state -shl 5) -band 0xFFFFFFFF)))
        return $state
    }.GetNewClosure()
}

function Get-FamilyStyle {
    param([string]$familyPrefix)

    switch ($familyPrefix) {
        'ashbloom' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'ash_speckle'; BarkRidge = 3; BarkCrackChance = 28; LeavesDensity = 62 } }
        'cairn_tree' { return [pscustomobject]@{ PlankBoard = 3; PlankMotif = 'stone_mosaic'; BarkRidge = 4; BarkCrackChance = 999; LeavesDensity = 55 } }
        'driftwillow' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'wavy_grain'; BarkRidge = 3; BarkCrackChance = 35; LeavesDensity = 68 } }
        'driftwood' { return [pscustomobject]@{ PlankBoard = 5; PlankMotif = 'bleached_streaks'; BarkRidge = 4; BarkCrackChance = 40; LeavesDensity = 58 } }
        'echowood' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'echo_ripples'; BarkRidge = 3; BarkCrackChance = 999; LeavesDensity = 60 } }
        'fallbark' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'autumn_gradient'; BarkRidge = 3; BarkCrackChance = 30; LeavesDensity = 72 } }
        'faultwood' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'fault_cracks'; BarkRidge = 3; BarkCrackChance = 12; LeavesDensity = 64 } }
        'glimmerpine' { return [pscustomobject]@{ PlankBoard = 3; PlankMotif = 'fine_striations'; BarkRidge = 2; BarkCrackChance = 45; LeavesDensity = 56 } }
        'hollowway_tree' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'hollow_knots'; BarkRidge = 4; BarkCrackChance = 32; LeavesDensity = 52 } }
        'monument_oak' { return [pscustomobject]@{ PlankBoard = 6; PlankMotif = 'big_knots'; BarkRidge = 4; BarkCrackChance = 30; LeavesDensity = 70 } }
        'shardbark_pine' { return [pscustomobject]@{ PlankBoard = 3; PlankMotif = 'shards'; BarkRidge = 2; BarkCrackChance = 999; LeavesDensity = 60 } }
        'wayglass' { return [pscustomobject]@{ PlankBoard = 2; PlankMotif = 'glass_highlight'; BarkRidge = 5; BarkCrackChance = 999; LeavesDensity = 46 } }
        'wayroot' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'root_tendrils'; BarkRidge = 3; BarkCrackChance = 34; LeavesDensity = 66 } }
        'waytorch_tree' { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'char_embers'; BarkRidge = 3; BarkCrackChance = 22; LeavesDensity = 50 } }
        Default { return [pscustomobject]@{ PlankBoard = 4; PlankMotif = 'none'; BarkRidge = 3; BarkCrackChance = 30; LeavesDensity = 60 } }
    }
}

function Draw-LineClamped {
    param(
        [System.Drawing.Bitmap]$bmp,
        [int]$x0,
        [int]$y0,
        [int]$x1,
        [int]$y1,
        [System.Drawing.Color]$color
    )
    $dx = [Math]::Abs($x1 - $x0)
    $sx = if ($x0 -lt $x1) { 1 } else { -1 }
    $dy = -[Math]::Abs($y1 - $y0)
    $sy = if ($y0 -lt $y1) { 1 } else { -1 }
    $err = $dx + $dy
    while ($true) {
        Set-PixelSafe $bmp $x0 $y0 $color
        if ($x0 -eq $x1 -and $y0 -eq $y1) { break }
        $e2 = 2 * $err
        if ($e2 -ge $dy) { $err += $dy; $x0 += $sx }
        if ($e2 -le $dx) { $err += $dx; $y0 += $sy }
    }
}

function Rng-Range {
    param($rng, [int]$min, [int]$max)
    $n = [int](([int64](& $rng) -band 0x7FFFFFFF))
    if ($max -le $min) { return $min }
    return $min + ($n % ($max - $min + 1))
}

function Set-PixelSafe {
    param([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [System.Drawing.Color]$color)
    if ($x -lt 0 -or $y -lt 0 -or $x -ge $bmp.Width -or $y -ge $bmp.Height) { return }
    $bmp.SetPixel($x, $y, $color)
}

function Fill {
    param([System.Drawing.Bitmap]$bmp, [System.Drawing.Color]$color)
    for ($y = 0; $y -lt $bmp.Height; $y++) {
        for ($x = 0; $x -lt $bmp.Width; $x++) {
            $bmp.SetPixel($x, $y, $color)
        }
    }
}

function Draw-Planks {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey,
        [switch]$Vertical
    )
    $rng = New-Rng -seed (Hash-Seed $seedKey)
    $style = Get-FamilyStyle $familyPrefix
    $w = $bmp.Width
    $h = $bmp.Height
    $board = [int]$style.PlankBoard

    for ($y = 0; $y -lt $h; $y++) {
        for ($x = 0; $x -lt $w; $x++) {
            $pos = if ($Vertical) { $x } else { $y }
            $boardIndex = [int]([Math]::Floor($pos / $board))
            $shade = (Rng-Range $rng -2 2) + (($boardIndex % 2) * 1)
            $c = Color-Shift $base $shade

            # seam lines
            if (($pos % $board) -eq 0) {
                $c = Color-Shift $base -10
            }

            # subtle grain
            if ((Rng-Range $rng 0 24) -eq 0) {
                $c = Color-Shift $accent (Rng-Range $rng -6 6)
            }

            $bmp.SetPixel($x, $y, $c)
        }
    }

    # strong family motifs (on top of the baseline planks)
    switch ($style.PlankMotif) {
        'autumn_gradient' {
            for ($y = 0; $y -lt $h; $y++) {
                $delta = [int]([Math]::Round((($y / [double]($h - 1)) * 10) - 5))
                for ($x = 0; $x -lt $w; $x++) {
                    if ((($x + $y) % 5) -eq 0) {
                        $bmp.SetPixel($x, $y, (Color-Shift $accent $delta))
                    }
                }
            }
        }
        'big_knots' {
            for ($i = 0; $i -lt 2; $i++) {
                $kx = Rng-Range $rng 3 ($w - 4)
                $ky = Rng-Range $rng 3 ($h - 4)
                for ($y = ($ky - 2); $y -le ($ky + 2); $y++) {
                    for ($x = ($kx - 2); $x -le ($kx + 2); $x++) {
                        $dx = $x - $kx
                        $dy = $y - $ky
                        if ((($dx * $dx) + ($dy * $dy)) -le 5) {
                            Set-PixelSafe $bmp $x $y (Color-Shift $base -14)
                        }
                    }
                }
                Set-PixelSafe $bmp $kx $ky (Color-Shift $accent -10)
            }
        }
        'hollow_knots' {
            $kx = Rng-Range $rng 4 ($w - 5)
            $ky = Rng-Range $rng 4 ($h - 5)
            for ($y = ($ky - 2); $y -le ($ky + 2); $y++) {
                for ($x = ($kx - 2); $x -le ($kx + 2); $x++) {
                    $dx = $x - $kx
                    $dy = $y - $ky
                    $d2 = ($dx * $dx) + ($dy * $dy)
                    if ($d2 -ge 4 -and $d2 -le 7) { Set-PixelSafe $bmp $x $y (Color-Shift $accent -14) }
                    if ($d2 -le 2) { Set-PixelSafe $bmp $x $y (Color-Shift $base -24) }
                }
            }
        }
        'echo_ripples' {
            $cy = [int]($h / 2)
            for ($x = 0; $x -lt $w; $x++) {
                $yy = $cy + [int]([Math]::Round([Math]::Sin(($x + (Rng-Range $rng 0 50)) * 0.7) * 2))
                Set-PixelSafe $bmp $x $yy (Color-Shift $accent 8)
                Set-PixelSafe $bmp $x ($yy + 2) (Color-Shift $accent -4)
            }
        }
        'wavy_grain' {
            for ($y = 0; $y -lt $h; $y++) {
                $xx = [int]([Math]::Round(([Math]::Sin(($y + (Rng-Range $rng 0 50)) * 0.6) * 2) + ($w / 2)))
                Set-PixelSafe $bmp $xx $y (Color-Shift $accent 6)
            }
        }
        'fine_striations' {
            for ($x = 0; $x -lt $w; $x++) {
                if (($x % 2) -eq 0) {
                    for ($y = 0; $y -lt $h; $y++) {
                        if ((Rng-Range $rng 0 6) -eq 0) { Set-PixelSafe $bmp $x $y (Color-Shift $accent 4) }
                    }
                }
            }
        }
        'shards' {
            for ($i = 0; $i -lt 4; $i++) {
                $x0 = Rng-Range $rng 0 ($w - 1)
                $y0 = Rng-Range $rng 0 ($h - 1)
                $x1 = [Math]::Min($w - 1, [Math]::Max(0, $x0 + (Rng-Range $rng -6 6)))
                $y1 = [Math]::Min($h - 1, [Math]::Max(0, $y0 + (Rng-Range $rng -6 6)))
                Draw-LineClamped $bmp $x0 $y0 $x1 $y1 (Color-Shift $accent (Rng-Range $rng -6 10))
            }
        }
        'stone_mosaic' {
            for ($y = 0; $y -lt $h; $y += 2) {
                for ($x = 0; $x -lt $w; $x += 2) {
                    if ((Rng-Range $rng 0 3) -eq 0) {
                        $c = Color-Shift $accent (Rng-Range $rng -18 2)
                        Set-PixelSafe $bmp $x $y $c
                        Set-PixelSafe $bmp ($x + 1) $y $c
                        Set-PixelSafe $bmp $x ($y + 1) $c
                        Set-PixelSafe $bmp ($x + 1) ($y + 1) $c
                    }
                }
            }
            # rune dots on seams
            for ($i = 0; $i -lt 6; $i++) {
                Set-PixelSafe $bmp (Rng-Range $rng 1 ($w - 2)) (Rng-Range $rng 1 ($h - 2)) (Color-Shift $accent 14)
            }
        }
        'bleached_streaks' {
            for ($y = 1; $y -lt $h; $y += 3) {
                for ($x = 0; $x -lt $w; $x++) {
                    if ((Rng-Range $rng 0 2) -eq 0) { Set-PixelSafe $bmp $x $y (Color-Shift $base 10) }
                }
            }
        }
        'ash_speckle' {
            for ($i = 0; $i -lt 18; $i++) {
                $x = Rng-Range $rng 0 ($w - 1)
                $y = Rng-Range $rng 0 ($h - 1)
                Set-PixelSafe $bmp $x $y (Color-Shift $base (Rng-Range $rng -18 18))
            }
        }
        'fault_cracks' {
            for ($i = 0; $i -lt 3; $i++) {
                $x0 = Rng-Range $rng 0 ($w - 1)
                $y0 = Rng-Range $rng 0 ($h - 1)
                $x1 = [Math]::Min($w - 1, [Math]::Max(0, $x0 + (Rng-Range $rng -7 7)))
                $y1 = [Math]::Min($h - 1, [Math]::Max(0, $y0 + (Rng-Range $rng -7 7)))
                Draw-LineClamped $bmp $x0 $y0 $x1 $y1 (Color-Shift $accent -22)
            }
        }
        'glass_highlight' {
            for ($y = 0; $y -lt $h; $y++) {
                Set-PixelSafe $bmp ($w - 3) $y (Color-Shift $accent 12)
                if (($y % 2) -eq 0) { Set-PixelSafe $bmp ($w - 2) $y (Color-Shift $base 10) }
            }
            for ($x = 0; $x -lt $w; $x++) {
                Set-PixelSafe $bmp $x 0 (Color-Shift $base 8)
                Set-PixelSafe $bmp $x ($h - 1) (Color-Shift $base -6)
            }
        }
        'root_tendrils' {
            $cy = [int]($h / 2)
            $phase = Rng-Range $rng 0 100
            for ($x = 0; $x -lt $w; $x++) {
                $yy = $cy + [int]([Math]::Round([Math]::Sin(($x + $phase) * 0.65) * 2))
                Set-PixelSafe $bmp $x $yy (Color-Shift $accent -6)
                if ((Rng-Range $rng 0 3) -eq 0) { Set-PixelSafe $bmp $x ($yy + 1) (Color-Shift $base -10) }
            }
        }
        'char_embers' {
            # char-darken everything slightly
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    if ((Rng-Range $rng 0 4) -eq 0) {
                        $c = $bmp.GetPixel($x, $y)
                        $bmp.SetPixel($x, $y, (Color-Shift $c -12))
                    }
                }
            }
            # embers
            for ($i = 0; $i -lt 10; $i++) {
                $x = Rng-Range $rng 0 ($w - 1)
                $y = Rng-Range $rng 0 ($h - 1)
                Set-PixelSafe $bmp $x $y (Color-Shift $accent 10)
                if (-not $emissive.IsEmpty -and (Rng-Range $rng 0 1) -eq 0) { Set-PixelSafe $bmp $x $y $emissive }
            }
        }
    }

    # optional emissive inlays
    if (-not $emissive.IsEmpty) {
        for ($i = 0; $i -lt 10; $i++) {
            $px = Rng-Range $rng 1 ($w - 2)
            $py = Rng-Range $rng 1 ($h - 2)
            if ((Rng-Range $rng 0 1) -eq 0) {
                Set-PixelSafe $bmp $px $py $emissive
            }
        }
    }
}

function Draw-LogSide {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey,
        [switch]$Smooth
    )
    $rng = New-Rng -seed (Hash-Seed $seedKey)
    $style = Get-FamilyStyle $familyPrefix
    $w = $bmp.Width
    $h = $bmp.Height
    $ridge = if ($Smooth) { 5 } else { [int]$style.BarkRidge }
    $crackChance = [int]$style.BarkCrackChance

    for ($y = 0; $y -lt $h; $y++) {
        for ($x = 0; $x -lt $w; $x++) {
            $c = $base
            $noise = (Rng-Range $rng -2 2)
            $c = Color-Shift $c $noise

            # vertical ridges
            if (($x % $ridge) -eq 0) {
                $c = Color-Shift $c ($(if ($Smooth) { -4 } else { -10 }))
            }

            # cracks
            if (-not $Smooth -and $crackChance -lt 900 -and (Rng-Range $rng 0 $crackChance) -eq 0) {
                $c = Color-Shift $accent -18
            }

            $bmp.SetPixel($x, $y, $c)
        }
    }

    # family overlays
    switch ((Get-FamilyStyle $familyPrefix).PlankMotif) {
        'shards' {
            for ($i = 0; $i -lt 3; $i++) {
                $x0 = Rng-Range $rng 0 ($w - 1)
                $y0 = Rng-Range $rng 0 ($h - 1)
                $x1 = [Math]::Min($w - 1, [Math]::Max(0, $x0 + (Rng-Range $rng -6 6)))
                $y1 = [Math]::Min($h - 1, [Math]::Max(0, $y0 + (Rng-Range $rng -6 6)))
                Draw-LineClamped $bmp $x0 $y0 $x1 $y1 (Color-Shift $accent (Rng-Range $rng -10 6))
            }
        }
        'stone_mosaic' {
            for ($i = 0; $i -lt 10; $i++) {
                $x = Rng-Range $rng 0 ($w - 1)
                $y = Rng-Range $rng 0 ($h - 1)
                Set-PixelSafe $bmp $x $y (Color-Shift $accent (Rng-Range $rng -20 0))
            }
        }
        'glass_highlight' {
            for ($y = 0; $y -lt $h; $y++) {
                Set-PixelSafe $bmp ($w - 2) $y (Color-Shift $accent 10)
            }
        }
        'char_embers' {
            for ($y = 0; $y -lt $h; $y++) {
                if ((Rng-Range $rng 0 7) -eq 0) {
                    Set-PixelSafe $bmp (Rng-Range $rng 1 ($w - 2)) $y (Color-Shift $accent 8)
                }
            }
        }
        'fault_cracks' {
            for ($i = 0; $i -lt 2; $i++) {
                $x0 = Rng-Range $rng 0 ($w - 1)
                $y0 = 0
                $x1 = [Math]::Min($w - 1, [Math]::Max(0, $x0 + (Rng-Range $rng -2 2)))
                $y1 = $h - 1
                Draw-LineClamped $bmp $x0 $y0 $x1 $y1 (Color-Shift $accent -24)
            }
        }
    }

    # edge-contained emissive veins
    if (-not $emissive.IsEmpty) {
        for ($y = 1; $y -lt ($h - 1); $y += 2) {
            if ((Rng-Range $rng 0 4) -eq 0) {
                Set-PixelSafe $bmp 0 $y $emissive
                Set-PixelSafe $bmp ($w - 1) $y $emissive
            }
        }
    }
}

function Draw-LogTop {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey
    )
    $rng = New-Rng -seed (Hash-Seed $seedKey)
    $w = $bmp.Width
    $h = $bmp.Height
    Fill $bmp $base

    $cx = [int]($w / 2)
    $cy = [int]($h / 2)

    switch ($familyPrefix) {
        'echowood' {
            # Concentric ripple rings (non-organic, "sound fossilized")
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    $dx = $x - $cx
                    $dy = $y - $cy
                    $d = [Math]::Sqrt(($dx * $dx) + ($dy * $dy))
                    $band = [int]([Math]::Floor($d))
                    $c = if (($band % 2) -eq 0) { Color-Shift $base 2 } else { Color-Shift $accent -2 }
                    if (($band % 6) -eq 0) { $c = Color-Shift $c 6 }
                    $bmp.SetPixel($x, $y, $c)
                }
            }
        }
        'cairn_tree' {
            # Stacked memorial stones: mosaic + inscriptions
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    $c = Color-Shift $base (Rng-Range $rng -8 4)
                    if ((Rng-Range $rng 0 9) -eq 0) { $c = Color-Shift $accent (Rng-Range $rng -6 6) }
                    $bmp.SetPixel($x, $y, $c)
                }
            }
            for ($i = 0; $i -lt 16; $i++) {
                $x = Rng-Range $rng 1 ($w - 2)
                $y = Rng-Range $rng 1 ($h - 2)
                Set-PixelSafe $bmp $x $y (Color-Shift $accent -18)
            }
        }
        'shardbark_pine' {
            # Angular crystalline facets
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    $c = $base
                    if ((($x + $y) % 3) -eq 0) { $c = Color-Shift $accent (Rng-Range $rng -4 8) }
                    if ((($x - $y) % 5) -eq 0) { $c = Color-Shift $c -6 }
                    $bmp.SetPixel($x, $y, $c)
                }
            }
        }
        'wayglass' {
            # Glassy slab: gradient + highlight, emissive edge
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    $delta = [int]([Math]::Round((($y / [double]($h - 1)) * 10) - 5))
                    $c = Color-Shift $base $delta
                    if ((($x + $y) % 7) -eq 0) { $c = Color-Shift $c 6 }
                    $bmp.SetPixel($x, $y, $c)
                }
            }
            if (-not $emissive.IsEmpty) {
                for ($x = 0; $x -lt $w; $x++) {
                    Set-PixelSafe $bmp $x 0 $emissive
                    Set-PixelSafe $bmp $x ($h - 1) $emissive
                }
            }
        }
        'waytorch_tree' {
            # Char + ember channels
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    $c = Color-Shift $base (Rng-Range $rng -12 4)
                    if ((Rng-Range $rng 0 12) -eq 0) { $c = Color-Shift $accent (Rng-Range $rng -6 6) }
                    $bmp.SetPixel($x, $y, $c)
                }
            }
            if (-not $emissive.IsEmpty) {
                for ($i = 0; $i -lt 20; $i++) {
                    $x = Rng-Range $rng 1 ($w - 2)
                    $y = Rng-Range $rng 1 ($h - 2)
                    if ((Rng-Range $rng 0 2) -eq 0) { Set-PixelSafe $bmp $x $y $emissive }
                }
            }
        }
        Default {
            # Generic non-organic cut face: chips + fractures (no growth rings)
            for ($y = 0; $y -lt $h; $y++) {
                for ($x = 0; $x -lt $w; $x++) {
                    $c = Color-Shift $base (Rng-Range $rng -6 4)
                    if ((Rng-Range $rng 0 11) -eq 0) { $c = Color-Shift $accent (Rng-Range $rng -6 6) }
                    $bmp.SetPixel($x, $y, $c)
                }
            }

            # cross fractures
            for ($i = 0; $i -lt $w; $i++) {
                if ((Rng-Range $rng 0 2) -eq 0) {
                    Set-PixelSafe $bmp $i $cy (Color-Shift $accent -16)
                    Set-PixelSafe $bmp $cx $i (Color-Shift $accent -16)
                }
            }

            if (-not $emissive.IsEmpty) {
                for ($x = 1; $x -lt ($w - 1); $x++) {
                    if ((Rng-Range $rng 0 5) -eq 0) { Set-PixelSafe $bmp $x 1 $emissive }
                }
            }
        }
    }
}

function Draw-FlatDerived {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey
    )
    # Use plank look as a baseline for most wood-derived blocks
    Draw-Planks -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $familyPrefix -seedKey $seedKey

    # add a subtle border so buttons/plates read cleaner
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        Set-PixelSafe $bmp $x 0 (Color-Shift $base -10)
        Set-PixelSafe $bmp $x ($bmp.Height - 1) (Color-Shift $base -10)
    }
    for ($y = 0; $y -lt $bmp.Height; $y++) {
        Set-PixelSafe $bmp 0 $y (Color-Shift $base -10)
        Set-PixelSafe $bmp ($bmp.Width - 1) $y (Color-Shift $base -10)
    }
}

function Draw-DoorPart {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey,
        [ValidateSet('top','bottom')]
        [string]$Part
    )
    Draw-Planks -bmp $bmp -base $base -accent $accent -emissive ([System.Drawing.Color]::Empty) -familyPrefix $familyPrefix -seedKey ($seedKey + ":door") -Vertical

    # frame
    $frame = Color-Shift $base -12
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        Set-PixelSafe $bmp $x 0 $frame
        Set-PixelSafe $bmp $x ($bmp.Height - 1) $frame
    }
    for ($y = 0; $y -lt $bmp.Height; $y++) {
        Set-PixelSafe $bmp 0 $y $frame
        Set-PixelSafe $bmp ($bmp.Width - 1) $y $frame
    }

    # handle/inlay on bottom part
    if ($Part -eq 'bottom') {
        Set-PixelSafe $bmp 12 8 (Color-Shift $accent 10)
        Set-PixelSafe $bmp 12 9 (Color-Shift $accent 6)
        if (-not $emissive.IsEmpty) {
            Set-PixelSafe $bmp 3 8 $emissive
            Set-PixelSafe $bmp 3 9 $emissive
        }
    }

    # small inlay on top
    if ($Part -eq 'top' -and (-not $emissive.IsEmpty)) {
        Set-PixelSafe $bmp 8 4 $emissive
        Set-PixelSafe $bmp 7 4 $emissive
    }
}

function Draw-Trapdoor {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey
    )
    Draw-Planks -bmp $bmp -base $base -accent $accent -emissive ([System.Drawing.Color]::Empty) -familyPrefix $familyPrefix -seedKey ($seedKey + ":trapdoor")
    $bar = Color-Shift $base -14
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        if (($x % 5) -eq 0) {
            for ($y = 1; $y -lt ($bmp.Height - 1); $y++) {
                Set-PixelSafe $bmp $x $y $bar
            }
        }
    }
    if (-not $emissive.IsEmpty) {
        Set-PixelSafe $bmp 1 1 $emissive
        Set-PixelSafe $bmp 14 14 $emissive
    }
}

function Draw-Leaves {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$familyPrefix,
        [string]$seedKey
    )
    $rng = New-Rng -seed (Hash-Seed $seedKey)
    $style = Get-FamilyStyle $familyPrefix
    $w = $bmp.Width
    $h = $bmp.Height

    # transparent background
    for ($y = 0; $y -lt $h; $y++) {
        for ($x = 0; $x -lt $w; $x++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }

    for ($y = 0; $y -lt $h; $y++) {
        for ($x = 0; $x -lt $w; $x++) {
            $roll = Rng-Range $rng 0 99
            if ($roll -lt [int]$style.LeavesDensity) {
                $c = Color-Shift $base (Rng-Range $rng -6 6)
                if ($roll -lt 12) { $c = Color-Shift $accent (Rng-Range $rng -6 8) }
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(220, $c.R, $c.G, $c.B))
            }
        }
    }

    if (-not $emissive.IsEmpty) {
        for ($i = 0; $i -lt 12; $i++) {
            $x = Rng-Range $rng 1 ($w - 2)
            $y = Rng-Range $rng 1 ($h - 2)
            Set-PixelSafe $bmp $x $y ([System.Drawing.Color]::FromArgb(240, $emissive.R, $emissive.G, $emissive.B))
        }
    }
}

function Draw-Sapling {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [string]$seedKey
    )
    # transparent background
    for ($y = 0; $y -lt $bmp.Height; $y++) {
        for ($x = 0; $x -lt $bmp.Width; $x++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }

    # stem
    $stem = Color-Shift $base -12
    for ($y = 9; $y -lt 15; $y++) {
        Set-PixelSafe $bmp 7 $y ([System.Drawing.Color]::FromArgb(255, $stem.R, $stem.G, $stem.B))
        Set-PixelSafe $bmp 8 $y ([System.Drawing.Color]::FromArgb(255, $stem.R, $stem.G, $stem.B))
    }

    # crown
    $leaf = Color-Shift $accent 4
    for ($y = 2; $y -lt 9; $y++) {
        for ($x = 3; $x -lt 13; $x++) {
            if ((($x + $y) % 3) -ne 0) {
                $c = Color-Shift $leaf ((($x * 17 + $y * 11) % 5) - 2)
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(220, $c.R, $c.G, $c.B))
            }
        }
    }

    if (-not $emissive.IsEmpty) {
        Set-PixelSafe $bmp 8 6 ([System.Drawing.Color]::FromArgb(240, $emissive.R, $emissive.G, $emissive.B))
    }
}

function Ensure-Dir {
    param([string]$path)
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

function Copy-Backup {
    param([string]$src, [string]$dst)
    Ensure-Dir (Split-Path -Parent $dst)
    Copy-Item -Path $src -Destination $dst -Force
}

# Read markdown and locate Master Palette table
$lines = Get-Content -Path $MaterialBiblePath
$inTable = $false
$paletteEntries = @()

foreach ($line in $lines) {
    if ($line -match '^##\s+Master Palette \(RGB\)') {
        $inTable = $true
        continue
    }

    if ($inTable) {
        # End of table when we hit a horizontal rule or another heading
        if ($line -match '^---$' -or $line -match '^##\s+') {
            break
        }

        # Skip header separator or empty lines
        if ([string]::IsNullOrWhiteSpace($line)) { continue }
        if ($line.Trim() -like '|---*') { continue }
        if ($line.Trim().StartsWith('| Family')) { continue }

        # Expect table row like: | Ashbloom | 190,190,185 | 160,160,155 | none |
        if ($line -notmatch '^\|') { continue }

        $cells = $line.Trim('|').Split('|') | ForEach-Object { $_.Trim() }
        if ($cells.Count -lt 4) { continue }

        $family  = $cells[0]
        $baseStr = $cells[1]
        $accentStr = $cells[2]
        $emissiveStr = $cells[3]

        $base = Parse-RgbString -rgb $baseStr
        $accent = Parse-RgbString -rgb $accentStr
        $emissive = Parse-RgbString -rgb $emissiveStr

        $paletteEntries += [pscustomobject]@{
            Family   = $family
            Base     = $base
            Accent   = $accent
            Emissive = $emissive
        }
    }
}

if ($paletteEntries.Count -eq 0) {
    throw "No palette entries parsed from Master Palette table. Check the markdown format."
}

Write-Host "Found" $paletteEntries.Count "palette entries."`n

# Map palette families to in-repo texture prefixes
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

if ($Mode -eq 'palettes') {
    $palDir = Join-Path $OutputDir "palettes"
    Ensure-Dir $palDir
    foreach ($entry in $paletteEntries) {
        $nameSlug = $entry.Family.ToLowerInvariant() -replace '[^a-z0-9]+', '_'
        $fileName = "${nameSlug}_palette.png"
        $path = Join-Path $palDir $fileName

        $size = [Math]::Max(8, $TextureSize)
        $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        try {
            $baseColor = Color-FromRgb $entry.Base
            $accentColor = Color-FromRgb $entry.Accent
            $emissiveColor = if ($entry.Emissive) { Color-FromRgb $entry.Emissive } else { [System.Drawing.Color]::Empty }
            Fill $bmp $baseColor
            Draw-Planks -bmp $bmp -base $baseColor -accent $accentColor -emissive $emissiveColor -familyPrefix $nameSlug -seedKey ("palette:" + $nameSlug)
            if (-not $DryRun) {
                $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
            }
            Write-Host "Generated" $fileName
        }
        finally {
            $bmp.Dispose()
        }
    }
    Write-Host "Done. Palette textures written to" $palDir
    exit 0
}

# Build target texture list from existing asset PNGs for each family
$assetBlockDir = Join-Path $AssetsTexturesDir "block"
$assetItemDir = Join-Path $AssetsTexturesDir "item"
if (-not (Test-Path $assetBlockDir)) { throw "Missing assets block textures dir: $assetBlockDir" }
if (-not (Test-Path $assetItemDir)) { throw "Missing assets item textures dir: $assetItemDir" }

$targets = @()
foreach ($entry in $paletteEntries) {
    $prefix = Family-ToPrefix $entry.Family

    $blockMatches = Get-ChildItem -Path $assetBlockDir -Filter "${prefix}*.png" -File -ErrorAction SilentlyContinue
    $blockMatches += Get-ChildItem -Path $assetBlockDir -Filter "stripped_${prefix}*.png" -File -ErrorAction SilentlyContinue
    $itemMatches = Get-ChildItem -Path $assetItemDir -Filter "${prefix}*.png" -File -ErrorAction SilentlyContinue

    foreach ($m in $blockMatches) {
        $targets += [pscustomobject]@{ Family=$entry.Family; Prefix=$prefix; Palette=$entry; RelPath=("block/" + $m.Name) }
    }
    foreach ($m in $itemMatches) {
        # only wood-family item textures we expect (door icons)
        if ($m.Name -match "^${prefix}_door\.png$") {
            $targets += [pscustomobject]@{ Family=$entry.Family; Prefix=$prefix; Palette=$entry; RelPath=("item/" + $m.Name) }
        }
    }
}

$targets = $targets | Sort-Object RelPath -Unique
if ($targets.Count -eq 0) {
    throw "No matching wood-family texture targets found in assets."
}

Write-Host "Found" $targets.Count "target textures to generate."`n

if ($WriteToAssets -and -not $BackupDir) {
    $stamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $BackupDir = Join-Path $repoRoot "build/texture_backups/$stamp"
}

if ($WriteToAssets) {
    Write-Host "Backup directory:" $BackupDir
    Ensure-Dir $BackupDir
}

foreach ($t in $targets) {
    $rel = $t.RelPath
    $srcPath = Join-Path $AssetsTexturesDir $rel
    $dstPath = Join-Path $OutputDir $rel
    Ensure-Dir (Split-Path -Parent $dstPath)

    $size = [Math]::Max(16, $TextureSize)
    $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        $base = Color-FromRgb $t.Palette.Base
        $accent = Color-FromRgb $t.Palette.Accent
        $emissive = if ($t.Palette.Emissive) { Color-FromRgb $t.Palette.Emissive } else { [System.Drawing.Color]::Empty }

        $name = [System.IO.Path]::GetFileNameWithoutExtension($rel)
        $seedKey = ($t.Prefix + ":" + $name)

        if ($name -match '^stripped_') {
            Draw-LogSide -bmp $bmp -base (Color-Shift $base 4) -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey -Smooth
        }
        elseif ($name -match '_log_top$') {
            Draw-LogTop -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey
        }
        elseif ($name -match '_log$') {
            Draw-LogSide -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey
        }
        elseif ($name -match '_planks$') {
            Draw-Planks -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey
        }
        elseif ($name -match '_door_bottom$') {
            Draw-DoorPart -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey -Part bottom
        }
        elseif ($name -match '_door_top$') {
            Draw-DoorPart -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey -Part top
        }
        elseif ($name -match '_trapdoor$') {
            Draw-Trapdoor -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey
        }
        elseif ($name -match '_leaves$') {
            Draw-Leaves -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey
        }
        elseif ($name -match '_sapling$') {
            Draw-Sapling -bmp $bmp -base $base -accent $accent -emissive $emissive -seedKey $seedKey
        }
        elseif ($rel -match '^item/' -and $name -match '_door$') {
            # door inventory icon: derived from door bottom look
            Draw-DoorPart -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey -Part bottom
        }
        else {
            Draw-FlatDerived -bmp $bmp -base $base -accent $accent -emissive $emissive -familyPrefix $t.Prefix -seedKey $seedKey
        }

        if ($WriteToAssets) {
            if (Test-Path $srcPath) {
                $backupPath = Join-Path $BackupDir $rel
                Copy-Backup -src $srcPath -dst $backupPath
            }
        }

        if (-not $DryRun) {
            $bmp.Save($dstPath, [System.Drawing.Imaging.ImageFormat]::Png)
        }
        Write-Host "Generated" $rel
    }
    finally {
        $bmp.Dispose()
    }
}

Write-Host "Done. Generated" $targets.Count "textures into" $OutputDir