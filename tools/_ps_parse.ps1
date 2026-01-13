[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string]$Path
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

if (-not (Test-Path -LiteralPath $Path)) {
    throw "File not found: $Path"
}

$t = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
[void][scriptblock]::Create($t)
Write-Output 'PARSE_OK'
