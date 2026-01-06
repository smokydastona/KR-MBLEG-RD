[
    Diagnostics.CodeAnalysis.SuppressMessageAttribute(
        'PSUseApprovedVerbs',
        '',
        Justification = 'Local helper functions in this script are not exported cmdlets; keep names concise.'
    )
]
param(
    [int]$Width = 2048,
    [int]$Height = 1024,
    [string]$OutPath = "src/main/resources/assets/kruemblegard/textures/environment/wayfall_panorama.png"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if ($Height * 2 -ne $Width) {
    throw "Panorama must be 2:1 aspect ratio (Width == 2 * Height). Got ${Width}x${Height}."
}

$fullOut = Join-Path -Path (Get-Location) -ChildPath $OutPath
$dir = Split-Path -Parent $fullOut
if (-not (Test-Path $dir)) {
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
}

Add-Type -AssemblyName System.Drawing

$bmp = New-Object System.Drawing.Bitmap($Width, $Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$gfx = [System.Drawing.Graphics]::FromImage($bmp)
$gfx.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$gfx.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$gfx.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

# Background gradient: darker zenith, lighter horizon, darker nadir
$bg = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Rectangle(0, 0, $Width, $Height)),
    [System.Drawing.Color]::FromArgb(255, 18, 18, 32),
    [System.Drawing.Color]::FromArgb(255, 80, 70, 120),
    [System.Drawing.Drawing2D.LinearGradientMode]::Vertical
)
$gfx.FillRectangle($bg, 0, 0, $Width, $Height)
$bg.Dispose()

# Add a faint second band near the horizon
$horizonY = [int]($Height * 0.55)
$bandTop = [int]($horizonY - 120)
$bandRect = New-Object System.Drawing.Rectangle(0, $bandTop, $Width, 240)
$band = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    $bandRect,
    [System.Drawing.Color]::FromArgb(0, 0, 0, 0),
    [System.Drawing.Color]::FromArgb(90, 180, 160, 220),
    [System.Drawing.Drawing2D.LinearGradientMode]::Vertical
)
$gfx.FillRectangle($band, $bandRect)
$band.Dispose()

# Grid lines (lat/long)
$gridPenMajor = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(120, 255, 255, 255), 2)
$gridPenMinor = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(60, 255, 255, 255), 1)

# Vertical longitudes every 15°, major every 90°
for ($deg = 0; $deg -le 360; $deg += 15) {
    $x = [int]([math]::Round(($deg / 360.0) * ($Width - 1)))
    $pen = if (($deg % 90) -eq 0) { $gridPenMajor } else { $gridPenMinor }
    $gfx.DrawLine($pen, $x, 0, $x, $Height - 1)
}

# Horizontal latitudes every 15°, major every 45°
for ($deg = -90; $deg -le 90; $deg += 15) {
    $v = ($deg + 90) / 180.0
    $y = [int]([math]::Round($v * ($Height - 1)))
    $pen = if (([math]::Abs($deg) % 45) -eq 0) { $gridPenMajor } else { $gridPenMinor }
    $gfx.DrawLine($pen, 0, $y, $Width - 1, $y)
}

# Labels
$fontTitle = New-Object System.Drawing.Font('Segoe UI', 36, [System.Drawing.FontStyle]::Bold)
$fontSmall = New-Object System.Drawing.Font('Segoe UI', 18, [System.Drawing.FontStyle]::Regular)
$brushText = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(220, 255, 255, 255))
$shadow = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(140, 0, 0, 0))

$title = 'WAYFALL PANORAMA (PLACEHOLDER)'
$hint = 'Replace this with a real equirectangular 2:1 panorama (e.g. 4096x2048).'

function Write-ShadowText($text, $x, $y, $font) {
    $gfx.DrawString($text, $font, $shadow, ($x + 3), ($y + 3))
    $gfx.DrawString($text, $font, $brushText, $x, $y)
}

Write-ShadowText $title 40 30 $fontTitle
Write-ShadowText $hint 40 90 $fontSmall

# Cardinal directions on the horizon band
$labels = @(
    @{ t = 'NORTH (Z-)'; deg = 0 },
    @{ t = 'EAST (X+)'; deg = 90 },
    @{ t = 'SOUTH (Z+)'; deg = 180 },
    @{ t = 'WEST (X-)'; deg = 270 },
    @{ t = 'NORTH (Z-)'; deg = 360 }
)
foreach ($l in $labels) {
    $x = [int]([math]::Round(($l.deg / 360.0) * ($Width - 1)))
    Write-ShadowText $l.t ($x + 12) ($horizonY + 12) $fontSmall
}

# Cleanup + save
$gridPenMajor.Dispose()
$gridPenMinor.Dispose()
$fontTitle.Dispose()
$fontSmall.Dispose()
$brushText.Dispose()
$shadow.Dispose()
$gfx.Dispose()

$bmp.Save($fullOut, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()

Write-Host "Wrote panorama placeholder: $OutPath (${Width}x${Height})"