package team.cappcraft.itemstages.condition

/**
 * Interface for conditions
 */
interface ICondition<in T> {
    /**
     * @param toMatch the item to test the condition
     * @return if the item match the condition
     */
    fun match(toMatch: T): Boolean
}

/**
 * Provide some predefined condition
 */
object StandardCondition {
    /**
     * Always return true when match
     */
    object AlwAysTrue : ICondition<Any> {
        override fun match(toMatch: Any): Boolean {
            return true
        }
    }
    
    /**
     * Always return false when match
     */
    object AlwAysFalse : ICondition<Any> {
        override fun match(toMatch: Any): Boolean {
            return false
        }
    }
}