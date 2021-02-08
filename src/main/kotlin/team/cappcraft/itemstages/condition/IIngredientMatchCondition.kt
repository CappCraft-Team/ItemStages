package team.cappcraft.itemstages.condition

import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemCondition
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack

@Suppress("MemberVisibilityCanBePrivate")
object IIngredientMatchCondition {
    abstract class IIngredientCondition : ICondition<IIngredient>
    
    /**
     * Check if the [IItemStack]/[ILiquidStack] match the [IIngredient]
     */
    class Any(vararg ingredient: IIngredient) : IIngredientCondition() {
        val ingredient = ingredient.toSet()
        override fun match(toMatch: IIngredient): Boolean {
            return when (toMatch) {
                is IItemStack -> ingredient.any { it.matches(toMatch) }
                is ILiquidStack -> ingredient.any { it.matches(toMatch) }
                else -> false
            }
        }
    }
    
    /**
     * Check if the [IItemStack] match the [IItemCondition]
     */
    class AnyItemCondition(vararg condition: IItemCondition) : IIngredientCondition() {
        val condition = condition.toSet()
        override fun match(toMatch: IIngredient): Boolean {
            return when (toMatch) {
                is IItemStack -> condition.any { it.matches(toMatch) }
                else -> false
            }
        }
    }
}