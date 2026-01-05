# Generates 6 placeholder skybox face PNGs for the Wayfall dimension.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_wayfall_skybox_placeholders.ps1

param(
    [int]$Size = 512
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.Drawing

$root = Resolve-Path '.'
$outDir = Join-Path $root 'src/main/resources/assets/kruemblegard/textures/environment'
if (-not (Test-Path -LiteralPath $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
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

function Lerp([int]$a, [int]$b, [double]$t) { [int]([Math]::Round($a + ($b - $a) * $t)) }

function New-Face([string]$name, [string]$fileName) {
    $path = Join-Path $outDir $fileName
    if (Test-Path -LiteralPath $path) {
        return
    }

    $seed = Get-Seed $name
    $rng = New-Object System.Random $seed

    $bmp = New-Object System.Drawing.Bitmap $Size, $Size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        # Two-tone gloom gradient with a faint "waystone" glow band.
        $top = [System.Drawing.Color]::FromArgb(255, 12, 12, 18)
        $bot = [System.Drawing.Color]::FromArgb(255, 28, 18, 38)
        $glow = [System.Drawing.Color]::FromArgb(255, 80, 160, 220)
        $rune = [System.Drawing.Color]::FromArgb(255, 160, 70, 210)

        for ($y = 0; $y -lt $Size; $y++) {
            $t = $y / [double]($Size - 1)
            $c = [System.Drawing.Color]::FromArgb(
                255,
                (Lerp $top.R $bot.R $t),
                (Lerp $top.G $bot.G $t),
                (Lerp $top.B $bot.B $t)
            )

            # Add a dim horizontal glow band (different per face).
            $bandCenter = [int]($Size * (0.35 + 0.3 * ($rng.NextDouble())))
            $bandDist = [Math]::Abs($y - $bandCenter)
            $bandStrength = [Math]::Max(0.0, 1.0 - ($bandDist / ($Size * 0.18)))

            for ($x = 0; $x -lt $Size; $x++) {
                $pixel = $c
                if ($bandStrength -gt 0) {
                    # Slight shimmer inside the band
                    $j = 0.75 + 0.25 * [Math]::Sin(($x + $seed) * 0.02)
                    $blend = [Math]::Min(1.0, $bandStrength * 0.55 * $j)
                    $pixel = [System.Drawing.Color]::FromArgb(
                        255,
                        (Lerp $pixel.R $glow.R $blend),
                        (Lerp $pixel.G $glow.G $blend),
                        (Lerp $pixel.B $glow.B $blend)
                    )
                }

                $bmp.SetPixel($x, $y, $pixel)
            }
        }

        # Scatter a few "rune" sparks and drifting dust specks.
        $sparks = [int]($Size * 0.9)
        for ($i = 0; $i -lt $sparks; $i++) {
            $x = $rng.Next(0, $Size)
            $y = $rng.Next(0, $Size)
            $bmp.SetPixel($x, $y, $rune)
        }

        $dust = [int]($Size * 1.6)
        $dustA = [System.Drawing.Color]::FromArgb(255, 50, 50, 62)
        for ($i = 0; $i -lt $dust; $i++) {
            $x = $rng.Next(0, $Size)
            $y = $rng.Next(0, $Size)
            $bmp.SetPixel($x, $y, $dustA)
        }

        $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Host "Created $fileName"
    }
    finally {
        $bmp.Dispose()
    }
}

New-Face 'north' 'wayfall_skybox_north.png'
New-Face 'south' 'wayfall_skybox_south.png'
New-Face 'east'  'wayfall_skybox_east.png'
New-Face 'west'  'wayfall_skybox_west.png'
New-Face 'up'    'wayfall_skybox_up.png'
New-Face 'down'  'wayfall_skybox_down.png'
