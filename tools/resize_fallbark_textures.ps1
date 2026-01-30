param(
  [switch]$Write
)

$ErrorActionPreference = 'Stop'

# NOTE: This script is legacy / fallbark-only.
# Prefer `tools/resize_block_textures_to_256.ps1` for a repo-wide block-texture pass.
#
# Resizes all fallbark*.png textures under assets/kruemblegard/textures to 256x256.
# - If the source is non-square, it center-crops to a square first (avoids distortion).
# - Uses high-quality downscaling; uses nearest-neighbor when upscaling (keeps pixel art crisp).

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$texturesDir = Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\textures'

if (-not (Test-Path $texturesDir)) {
  throw "Textures directory not found: $texturesDir"
}

Add-Type -AssemblyName System.Drawing

function CenterCrop-Square([System.Drawing.Bitmap]$bmp) {
  if ($bmp.Width -eq $bmp.Height) { return $bmp }

  $side = [Math]::Min($bmp.Width, $bmp.Height)
  $left = [int](($bmp.Width - $side) / 2)
  $top  = [int](($bmp.Height - $side) / 2)

  $rect = New-Object System.Drawing.Rectangle($left, $top, $side, $side)
  return $bmp.Clone($rect, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Resize-To256([System.Drawing.Bitmap]$bmp) {
  $dest = New-Object System.Drawing.Bitmap 256, 256, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
  $dest.SetResolution($bmp.HorizontalResolution, $bmp.VerticalResolution)

  $g = [System.Drawing.Graphics]::FromImage($dest)
  try {
    $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality

    if ($bmp.Width -lt 256 -or $bmp.Height -lt 256) {
      $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
      $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    } else {
      $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
      $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    }

    $g.DrawImage($bmp, 0, 0, 256, 256)
  } finally {
    $g.Dispose()
  }

  return $dest
}

$files = Get-ChildItem -Recurse -File $texturesDir | Where-Object { $_.Name -like 'fallbark*.png' } | Sort-Object FullName

if ($files.Count -eq 0) {
  Write-Output 'No fallbark*.png files found.'
  exit 0
}

$changed = 0

foreach ($f in $files) {
  $rel = $f.FullName.Substring($repoRoot.Length + 1)

  $bmp = [System.Drawing.Bitmap]::FromFile($f.FullName)
  try {
    Write-Output ("{0} -> {1}x{2}" -f $rel, $bmp.Width, $bmp.Height)

    if ($Write -and (-not ($bmp.Width -eq 256 -and $bmp.Height -eq 256))) {
      $cropped = CenterCrop-Square $bmp
      try {
        $resized = Resize-To256 $cropped
        try {
          $resized.Save($f.FullName, [System.Drawing.Imaging.ImageFormat]::Png)
          $changed++
        } finally {
          $resized.Dispose()
        }
      } finally {
        if ($cropped -ne $bmp) { $cropped.Dispose() }
      }
    }
  } finally {
    $bmp.Dispose()
  }
}

if ($Write) {
  Write-Output "Resized $changed file(s)."
}
