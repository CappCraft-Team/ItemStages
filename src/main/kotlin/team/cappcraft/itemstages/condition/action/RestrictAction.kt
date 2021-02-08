package team.cappcraft.itemstages.condition.action

import net.minecraft.entity.player.EntityPlayer
import team.cappcraft.itemstages.condition.ICondition

/**
 * do restriction on target
 *
 * @param isRestrictTarget check if the item is a restrict target
 * @param needToRestrict check if we need to perform the restriction
 * @param restrictAction how to restrict
 * @param T the type of the restrict target
 * @param R the type of the item being restrict
 * @see ConditionAction
 */
@Suppress("MemberVisibilityCanBePrivate")
open class RestrictAction<in T, in R>(
    val isRestrictTarget: ICondition<T>,
    needToRestrict: ICondition<R>,
    restrictAction: () -> Unit
) : ConditionAction<R>(needToRestrict, true, restrictAction) {
    /**
     * do restriction when condition not satisfy
     *
     * [doAction] only calls when [isRestrictTarget] return true
     *
     * @param testRestrict the item to check if it is required to be restricted
     * @param toRestrict the thing we need to perform restriction when needToRestrict condition not satisfy
     * @return if the restrict action has ran
     */
    fun doRestrictAction(testRestrict: T, toRestrict: R): Boolean {
        if (isRestrictTarget.match(testRestrict)) {
            return doAction(toRestrict)
        }
        return false
    }
}

/**
 * do restriction on player
 * @param T the type of the restrict target
 * @see RestrictAction
 */
open class EntityPlayerRestrictAction<in T>(
    restrictRequired: ICondition<T>,
    needToRestrict: ICondition<EntityPlayer>,
    restrictAction: () -> Unit
) : RestrictAction<T, EntityPlayer>(restrictRequired, needToRestrict, restrictAction)