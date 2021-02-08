package team.cappcraft.itemstages

import crafttweaker.api.item.IIngredient
import crafttweaker.mc1120.item.MCItemStack
import crafttweaker.mc1120.liquid.MCLiquidStack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import net.darkhax.gamestages.event.StagesSyncedEvent
import net.darkhax.itemstages.ConfigurationHandler
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import team.cappcraft.itemstages.condition.ICondition
import team.cappcraft.itemstages.condition.action.EntityPlayerRestrictAction
import thedarkcolour.kotlinforforge.forge.SIDE

@JEIPlugin
class ItemStageJEIPlugin : IModPlugin {
    lateinit var jeiRuntime: IJeiRuntime
    lateinit var modRegistry: IModRegistry
    
    private val hiddenRecipeCategories = mutableSetOf<String>()
    private val hiddenIngredients = mutableSetOf<Any>()
    
    companion object {
        lateinit var INSTANCE: ItemStageJEIPlugin
        
        /**
         * Contain restrict targets and the restrict requirement
         * Key -> restrict target
         * @see team.cappcraft.itemstages.condition.RecipeCategoryMatchCondition
         *
         * Value -> restrict requirement
         * @see team.cappcraft.itemstages.condition.StageMatchCondition
         */
        val recipeCategoryRestriction = mutableMapOf<ICondition<String>, ICondition<EntityPlayer>>()
    }
    
    @SubscribeEvent
    fun onStageSync(evt: StagesSyncedEvent) {
        if (ConfigurationHandler.configuration.hideRestrictionsInJEI)
            syncHiddens(evt.entityPlayer)
    }
    
    override fun register(registry: IModRegistry) {
        modRegistry = registry
    }
    
    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        INSTANCE = this
        this.jeiRuntime = jeiRuntime
        if (SIDE == Side.CLIENT)
            MinecraftForge.EVENT_BUS.register(this)
    }
    
    /**
     * Hide restricted ingredients
     * Hide restricted RecipeCategory
     */
    fun syncHiddens(entityPlayer: EntityPlayer) {
        syncRecipeCategory(entityPlayer)
        syncIngredient(entityPlayer)
    }
    
    private fun syncIngredient(entityPlayer: EntityPlayer) {
        Minecraft.getMinecraft().addScheduledTask {
            with(modRegistry.ingredientRegistry) {
                //Un-hide old values
                hiddenIngredients.groupBy(::getIngredientType).forEach { (type, items) ->
                    addIngredientsAtRuntime(type, items)
                }
                hiddenIngredients.clear()
                GlobalScope.launch {
                    //Generate new hidden ingredient list
                    registeredIngredientTypes.forEach {
                        getAllIngredients(it).forEach { ingredient ->
                            var toMatch: IIngredient? = null
                            when (ingredient) {
                                is ItemStack -> toMatch = MCItemStack(ingredient)
                                is FluidStack -> toMatch = MCLiquidStack(ingredient)
                            }
                            toMatch?.let {
                                ItemStages.mergedRestriction.any { (isRestrictTarget, needToRestrict) ->
                                    EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                        hiddenIngredients.add(ingredient)
                                    }.doRestrictAction(toMatch, entityPlayer)
                                }
                            }
                        }
                    }
                    Minecraft.getMinecraft().addScheduledTask {
                        //Hide elements
                        hiddenIngredients.groupBy(::getIngredientType).forEach { (type, items) ->
                            removeIngredientsAtRuntime(type, items)
                        }
                    }
                }
            }
        }
    }
    
    private fun syncRecipeCategory(entityPlayer: EntityPlayer) {
        Minecraft.getMinecraft().addScheduledTask {
            ItemStages.LOG.debug("Start syncing RecipeCategory")
            //Un-hide old values
            hiddenRecipeCategories.forEach(jeiRuntime.recipeRegistry::unhideRecipeCategory)
            
            GlobalScope.launch {
                //Search for hidden category
                jeiRuntime.recipeRegistry.recipeCategories.forEach {
                    recipeCategoryRestriction.any { (isRestrictTarget, needToRestrict) ->
                        EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                            hiddenRecipeCategories.add(it.uid)
                        }.doRestrictAction(it.uid, entityPlayer)
                    }
                }
                hiddenRecipeCategories.forEach { ItemStages.LOG.trace("Hiding RecipeCategory:$it") }
                //Hide new values
                Minecraft.getMinecraft().addScheduledTask {
                    hiddenRecipeCategories.forEach(jeiRuntime.recipeRegistry::hideRecipeCategory)
                    ItemStages.LOG.debug("Finished syncing RecipeCategory")
                }
            }
        }
    }
}