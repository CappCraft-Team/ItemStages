package team.cappcraft.itemstages.condition

@Suppress("MemberVisibilityCanBePrivate")
object ToolTipMatchCondition {
    abstract class ToolTipCondition(vararg pattern: String) : ICondition<String> {
        val pattern = pattern.map { Regex(it) }
    }
    
    /**
     * Check if the tooltip strings matches any the regex pattern
     */
    class Any(vararg pattern: String) : ToolTipCondition(*pattern) {
        override fun match(toMatch: String): Boolean {
            return pattern.any(toMatch::contains)
        }
    }
}