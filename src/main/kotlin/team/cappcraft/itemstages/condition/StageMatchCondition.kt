package team.cappcraft.itemstages.condition

import net.darkhax.gamestages.GameStageHelper
import net.minecraft.entity.player.EntityPlayer

@Suppress("MemberVisibilityCanBePrivate")
object StageMatchCondition {
    abstract class StageCondition(vararg pattern: String) : ICondition<EntityPlayer> {
        val pattern = pattern.toSet()
    }
    
    /**
     * Check if [EntityPlayer] has any of the stage in pattern
     */
    class Any(vararg pattern: String) : StageCondition(*pattern) {
        override fun match(toMatch: EntityPlayer): Boolean {
            return GameStageHelper.hasAnyOf(toMatch, pattern)
        }
    }
    
    /**
     * Check if [EntityPlayer] has all the stage in pattern
     */
    class All(vararg pattern: String) : StageCondition(*pattern) {
        override fun match(toMatch: EntityPlayer): Boolean {
            return GameStageHelper.hasAllOf(toMatch, pattern)
        }
    }
}