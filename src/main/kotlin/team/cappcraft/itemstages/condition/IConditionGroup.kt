package team.cappcraft.itemstages.condition

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenConstructor

/**
 * Condition group, allow NOT condition
 *
 * @param T the type of toMatch
 * Key -> the [ICondition]
 * Value -> expected match result, set to false to indicate a NOT condition
 */
interface IConditionGroup<T> : ICondition<T>, MutableMap<ICondition<T>, Boolean>, Map<ICondition<T>, Boolean>

abstract class AbstractConditionGroup<T>(val conditions: MutableMap<ICondition<T>, Boolean>) : IConditionGroup<T> {
    constructor(vararg conditionPair: Pair<ICondition<T>, Boolean>) : this(conditionPair.toMap(mutableMapOf()))
    
    override val size: Int
        get() = conditions.size
    
    override fun containsKey(key: ICondition<T>): Boolean {
        return conditions.containsKey(key)
    }
    
    override fun containsValue(value: Boolean): Boolean {
        return conditions.containsValue(value)
    }
    
    override fun get(key: ICondition<T>): Boolean? {
        return conditions[key]
    }
    
    override fun isEmpty(): Boolean {
        return conditions.isEmpty()
    }
    
    override val entries: MutableSet<MutableMap.MutableEntry<ICondition<T>, Boolean>>
        get() = conditions.entries
    override val keys: MutableSet<ICondition<T>>
        get() = conditions.keys
    override val values: MutableCollection<Boolean>
        get() = conditions.values
    
    override fun clear() {
        conditions.clear()
    }
    
    override fun put(key: ICondition<T>, value: Boolean): Boolean? {
        return conditions.put(key, value)
    }
    
    override fun putAll(from: Map<out ICondition<T>, Boolean>) {
        return conditions.putAll(from)
    }
    
    override fun remove(key: ICondition<T>): Boolean? {
        return conditions.remove(key)
    }
    
}

@ZenRegister
@ZenClass("team.cappcraft.itemstages.condition.AnyConditionGroup")
class AnyConditionGroup<T>(vararg conditionPair: Pair<ICondition<T>, Boolean>) :
    AbstractConditionGroup<T>(*conditionPair) {
    @ZenConstructor
    constructor(vararg conditionGroup: ICondition<T>) : this(
        *conditionGroup.associate { it to true }.toList().toTypedArray()
    )
    
    /**
     * @return true if any condition matches
     */
    override fun match(toMatch: T): Boolean {
        return conditions.any { it.key.match(toMatch) == it.value }
    }
}

@ZenRegister
@ZenClass("team.cappcraft.itemstages.condition.AllConditionGroup")
class AllConditionGroup<T>(vararg conditionPair: Pair<ICondition<T>, Boolean>) :
    AbstractConditionGroup<T>(*conditionPair) {
    @ZenConstructor
    constructor(vararg conditionGroup: ICondition<T>) : this(
        *conditionGroup.associate { it to true }.toList().toTypedArray()
    )
    
    /**
     * @return true if all the condition matches
     */
    override fun match(toMatch: T): Boolean {
        return conditions.all { it.key.match(toMatch) == it.value }
    }
}