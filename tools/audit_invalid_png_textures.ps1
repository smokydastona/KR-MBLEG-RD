param(
    [string]$Root = "src/main/resources/assets/kruemblegard/textures",
    [switch]$IncludeNonKruemblegard
)

$ErrorActionPreference = 'Stop'

function Normalize-Path([string]$p) {
    return $p -replace '\\','/'
}

$workspaceRoot = (Get-Location).Path
$rootPath = Join-Path $workspaceRoot $Root

if (-not (Test-Path $rootPath)) {
    Write-Host "ERROR: Root path not found: $rootPath"
    exit 1
}

try {
    Add-Type -AssemblyName System.Drawing | Out-Null
} catch {
    Write-Host "ERROR: Unable to load System.Drawing. This script expects Windows PowerShell 5.1/.NET Framework.";
    Write-Host $_
    exit 1
}

$pngFiles = Get-ChildItem -Path $rootPath -Recurse -File -Filter '*.png'

if ($IncludeNonKruemblegard) {
    $pngFiles = Get-ChildItem -Path (Join-Path $workspaceRoot "src/main/resources/assets") -Recurse -File -Filter '*.png'
}

$results = New-Object System.Collections.Generic.List[object]

foreach ($file in $pngFiles) {
    $relative = Normalize-Path ($file.FullName.Substring($workspaceRoot.Length).TrimStart('\'))

    if ($file.Length -le 0) {
        $results.Add([pscustomobject]@{
            path = $relative
            sizeBytes = $file.Length
            width = $null
            height = $null
            status = 'invalid'
            reason = 'zero-byte file'
        })
        continue
    }

    $img = $null
    try {
        $img = [System.Drawing.Image]::FromFile($file.FullName)
        $results.Add([pscustomobject]@{
            path = $relative
            sizeBytes = $file.Length
            width = $img.Width
            height = $img.Height
            status = 'ok'
            reason = ''
        })
    } catch {
        $results.Add([pscustomobject]@{
            path = $relative
            sizeBytes = $file.Length
            width = $null
            height = $null
            status = 'invalid'
            reason = $_.Exception.Message
        })
    } finally {
        if ($img -ne $null) {
            $img.Dispose()
        }
    }
}

$invalid = $results | Where-Object { $_.status -eq 'invalid' }

Write-Host "PNG files scanned: $($results.Count)"
Write-Host "Invalid PNG files:  $($invalid.Count)"

if ($invalid.Count -gt 0) {
    Write-Host "--- Invalid PNGs (first 200) ---"
    $invalid | Select-Object -First 200 path,sizeBytes,reason | Format-Table -AutoSize | Out-String | Write-Host
    exit 2
}

# Optional sanity info: list non-square textures (not necessarily wrong, just a quick signal)
$nonSquare = $results | Where-Object { $_.status -eq 'ok' -and $_.width -ne $_.height }
if ($nonSquare.Count -gt 0) {
    Write-Host "Non-square PNGs (FYI): $($nonSquare.Count)"
    $nonSquare | Select-Object -First 50 path,width,height | Format-Table -AutoSize | Out-String | Write-Host
}
