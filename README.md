# Item Stages [![](http://cf.way2muchnoise.eu/280316.svg)](https://minecraft.curseforge.com/projects/item-stages) [![](http://cf.way2muchnoise.eu/versions/280316.svg)](https://minecraft.curseforge.com/projects/item-stages)

This mod is an addon for the [GameStages API](https://minecraft.curseforge.com/projects/game-stages). It allows for items and blocks to be placed into custom progression systems.  You should check out the GameStage API mod's description for more info. To give a brief run down, stages are parts of the progression system set up by the modpack or server. Stages are given to players through a command, which is typically ran by a questing mod, advancement, or even a Command Block.

[![Nodecraft](https://nodecraft.com/assets/images/logo-dark.png)](https://nodecraft.com/r/darkhax)    
This project is sponsored by Nodecraft. Use code [Darkhax](https://nodecraft.com/r/darkhax) for 30% off your first month of service!

## Setup

This mod uses [CraftTweaker](https://minecraft.curseforge.com/projects/crafttweaker) for configuration.

This mod adds one new ZenScript method for adding item stage restrictions. You can use a specific item/block id, or an ore dictionary entry. If an ore dictionary is used, all entries for that oredict will be restricted. `mods.ItemStages.addItemStage(String stage, Item/Block/OreDict);`

## Effects

When something is restricted by this mod, several things will happen to prevent the player from using the item. 

- Holding a restricted item will cause it to be dropped immediately.
- The tooltip will be replaced with a restricted message.
- Items will be hidden in JEI if JEI is installed. 
- More to come!

## Example Script

```
// Example Script

import crafttweaker.item.IIngredient;

import team.cappcraft.itemstages.ItemStagesCrT;
import team.cappcraft.itemstages.condition.AnyConditionGroup;
import team.cappcraft.itemstages.condition.AllConditionGroup;

//Stage
val stageGroup = ItemStagesCrT.anyStage("Stage_Dirt", "Stage_Wood");
val stageNoOak = ItemStagesCrT.anyStage("No Oak");

//ItemStack Matcher - Dirt & Wood & chestplate & shield
val itemGroup = AnyConditionGroup([
    ItemStagesCrT.anyIngredient(<minecraft:dirt>, <minecraft:iron_chestplate>, <minecraft:shield>),
    ItemStagesCrT.anyIngredient(<ore:logWood>)
    ]);
//ItemStack Matcher - All the item in the above group except Oak
val Any_Except_Oak = AllConditionGroup([
    itemGroup,
    ItemStagesCrT.anyIngredient(false, <minecraft:log:0>)
    ]);

//Restrict item
ItemStagesCrT.addIngredientRestriction(itemGroup, stageGroup);
ItemStagesCrT.addIngredientRestriction(Any_Except_Oak, stageNoOak);

//Change unfamiliar name
ItemStagesCrT.addCustomItemName(itemGroup, "CustomStageName");

//Recipe Category
ItemStagesCrT.addRecipeCategoryRestriction(
    ItemStagesCrT.anyRecipeCategory("minecraft.fuel"),
    stageGroup);

//Enchantment
val enchantGroup = ItemStagesCrT.anyEnchantment(<enchantment:minecraft:protection>);
ItemStagesCrT.addEnchantmentRestriction(enchantGroup, stageGroup);

//Liquid
val liquidGroup = ItemStagesCrT.anyIngredient(<fluid:lava>);
ItemStagesCrT.addIngredientRestriction(liquidGroup, stageGroup);

//Tooltip - Remove one of armor's  tooltip
val tooltipGroup = ItemStagesCrT.anyToolTip("穿在身上时");
ItemStagesCrT.addToolTipRestriction(tooltipGroup, stageGroup);
```
