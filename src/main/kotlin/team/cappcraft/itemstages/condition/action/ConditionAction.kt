package team.cappcraft.itemstages.condition.action

import team.cappcraft.itemstages.condition.ICondition

/**
 * do action when (not) satisfy condition
 *
 * @param condition the condition to satisfy
 * @param onNotSatisfy decide if we do action when not satisfy the condition
 * @param action the action to do
 * @param T the type of the item to test condition
 */
@Suppress("MemberVisibilityCanBePrivate")
open class ConditionAction<in T>(
    val condition: ICondition<T>,
    val onNotSatisfy: Boolean = false,
    val action: () -> Unit
) {
    /**
     * doAction when satisfy condition
     *
     * if [onNotSatisfy] is true, will do the action when [condition] return false
     *
     * @param toMatch the item to match the action
     * @return if the action has ran
     */
    fun doAction(toMatch: T): Boolean {
        if (condition.match(toMatch) != onNotSatisfy) {
            action()
            return true
        }
        return false
    }
}