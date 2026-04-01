param(
    [switch]$Verify
)

$ErrorActionPreference = 'Stop'

function Resolve-PythonCommand {
    $repoRoot = Split-Path -Parent $PSScriptRoot
    $candidates = @(
        (Join-Path $repoRoot '.venv\Scripts\python.exe'),
        (Join-Path $repoRoot '.venv/bin/python'),
        'python',
        'python3'
    )

    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            return $candidate
        }

        $command = Get-Command $candidate -ErrorAction SilentlyContinue
        if ($command) {
            return $command.Source
        }
    }

    throw 'No Python interpreter found. Install Python or create the repo virtual environment first.'
}

$python = Resolve-PythonCommand
$syncScript = Join-Path $PSScriptRoot 'sync_lang_locales.py'
$coverageScript = Join-Path $PSScriptRoot 'check_lang_translation_coverage.py'

if ($Verify) {
    & $python $syncScript --verify
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }

    & $python $coverageScript
    exit $LASTEXITCODE
}

& $python $syncScript
exit $LASTEXITCODE