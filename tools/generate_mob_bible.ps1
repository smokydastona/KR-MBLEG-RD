[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Get-RepoRoot {
    $root = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')
    return $root.Path
}

$repoRoot = Get-RepoRoot
$script = Join-Path $repoRoot 'tools/generate_mob_bible.py'

Write-Host "Generating docs/mob_bible.md from tools/_reports/mob_audit_report.json..."
python $script
Write-Host "Done."
