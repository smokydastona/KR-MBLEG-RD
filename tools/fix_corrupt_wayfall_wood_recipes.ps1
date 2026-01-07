# Fixes corrupted Wayfall wood recipes that contain null fields / corrupted item strings.
# Overwrites the affected recipe JSON files with valid vanilla-style shaped/shapeless recipes.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/fix_corrupt_wayfall_wood_recipes.ps1

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$ModId = 'kruemblegard'
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$recipesDir = Join-Path $repoRoot "src/main/resources/data/$ModId/recipes"

if (-not (Test-Path -LiteralPath $recipesDir)) {
    throw "Missing recipes dir: $recipesDir"
}

function Write-JsonFile {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][object]$Object
    )

    $json = $Object | ConvertTo-Json -Depth 32
    $json = $json -replace "\r?\n", "`n"
    Set-Content -Path $Path -Value $json -Encoding UTF8
}

function ShapedRecipe($resultItemId, $count, $pattern, $key) {
    return @{
        type = 'minecraft:crafting_shaped'
        pattern = $pattern
        key = $key
        result = @{ item = $resultItemId; count = $count }
    }
}

function ShapelessRecipe($resultItemId, $count, $ingredients) {
    return @{
        type = 'minecraft:crafting_shapeless'
        ingredients = $ingredients
        result = @{ item = $resultItemId; count = $count }
    }
}

$woods = @(
    @{ id = 'wayroot'; log = 'wayroot_log'; planks = 'wayroot_planks' },
    @{ id = 'fallbark'; log = 'fallbark_log'; planks = 'fallbark_planks' },
    @{ id = 'echowood'; log = 'echowood_log'; planks = 'echowood_planks' },
    @{ id = 'cairn_tree'; log = 'cairn_tree_log'; planks = 'cairn_tree_planks' },
    @{ id = 'wayglass'; log = 'wayglass_log'; planks = 'wayglass_planks' },
    @{ id = 'shardbark_pine'; log = 'shardbark_pine_log'; planks = 'shardbark_pine_planks' },
    @{ id = 'hollowway_tree'; log = 'hollowway_tree_log'; planks = 'hollowway_tree_planks' },
    @{ id = 'driftwillow'; log = 'driftwillow_log'; planks = 'driftwillow_planks' },
    @{ id = 'monument_oak'; log = 'monument_oak_log'; planks = 'monument_oak_planks' },
    @{ id = 'waytorch_tree'; log = 'waytorch_tree_log'; planks = 'waytorch_tree_planks' },
    @{ id = 'faultwood'; log = 'faultwood_log'; planks = 'faultwood_planks' }
)

$fixed = 0
foreach ($w in $woods) {
    $id = $w.id
    $logName = $w.log
    $planksName = $w.planks

    $logId = "${ModId}:$logName"
    $planksId = "${ModId}:$planksName"

    $stairsId = "${ModId}:${id}_stairs"
    $slabId = "${ModId}:${id}_slab"
    $fenceId = "${ModId}:${id}_fence"
    $gateId = "${ModId}:${id}_fence_gate"
    $doorId = "${ModId}:${id}_door"
    $trapdoorId = "${ModId}:${id}_trapdoor"
    $buttonId = "${ModId}:${id}_button"
    $plateId = "${ModId}:${id}_pressure_plate"

    Write-JsonFile (Join-Path $recipesDir "${planksName}_from_${logName}.json") (ShapelessRecipe $planksId 4 @(@{ item = $logId }))
    Write-JsonFile (Join-Path $recipesDir "${id}_stairs.json") (ShapedRecipe $stairsId 4 @('X  ', 'XX ', 'XXX') @{ X = @{ item = $planksId } })
    Write-JsonFile (Join-Path $recipesDir "${id}_slab.json") (ShapedRecipe $slabId 6 @('XXX') @{ X = @{ item = $planksId } })
    Write-JsonFile (Join-Path $recipesDir "${id}_fence.json") (ShapedRecipe $fenceId 3 @('XSX', 'XSX') @{ X = @{ item = $planksId }; S = @{ item = 'minecraft:stick' } })
    Write-JsonFile (Join-Path $recipesDir "${id}_fence_gate.json") (ShapedRecipe $gateId 1 @('SXS', 'SXS') @{ X = @{ item = $planksId }; S = @{ item = 'minecraft:stick' } })
    Write-JsonFile (Join-Path $recipesDir "${id}_door.json") (ShapedRecipe $doorId 3 @('XX', 'XX', 'XX') @{ X = @{ item = $planksId } })
    Write-JsonFile (Join-Path $recipesDir "${id}_trapdoor.json") (ShapedRecipe $trapdoorId 2 @('XXX', 'XXX') @{ X = @{ item = $planksId } })
    Write-JsonFile (Join-Path $recipesDir "${id}_button.json") (ShapelessRecipe $buttonId 1 @(@{ item = $planksId }))
    Write-JsonFile (Join-Path $recipesDir "${id}_pressure_plate.json") (ShapedRecipe $plateId 1 @('XX') @{ X = @{ item = $planksId } })

    $fixed += 9
}

Write-Host "Rewrote recipes: $fixed" -ForegroundColor Green
