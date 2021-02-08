package team.cappcraft.itemstages

import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.enchantments.IEnchantment
import crafttweaker.api.enchantments.IEnchantmentDefinition
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemCondition
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import crafttweaker.api.oredict.IOreDictEntry
import net.minecraft.entity.player.EntityPlayer
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod
import team.cappcraft.itemstages.condition.*

@ZenRegister
@ZenClass("team.cappcraft.itemstages.ItemStagesCrT")
class ItemStagesCrT {
    companion object {
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyStage(expectValue: Boolean = true, vararg stage: String): ICondition<EntityPlayer> {
            return AnyConditionGroup(StageMatchCondition.Any(*stage) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun allStage(expectValue: Boolean = true, vararg stage: String): ICondition<EntityPlayer> {
            return AnyConditionGroup(StageMatchCondition.All(*stage) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyIngredient(
            expectValue: Boolean = true,
            vararg ingredient: IIngredient
        ): ICondition<IIngredient> {
            return AnyConditionGroup(IIngredientMatchCondition.Any(*ingredient) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyIngredient(
            expectValue: Boolean = true,
            vararg ingredient: IItemStack
        ): ICondition<IIngredient> {
            return AnyConditionGroup(IIngredientMatchCondition.Any(*ingredient) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyIngredient(
            expectValue: Boolean = true,
            vararg ingredient: IOreDictEntry
        ): ICondition<IIngredient> {
            return AnyConditionGroup(IIngredientMatchCondition.Any(*ingredient) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyIngredient(
            expectValue: Boolean = true,
            vararg ingredient: ILiquidStack
        ): ICondition<IIngredient> {
            return AnyConditionGroup(IIngredientMatchCondition.Any(*ingredient) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyItemCondition(
            expectValue: Boolean = true,
            vararg itemCondition: IItemCondition
        ): ICondition<IIngredient> {
            return AnyConditionGroup(IIngredientMatchCondition.AnyItemCondition(*itemCondition) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyEnchantment(
            expectValue: Boolean = true,
            vararg enchantment: IEnchantment
        ): ICondition<IItemStack> {
            return AnyConditionGroup(EnchantmentMatchCondition.Any(*enchantment) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyEnchantment(
            expectValue: Boolean = true,
            vararg enchantment: IEnchantmentDefinition
        ): ICondition<IItemStack> {
            return AnyConditionGroup(EnchantmentMatchCondition.Any(*enchantment) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun allEnchantment(
            expectValue: Boolean = true,
            vararg enchantment: IEnchantment
        ): ICondition<IItemStack> {
            return AnyConditionGroup(EnchantmentMatchCondition.All(*enchantment) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun allEnchantment(
            expectValue: Boolean = true,
            vararg enchantment: IEnchantmentDefinition
        ): ICondition<IItemStack> {
            return AnyConditionGroup(EnchantmentMatchCondition.All(*enchantment) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyToolTip(expectValue: Boolean = true, vararg pattern: String): ICondition<String> {
            return AnyConditionGroup(ToolTipMatchCondition.Any(*pattern) to expectValue)
        }
        
        @ModOnly("jei")
        @ZenMethod
        @JvmStatic
        @JvmOverloads
        fun anyRecipeCategory(
            expectValue: Boolean = true,
            vararg category: String
        ): ICondition<String> {
            return AnyConditionGroup(RecipeCategoryMatchCondition.Any(*category) to expectValue)
        }
        
        @ZenMethod
        @JvmStatic
        fun addIngredientRestriction(
            isRestrictTarget: ICondition<IIngredient>,
            needToRestrict: ICondition<EntityPlayer>
        ) {
            ItemStages.ingredientRestriction[isRestrictTarget] = needToRestrict
        }
        
        @ZenMethod
        @JvmStatic
        fun addEnchantmentRestriction(
            isRestrictTarget: ICondition<IIngredient>,
            needToRestrict: ICondition<EntityPlayer>
        ) {
            ItemStages.enchantmentRestriction[isRestrictTarget] = needToRestrict
        }
        
        @ZenMethod
        @JvmStatic
        fun addToolTipRestriction(
            isRestrictTarget: ICondition<String>,
            needToRestrict: ICondition<EntityPlayer>
        ) {
            ItemStages.toolTipRestriction[isRestrictTarget] = needToRestrict
        }
        
        @ModOnly("jei")
        @ZenMethod
        @JvmStatic
        fun addRecipeCategoryRestriction(
            isRestrictTarget: ICondition<String>,
            needToRestrict: ICondition<EntityPlayer>
        ) {
            ItemStageJEIPlugin.recipeCategoryRestriction[isRestrictTarget] = needToRestrict
        }
        
        @ZenMethod
        @JvmStatic
        fun addCustomItemName(isRestrictTarget: ICondition<IItemStack>, customName: String) {
            ItemStages.customItemName[isRestrictTarget] = customName
        }
    }
}