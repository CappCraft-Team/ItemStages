package team.cappcraft.itemstages.condition

@Suppress("MemberVisibilityCanBePrivate")
object RecipeCategoryMatchCondition {
    abstract class RecipeCategoryCondition : ICondition<String>
    
    class Any(vararg category: String) : RecipeCategoryCondition() {
        val category = category.asList()
        override fun match(toMatch: String): Boolean {
            return category.contains(toMatch)
        }
    }
}