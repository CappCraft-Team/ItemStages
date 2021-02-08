package team.cappcraft.itemstages.condition

import crafttweaker.api.enchantments.IEnchantment
import crafttweaker.api.enchantments.IEnchantmentDefinition
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack

@Suppress("MemberVisibilityCanBePrivate")
object EnchantmentMatchCondition {
    abstract class EnchantmentCondition : ICondition<IIngredient>
    
    class Any(vararg pattern: IEnchantment) : EnchantmentCondition() {
        constructor(vararg pattern: IEnchantmentDefinition) : this(*toEnchantment(pattern))
        
        val pattern = pattern.toSet()
        override fun match(toMatch: IIngredient): Boolean {
            return toMatch is IItemStack && toMatch.enchantments.any { itemEnchantment ->
                pattern.any { itemEnchantment.definition.id == it.definition.id && itemEnchantment.level == it.level }
            }
        }
    }
    
    /**
     * Check if the itemStack contains all the enchantments in the pattern
     */
    class All(vararg pattern: IEnchantment) : EnchantmentCondition() {
        constructor(vararg pattern: IEnchantmentDefinition) : this(*toEnchantment(pattern))
        
        val pattern = pattern.toSet()
        override fun match(toMatch: IIngredient): Boolean {
            return toMatch is IItemStack && toMatch.enchantments.all { itemEnchantment ->
                pattern.any { itemEnchantment.definition.id == it.definition.id && itemEnchantment.level == it.level }
            }
        }
    }
    
    private fun toEnchantment(pattern: Array<out IEnchantmentDefinition>): Array<out IEnchantment> {
        return pattern.let {
            val enchantments = mutableListOf<IEnchantment>()
            it.forEach {
                for (level in it.minLevel..it.maxLevel)
                    enchantments.add(it.makeEnchantment(level))
            }
            enchantments.toTypedArray()
        }
    }
}