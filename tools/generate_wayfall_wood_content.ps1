Param(
    [switch]$Force
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..')

$assetsRoot = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard'
$dataRoot = Join-Path $repoRoot 'src/main/resources/data/kruemblegard'

$blockstatesDir = Join-Path $assetsRoot 'blockstates'
$modelsBlockDir = Join-Path $assetsRoot 'models/block'
$modelsItemDir  = Join-Path $assetsRoot 'models/item'
$texturesBlockDir = Join-Path $assetsRoot 'textures/block'
$texturesItemDir  = Join-Path $assetsRoot 'textures/item'
$lootBlocksDir  = Join-Path $dataRoot 'loot_tables/blocks'
$recipesDir     = Join-Path $dataRoot 'recipes'

$woods = @(
    @{ id = 'wayroot'; log='wayroot_log'; planks='wayroot_planks'; leaves='wayroot_leaves'; sapling='wayroot_sapling' },
    @{ id = 'fallbark'; log='fallbark_log'; planks='fallbark_planks'; leaves='fallbark_leaves'; sapling='fallbark_sapling' },
    @{ id = 'echowood'; log='echowood_log'; planks='echowood_planks'; leaves='echowood_leaves'; sapling='echowood_sapling' },
    @{ id = 'cairn_tree'; log='cairn_tree_log'; planks='cairn_tree_planks'; leaves='cairn_tree_leaves'; sapling='cairn_tree_sapling' },
    @{ id = 'wayglass'; log='wayglass_log'; planks='wayglass_planks'; leaves='wayglass_leaves'; sapling='wayglass_sapling' },
    @{ id = 'shardbark_pine'; log='shardbark_pine_log'; planks='shardbark_pine_planks'; leaves='shardbark_pine_leaves'; sapling='shardbark_pine_sapling' },
    @{ id = 'hollowway_tree'; log='hollowway_tree_log'; planks='hollowway_tree_planks'; leaves='hollowway_tree_leaves'; sapling='hollowway_tree_sapling' },
    @{ id = 'driftwillow'; log='driftwillow_log'; planks='driftwillow_planks'; leaves='driftwillow_leaves'; sapling='driftwillow_sapling' },
    @{ id = 'monument_oak'; log='monument_oak_log'; planks='monument_oak_planks'; leaves='monument_oak_leaves'; sapling='monument_oak_sapling' },
    @{ id = 'waytorch_tree'; log='waytorch_tree_log'; planks='waytorch_tree_planks'; leaves='waytorch_tree_leaves'; sapling='waytorch_tree_sapling' },
    @{ id = 'faultwood'; log='faultwood_log'; planks='faultwood_planks'; leaves='faultwood_leaves'; sapling='faultwood_sapling' }
)

function Write-JsonFile {
    param(
        [Parameter(Mandatory=$true)][string]$Path,
        [Parameter(Mandatory=$true)][object]$Object
    )

    if (-not $Force -and (Test-Path $Path)) {
        return
    }

    $dir = Split-Path -Parent $Path
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }

    $json = $Object | ConvertTo-Json -Depth 32
    $json = $json -replace "\\u003c", "<" -replace "\\u003e", ">" -replace "\\u0026", "&"
    Set-Content -Path $Path -Value $json -Encoding UTF8
}

function Ensure-FileCopy {
    param(
        [Parameter(Mandatory=$true)][string]$Source,
        [Parameter(Mandatory=$true)][string]$Dest
    )

    if (-not (Test-Path $Source)) {
        throw "Missing source file: $Source"
    }

    if (-not $Force -and (Test-Path $Dest)) {
        return
    }

    $dir = Split-Path -Parent $Dest
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }

    Copy-Item -Path $Source -Destination $Dest -Force
}

function RotVariants($model) {
    return @{
        "axis=y" = @{ model = $model }
        "axis=z" = @{ model = $model; x = 90 }
        "axis=x" = @{ model = $model; x = 90; y = 90 }
    }
}

function StairsVariants($name) {
    $mStraight = "kruemblegard:block/${name}"
    $mInner = "kruemblegard:block/${name}_inner"
    $mOuter = "kruemblegard:block/${name}_outer"
    $faceY = @{ east=0; south=90; west=180; north=270 }

    $variants = @{}

    foreach ($half in @('bottom','top')) {
        foreach ($shape in @('straight','inner_left','inner_right','outer_left','outer_right')) {
            $model = $mStraight
            if ($shape -like 'inner_*') { $model = $mInner }
            if ($shape -like 'outer_*') { $model = $mOuter }

            foreach ($facing in @('east','west','south','north')) {
                $key = "facing=$facing,half=$half,shape=$shape"
                $entry = @{ model = $model }

                $y = $faceY[$facing]
                $x = if ($half -eq 'top') { 180 } else { 0 }

                # Base rotations
                if ($x -ne 0) { $entry.x = $x }
                if ($y -ne 0) { $entry.y = $y }

                # Shape-specific adjustments (vanilla stair blockstate conventions)
                if ($shape -eq 'inner_left') {
                    $entry.y = ($y + 270) % 360
                } elseif ($shape -eq 'inner_right') {
                    $entry.y = ($y + 0) % 360
                } elseif ($shape -eq 'outer_left') {
                    $entry.y = ($y + 270) % 360
                } elseif ($shape -eq 'outer_right') {
                    $entry.y = ($y + 0) % 360
                }

                $variants[$key] = $entry
            }
        }
    }

    return $variants
}

function SlabVariants($name) {
    return @{
        "type=bottom" = @{ model = "kruemblegard:block/${name}" }
        "type=top" = @{ model = "kruemblegard:block/${name}_top" }
        "type=double" = @{ model = "kruemblegard:block/${name}_double" }
    }
}

function FenceMultipart($name) {
    $post = @{ apply = @{ model = "kruemblegard:block/${name}_post" } }
    $side = "kruemblegard:block/${name}_side"

    return @(
        $post,
        @{ when = @{ north = "true" }; apply = @{ model = $side; uvlock = $true; y = 0 } },
        @{ when = @{ east = "true" };  apply = @{ model = $side; uvlock = $true; y = 90 } },
        @{ when = @{ south = "true" }; apply = @{ model = $side; uvlock = $true; y = 180 } },
        @{ when = @{ west = "true" };  apply = @{ model = $side; uvlock = $true; y = 270 } }
    )
}

function PressurePlateVariants($name) {
    return @{
        "powered=false" = @{ model = "kruemblegard:block/${name}" }
        "powered=true" = @{ model = "kruemblegard:block/${name}_down" }
    }
}

function TrapdoorVariants($name) {
    $mBottom = "kruemblegard:block/${name}_bottom"
    $mTop = "kruemblegard:block/${name}_top"
    $mOpen = "kruemblegard:block/${name}_open"

    $variants = @{}
    $faceY = @{ north=0; east=90; south=180; west=270 }

    foreach ($facing in @('north','south','west','east')) {
        foreach ($half in @('bottom','top')) {
            foreach ($open in @('false','true')) {
                $key = "facing=$facing,half=$half,open=$open"
                $entry = @{}

                if ($open -eq 'true') {
                    $entry.model = $mOpen
                    $entry.y = $faceY[$facing]
                } else {
                    $entry.model = if ($half -eq 'top') { $mTop } else { $mBottom }
                    $entry.y = $faceY[$facing]
                }

                if ($entry.y -eq 0) { $entry.Remove('y') | Out-Null }

                $variants[$key] = $entry
            }
        }
    }

    return $variants
}

function GateVariants($name) {
    $variants = @{}
    $faceY = @{ north=0; east=90; south=180; west=270 }

    foreach ($facing in @('north','south','west','east')) {
        foreach ($open in @('false','true')) {
            foreach ($inWall in @('false','true')) {
                $key = "facing=$facing,in_wall=$inWall,open=$open"
                $model = "kruemblegard:block/${name}"

                if ($inWall -eq 'true' -and $open -eq 'true') { $model = "kruemblegard:block/${name}_wall_open" }
                elseif ($inWall -eq 'true' -and $open -eq 'false') { $model = "kruemblegard:block/${name}_wall" }
                elseif ($inWall -eq 'false' -and $open -eq 'true') { $model = "kruemblegard:block/${name}_open" }

                $entry = @{ model = $model }
                $y = $faceY[$facing]
                if ($y -ne 0) { $entry.y = $y }

                $variants[$key] = $entry
            }
        }
    }

    return $variants
}

function DoorVariants($name) {
    $variants = @{}
    $faceY = @{ north=0; east=90; south=180; west=270 }

    foreach ($facing in @('north','south','west','east')) {
        foreach ($half in @('lower','upper')) {
            foreach ($hinge in @('left','right')) {
                foreach ($open in @('false','true')) {
                    $key = "facing=$facing,half=$half,hinge=$hinge,open=$open"

                    $suffix = if ($half -eq 'lower') { 'bottom' } else { 'top' }
                    $part = if ($hinge -eq 'left') { 'left' } else { 'right' }

                    $model = "kruemblegard:block/${name}_${suffix}_${part}"

                    $entry = @{ model = $model }

                    $y = $faceY[$facing]
                    if ($open -eq 'true') {
                        $y = ($y + 90) % 360
                    }

                    if ($y -ne 0) { $entry.y = $y }
                    $variants[$key] = $entry
                }
            }
        }
    }

    return $variants
}

function ButtonVariants($name) {
    $variants = @{}
    $faceY = @{ south=0; west=90; north=180; east=270 }

    foreach ($face in @('ceiling','floor','wall')) {
        foreach ($facing in @('north','south','west','east')) {
            foreach ($powered in @('false','true')) {
                $key = "face=$face,facing=$facing,powered=$powered"
                $model = if ($powered -eq 'true') { "kruemblegard:block/${name}_pressed" } else { "kruemblegard:block/${name}" }
                $entry = @{ model = $model }

                if ($face -eq 'ceiling') { $entry.x = 180 }
                elseif ($face -eq 'wall') { $entry.x = 90 }

                $y = $faceY[$facing]
                if ($y -ne 0) { $entry.y = $y }

                $variants[$key] = $entry
            }
        }
    }

    return $variants
}

function SimpleSelfDropLoot($blockName) {
    return @{
        type = "minecraft:block"
        pools = @(
            @{
                rolls = 1
                entries = @(
                    @{
                        type = "minecraft:item"
                        name = "kruemblegard:$blockName"
                    }
                )
            }
        )
    }
}

function SlabLoot($blockName) {
    return @{
        type = "minecraft:block"
        pools = @(
            @{
                rolls = 1
                entries = @(
                    @{
                        type = "minecraft:item"
                        name = "kruemblegard:$blockName"
                    }
                )
                functions = @(
                    @{
                        function = "minecraft:set_count"
                        conditions = @(
                            @{
                                condition = "minecraft:block_state_property"
                                block = "kruemblegard:$blockName"
                                properties = @{ type = "double" }
                            }
                        )
                        count = 2
                    }
                )
            }
        )
    }
}

function DoorLoot($blockName) {
    return @{
        type = "minecraft:block"
        pools = @(
            @{
                rolls = 1
                entries = @(
                    @{
                        type = "minecraft:item"
                        name = "kruemblegard:$blockName"
                    }
                )
                conditions = @(
                    @{
                        condition = "minecraft:block_state_property"
                        block = "kruemblegard:$blockName"
                        properties = @{ half = "lower" }
                    }
                )
            }
        )
    }
}

function ShapedRecipe($resultItem, $count, $pattern, $key) {
    return @{
        type = "minecraft:crafting_shaped"
        pattern = $pattern
        key = $key
        result = @{ item = "kruemblegard:$resultItem"; count = $count }
    }
}

function ShapelessRecipe($resultItem, $count, $ingredients) {
    return @{
        type = "minecraft:crafting_shapeless"
        ingredients = $ingredients
        result = @{ item = "kruemblegard:$resultItem"; count = $count }
    }
}

Write-Host "Generating Wayfall wood assets/recipes/loot..." -ForegroundColor Cyan

foreach ($w in $woods) {
    $planks = $w.planks

    $family = @{
        stairs = "${planks}_stairs" -replace "_planks",""
        slab = "${planks}_slab" -replace "_planks",""
        fence = "${planks}_fence" -replace "_planks",""
        gate = "${planks}_fence_gate" -replace "_planks",""
        door = "${planks}_door" -replace "_planks",""
        trapdoor = "${planks}_trapdoor" -replace "_planks",""
        button = "${planks}_button" -replace "_planks",""
        plate = "${planks}_pressure_plate" -replace "_planks",""
    }

    # Dedicated (placeholder) textures per wood-part. These are initially copied from planks
    # so they exist as real files and can be painted later without needing JSON changes.
    $planksPng = Join-Path $texturesBlockDir "$planks.png"

    $stairsName = $family.stairs
    $slabName = $family.slab
    $fenceName = $family.fence
    $gateName = $family.gate
    $doorName = $family.door
    $trapName = $family.trapdoor
    $buttonName = $family.button
    $plateName = $family.plate

    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$stairsName.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$slabName.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$fenceName.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$gateName.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "${doorName}_top.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "${doorName}_bottom.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$trapName.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$buttonName.png")
    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesBlockDir "$plateName.png")

    Ensure-FileCopy -Source $planksPng -Dest (Join-Path $texturesItemDir "$doorName.png")

    # Base models/blockstates/item models (create if missing)
    Write-JsonFile (Join-Path $modelsBlockDir "$($w.log).json") @{ parent = "minecraft:block/cube_column"; textures = @{ end = "kruemblegard:block/$($w.log)"; side = "kruemblegard:block/$($w.log)" } }
    Write-JsonFile (Join-Path $modelsBlockDir "$($w.planks).json") @{ parent = "minecraft:block/cube_all"; textures = @{ all = "kruemblegard:block/$($w.planks)" } }
    Write-JsonFile (Join-Path $modelsBlockDir "$($w.leaves).json") @{ parent = "minecraft:block/cube_all"; textures = @{ all = "kruemblegard:block/$($w.leaves)" } }
    Write-JsonFile (Join-Path $modelsBlockDir "$($w.sapling).json") @{ parent = "minecraft:block/cross"; textures = @{ cross = "kruemblegard:block/$($w.sapling)" } }

    Write-JsonFile (Join-Path $blockstatesDir "$($w.log).json") @{ variants = (RotVariants("kruemblegard:block/$($w.log)")) }
    Write-JsonFile (Join-Path $blockstatesDir "$($w.planks).json") @{ variants = @{ "" = @{ model = "kruemblegard:block/$($w.planks)" } } }
    Write-JsonFile (Join-Path $blockstatesDir "$($w.leaves).json") @{ variants = @{ "" = @{ model = "kruemblegard:block/$($w.leaves)" } } }
    Write-JsonFile (Join-Path $blockstatesDir "$($w.sapling).json") @{ variants = @{ "" = @{ model = "kruemblegard:block/$($w.sapling)" } } }

    Write-JsonFile (Join-Path $modelsItemDir "$($w.log).json") @{ parent = "kruemblegard:block/$($w.log)" }
    Write-JsonFile (Join-Path $modelsItemDir "$($w.planks).json") @{ parent = "kruemblegard:block/$($w.planks)" }
    Write-JsonFile (Join-Path $modelsItemDir "$($w.leaves).json") @{ parent = "kruemblegard:block/$($w.leaves)" }
    Write-JsonFile (Join-Path $modelsItemDir "$($w.sapling).json") @{ parent = "kruemblegard:block/$($w.sapling)" }

    # --- Stairs ---
    $stairsTex = "kruemblegard:block/$stairsName"
    Write-JsonFile (Join-Path $modelsBlockDir "$stairsName.json") @{ parent = "minecraft:block/stairs"; textures = @{ bottom = $stairsTex; top = $stairsTex; side = $stairsTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${stairsName}_inner.json") @{ parent = "minecraft:block/inner_stairs"; textures = @{ bottom = $stairsTex; top = $stairsTex; side = $stairsTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${stairsName}_outer.json") @{ parent = "minecraft:block/outer_stairs"; textures = @{ bottom = $stairsTex; top = $stairsTex; side = $stairsTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$stairsName.json") @{ variants = (StairsVariants($stairsName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$stairsName.json") @{ parent = "kruemblegard:block/$stairsName" }

    # --- Slab ---
    $slabTex = "kruemblegard:block/$slabName"
    Write-JsonFile (Join-Path $modelsBlockDir "$slabName.json") @{ parent = "minecraft:block/slab"; textures = @{ bottom = $slabTex; top = $slabTex; side = $slabTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${slabName}_top.json") @{ parent = "minecraft:block/slab_top"; textures = @{ bottom = $slabTex; top = $slabTex; side = $slabTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${slabName}_double.json") @{ parent = "minecraft:block/cube_all"; textures = @{ all = $slabTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$slabName.json") @{ variants = (SlabVariants($slabName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$slabName.json") @{ parent = "kruemblegard:block/$slabName" }

    # --- Fence ---
    $fenceTex = "kruemblegard:block/$fenceName"
    Write-JsonFile (Join-Path $modelsBlockDir "${fenceName}_post.json") @{ parent = "minecraft:block/fence_post"; textures = @{ texture = $fenceTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${fenceName}_side.json") @{ parent = "minecraft:block/fence_side"; textures = @{ texture = $fenceTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${fenceName}_inventory.json") @{ parent = "minecraft:block/fence_inventory"; textures = @{ texture = $fenceTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$fenceName.json") @{ multipart = (FenceMultipart($fenceName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$fenceName.json") @{ parent = "kruemblegard:block/${fenceName}_inventory" }

    # --- Fence Gate ---
    $gateTex = "kruemblegard:block/$gateName"
    Write-JsonFile (Join-Path $modelsBlockDir "${gateName}.json") @{ parent = "minecraft:block/fence_gate"; textures = @{ texture = $gateTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${gateName}_open.json") @{ parent = "minecraft:block/fence_gate_open"; textures = @{ texture = $gateTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${gateName}_wall.json") @{ parent = "minecraft:block/fence_gate_wall"; textures = @{ texture = $gateTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${gateName}_wall_open.json") @{ parent = "minecraft:block/fence_gate_wall_open"; textures = @{ texture = $gateTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$gateName.json") @{ variants = (GateVariants($gateName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$gateName.json") @{ parent = "kruemblegard:block/$gateName" }

    # --- Door ---
    $doorTexTop = "kruemblegard:block/${doorName}_top"
    $doorTexBottom = "kruemblegard:block/${doorName}_bottom"
    Write-JsonFile (Join-Path $modelsBlockDir "${doorName}_bottom_left.json") @{ parent = "minecraft:block/door_bottom_left"; textures = @{ bottom = $doorTexBottom; top = $doorTexTop } }
    Write-JsonFile (Join-Path $modelsBlockDir "${doorName}_bottom_right.json") @{ parent = "minecraft:block/door_bottom_right"; textures = @{ bottom = $doorTexBottom; top = $doorTexTop } }
    Write-JsonFile (Join-Path $modelsBlockDir "${doorName}_top_left.json") @{ parent = "minecraft:block/door_top_left"; textures = @{ bottom = $doorTexBottom; top = $doorTexTop } }
    Write-JsonFile (Join-Path $modelsBlockDir "${doorName}_top_right.json") @{ parent = "minecraft:block/door_top_right"; textures = @{ bottom = $doorTexBottom; top = $doorTexTop } }
    Write-JsonFile (Join-Path $blockstatesDir "$doorName.json") @{ variants = (DoorVariants($doorName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$doorName.json") @{ parent = "minecraft:item/generated"; textures = @{ layer0 = "kruemblegard:item/$doorName" } }

    # --- Trapdoor ---
    $trapTex = "kruemblegard:block/$trapName"
    Write-JsonFile (Join-Path $modelsBlockDir "${trapName}_bottom.json") @{ parent = "minecraft:block/template_orientable_trapdoor_bottom"; textures = @{ texture = $trapTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${trapName}_top.json") @{ parent = "minecraft:block/template_orientable_trapdoor_top"; textures = @{ texture = $trapTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${trapName}_open.json") @{ parent = "minecraft:block/template_orientable_trapdoor_open"; textures = @{ texture = $trapTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$trapName.json") @{ variants = (TrapdoorVariants($trapName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$trapName.json") @{ parent = "kruemblegard:block/${trapName}_bottom" }

    # --- Button ---
    $buttonTex = "kruemblegard:block/$buttonName"
    Write-JsonFile (Join-Path $modelsBlockDir "${buttonName}.json") @{ parent = "minecraft:block/button"; textures = @{ texture = $buttonTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${buttonName}_pressed.json") @{ parent = "minecraft:block/button_pressed"; textures = @{ texture = $buttonTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${buttonName}_inventory.json") @{ parent = "minecraft:block/button_inventory"; textures = @{ texture = $buttonTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$buttonName.json") @{ variants = (ButtonVariants($buttonName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$buttonName.json") @{ parent = "kruemblegard:block/${buttonName}_inventory" }

    # --- Pressure Plate ---
    $plateTex = "kruemblegard:block/$plateName"
    Write-JsonFile (Join-Path $modelsBlockDir "${plateName}.json") @{ parent = "minecraft:block/pressure_plate_up"; textures = @{ texture = $plateTex } }
    Write-JsonFile (Join-Path $modelsBlockDir "${plateName}_down.json") @{ parent = "minecraft:block/pressure_plate_down"; textures = @{ texture = $plateTex } }
    Write-JsonFile (Join-Path $blockstatesDir "$plateName.json") @{ variants = (PressurePlateVariants($plateName)) }
    Write-JsonFile (Join-Path $modelsItemDir "$plateName.json") @{ parent = "kruemblegard:block/${plateName}" }

    # Loot tables
    Write-JsonFile (Join-Path $lootBlocksDir "$stairsName.json") (SimpleSelfDropLoot($stairsName))
    Write-JsonFile (Join-Path $lootBlocksDir "$slabName.json") (SlabLoot($slabName))
    Write-JsonFile (Join-Path $lootBlocksDir "$fenceName.json") (SimpleSelfDropLoot($fenceName))
    Write-JsonFile (Join-Path $lootBlocksDir "$gateName.json") (SimpleSelfDropLoot($gateName))
    Write-JsonFile (Join-Path $lootBlocksDir "$doorName.json") (DoorLoot($doorName))
    Write-JsonFile (Join-Path $lootBlocksDir "$trapName.json") (SimpleSelfDropLoot($trapName))
    Write-JsonFile (Join-Path $lootBlocksDir "$buttonName.json") (SimpleSelfDropLoot($buttonName))
    Write-JsonFile (Join-Path $lootBlocksDir "$plateName.json") (SimpleSelfDropLoot($plateName))

    # Recipes
    $logName = $w.log

    Write-JsonFile (Join-Path $recipesDir "${planks}_from_${logName}.json") (ShapelessRecipe($planks, 4, @(@{ item = "kruemblegard:$logName" })))

    Write-JsonFile (Join-Path $recipesDir "${stairsName}.json") (ShapedRecipe($stairsName, 4, @("#  ","## ","###"), @{ "#" = @{ item = "kruemblegard:$planks" } }))
    Write-JsonFile (Join-Path $recipesDir "${slabName}.json") (ShapedRecipe($slabName, 6, @("###"), @{ "#" = @{ item = "kruemblegard:$planks" } }))
    Write-JsonFile (Join-Path $recipesDir "${fenceName}.json") (ShapedRecipe($fenceName, 3, @("#S#","#S#"), @{ "#" = @{ item = "kruemblegard:$planks" }; "S" = @{ item = "minecraft:stick" } }))
    Write-JsonFile (Join-Path $recipesDir "${gateName}.json") (ShapedRecipe($gateName, 1, @("S#S","S#S"), @{ "#" = @{ item = "kruemblegard:$planks" }; "S" = @{ item = "minecraft:stick" } }))
    Write-JsonFile (Join-Path $recipesDir "${doorName}.json") (ShapedRecipe($doorName, 3, @("##","##","##"), @{ "#" = @{ item = "kruemblegard:$planks" } }))
    Write-JsonFile (Join-Path $recipesDir "${trapName}.json") (ShapedRecipe($trapName, 2, @("###","###"), @{ "#" = @{ item = "kruemblegard:$planks" } }))
    Write-JsonFile (Join-Path $recipesDir "${buttonName}.json") (ShapelessRecipe($buttonName, 1, @(@{ item = "kruemblegard:$planks" })))
    Write-JsonFile (Join-Path $recipesDir "${plateName}.json") (ShapedRecipe($plateName, 1, @("##"), @{ "#" = @{ item = "kruemblegard:$planks" } }))
}

Write-Host "Done." -ForegroundColor Green
