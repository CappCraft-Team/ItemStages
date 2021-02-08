package team.cappcraft.itemstages

import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import crafttweaker.mc1120.item.MCItemStack
import net.darkhax.bookshelf.lib.LoggingHelper
import net.darkhax.gamestages.GameStageHelper
import net.darkhax.itemstages.ConfigurationHandler.*
import net.darkhax.itemstages.ConfigurationHandler.Configuration.*
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import team.cappcraft.itemstages.ItemStages.MOD_ID
import team.cappcraft.itemstages.ItemStages.MOD_NAME
import team.cappcraft.itemstages.ItemStages.MOD_VERSION
import team.cappcraft.itemstages.condition.AbstractConditionGroup
import team.cappcraft.itemstages.condition.ICondition
import team.cappcraft.itemstages.condition.StageMatchCondition.StageCondition
import team.cappcraft.itemstages.condition.action.EntityPlayerRestrictAction
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.SIDE

@Mod(
    modid = MOD_ID,
    name = MOD_NAME,
    version = MOD_VERSION,
    dependencies = "after:jei@[4.14.4.267,);required-after:bookshelf;required-after:gamestages@[2.0.114,);required-after:crafttweaker;required-after:kotlinforforge",
    modLanguageAdapter = "thedarkcolour.kotlinforforge.KotlinLanguageAdapter",
    guiFactory = "team.cappcraft.itemstages.GuiFactory"
)
object ItemStages {
    const val MOD_ID = "itemstages"
    const val MOD_NAME = "ItemStages"
    const val MOD_VERSION = "@version@"
    
    const val ITEMNAME_DEFAULT = "tooltip.itemstages.name.default"
    const val TOOLTIP_DESC = "tooltip.itemstages.description"
    const val TOOLTIP_ENCHANT = "tooltip.itemstages.enchant"
    const val TOOLTIP_STAGE = "tooltip.itemstages.stage"
    const val MESSAGE_DROP = "message.itemstages.drop"
    const val MESSAGE_ATTACK = "message.itemstages.attack"
    
    val LOG = LoggingHelper("Item Stages")
    
    /**
     * Contain restrict targets and the restrict requirement
     * Key -> restrict target
     * @see team.cappcraft.itemstages.condition.EnchantmentMatchCondition
     *
     * Value -> restrict requirement
     * @see team.cappcraft.itemstages.condition.StageMatchCondition
     */
    val enchantmentRestriction = mutableMapOf<ICondition<IIngredient>, ICondition<EntityPlayer>>()
    
    /**
     * Contain restrict targets and the restrict requirement
     * Key -> restrict target
     * @see team.cappcraft.itemstages.condition.IIngredientMatchCondition
     *
     * Value -> restrict requirement
     * @see team.cappcraft.itemstages.condition.StageMatchCondition
     */
    val ingredientRestriction = mutableMapOf<ICondition<IIngredient>, ICondition<EntityPlayer>>()
    
    /**
     * Merged above two restriction map
     */
    val mergedRestriction
        get() = enchantmentRestriction.toList() + ingredientRestriction.toList()
    
    /**
     * Contain restrict targets and the restrict requirement
     * Key -> restrict target
     * @see team.cappcraft.itemstages.condition.ToolTipMatchCondition
     *
     * Value -> restrict requirement
     * @see team.cappcraft.itemstages.condition.StageMatchCondition
     */
    val toolTipRestriction = mutableMapOf<ICondition<String>, ICondition<EntityPlayer>>()
    
    /**
     * Contain custom restricted item's name
     * Key -> restrict target
     * Value -> custom name
     */
    val customItemName = mutableMapOf<ICondition<IItemStack>, String>()
    
    @Mod.EventHandler
    fun onPreInitialization(evt: FMLPreInitializationEvent) {
        ConfigManager.sync("itemstages", Config.Type.INSTANCE)
        
        with(FORGE_BUS) {
            addListener<ConfigChangedEvent.OnConfigChangedEvent> {
                if (it.modID.equals("itemstages", ignoreCase = true)) {
                    ConfigManager.sync("itemstages", Config.Type.INSTANCE)
                }
            }
            /**
             * Prevent player breaking a block by a restricted item
             * disable when [Configuration.allowInteractRestricted] is true
             */
            addListener<PlayerEvent.BreakSpeed> {
                if (!configuration.allowInteractRestricted
                    && !it.entityPlayer.isCreative
                    && !it.entityPlayer.heldItemMainhand.isEmpty
                ) {
                    with(MCItemStack(it.entityPlayer.heldItemMainhand)) {
                        mergedRestriction.any { (isRestrictTarget, needToRestrict) ->
                            EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                it.newSpeed = -1f
                                it.isCanceled = true
                            }.doRestrictAction(this, it.entityPlayer)
                        }
                    }
                }
            }
            /**
             * Prevent player damaging entity by a restricted item
             * disable when [Configuration.allowInteractRestricted] is true
             */
            addListener<AttackEntityEvent> {
                if (!configuration.allowInteractRestricted
                    && !it.entityPlayer.isCreative
                    && !it.entityPlayer.heldItemMainhand.isEmpty
                ) {
                    with(MCItemStack(it.entityPlayer.heldItemMainhand)) {
                        mergedRestriction.any { (isRestrictTarget, needToRestrict) ->
                            EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                it.entityPlayer.sendActionBarMsg(
                                    MESSAGE_ATTACK,
                                    "${TextFormatting.GOLD}${TextFormatting.BOLD}$displayName"
                                )
                                it.isCanceled = true
                            }.doRestrictAction(this, it.entityPlayer)
                        }
                    }
                }
            }
            /**
             * Prevent player interact with a block by a restricted item
             * Such as: left click or right click on a block
             * disable when [Configuration.allowInteractRestricted] is true
             */
            addListener<PlayerInteractEvent> {
                if (it.isCancelable
                    && !configuration.allowInteractRestricted
                    && !it.entityPlayer.isCreative
                    && !it.entityPlayer.heldItemMainhand.isEmpty
                ) {
                    with(MCItemStack(it.entityPlayer.heldItemMainhand)) {
                        mergedRestriction.any { (isRestrictTarget, needToRestrict) ->
                            EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                it.isCanceled = true
                            }.doRestrictAction(this, it.entityPlayer)
                        }
                    }
                }
            }
            
            /**
             * Drop the equipped/holding restricted item
             * set [Configuration.allowEquipRestricted] to true to allow equipping
             * set [Configuration.allowHoldingRestricted] to true to allow holding
             * set [Configuration.allowHoldingRestrictedEnchant] to true to allow holing the item which is
             * enchanted a restricted enchantment
             */
            addListener<LivingEquipmentChangeEvent> {
                if (configuration.allowEquipRestricted && it.slot.slotType == EntityEquipmentSlot.Type.ARMOR)
                    return@addListener //Skip if allow equip
                
                (it.entityLiving as? EntityPlayer)?.let { entityPlayer ->
                    if (!entityPlayer.isCreative && !it.to.isEmpty) {
                        val holding = it.slot.slotType == EntityEquipmentSlot.Type.HAND
                        val restriction =
                            if (!holding) mergedRestriction
                            else {
                                (if (!configuration.allowHoldingRestricted) ingredientRestriction.toList() else emptyList()) +
                                        (if (!configuration.allowHoldingRestrictedEnchant) enchantmentRestriction.toList() else emptyList())
                            }
                        
                        with(MCItemStack(it.to)) {
                            restriction.any { (isRestrictTarget, needToRestrict) ->
                                EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                    entityPlayer.dropSlot(it.slot)
                                }.doRestrictAction(this, entityPlayer)
                            }
                        }
                    }
                }
            }
            /**
             * Hide tooltip for a restricted item
             * Hide the Staged tooltip
             * set [Configuration.changeRestrictionTooltip] to false to disable
             *
             * this is a [Side.CLIENT] handler
             */
            if (SIDE == Side.CLIENT) {
                addListener<ItemTooltipEvent>(EventPriority.LOWEST) {
                    if (!configuration.changeRestrictionTooltip) return@addListener
                    if (it.itemStack.isEmpty) return@addListener //We dont handle empty stack
                    
                    it.entityPlayer?.let { entityPlayer ->
                        with(MCItemStack(it.itemStack)) {
                            if (!ingredientRestriction.any { (isRestrictTarget, needToRestrict) ->
                                    EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                        with(it.toolTip) {
                                            replaceWithRestrictTooltip(it)
                                            addStageRequirement(needToRestrict, entityPlayer)
                                        }
                                    }.doRestrictAction(this, entityPlayer)
                                }) {
                                //Not a Restricted item, handle enchantment
                                enchantmentRestriction.any { (isRestrictTarget, needToRestrict) ->
                                    EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                        with(it.toolTip) {
                                            addEnchantRestrictTooltip(it)
                                            addStageRequirement(needToRestrict, entityPlayer)
                                        }
                                    }.doRestrictAction(this, entityPlayer)
                                }
                                //Remove Restricted tooltip
                                it.toolTip.iterator().apply {
                                    while (hasNext()) {
                                        val text = next()
                                        toolTipRestriction.forEach { (isRestrictTarget, needToRestrict) ->
                                            EntityPlayerRestrictAction(isRestrictTarget, needToRestrict) {
                                                remove()
                                            }.doRestrictAction(text, entityPlayer)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * tell the enchant is being restricted
     * Example:
     *
     * You cannot use *ItemName* without first understanding the enchantment
     */
    private fun MutableList<String>.addEnchantRestrictTooltip(it: ItemTooltipEvent) {
        add(" ")
        add(I18n.format(TOOLTIP_ENCHANT, it.itemStack.displayName))
    }
    
    /**
     * replace the original tooltip with restricted tooltip
     *
     * Example:
     * Unfamiliar Item
     *
     * You do not know how to use this yet.
     * Stage Requite:
     * Stage_1
     * Stage_2
     * <Shift...>
     */
    private fun MutableList<String>.replaceWithRestrictTooltip(it: ItemTooltipEvent) {
        clear()
        add("${TextFormatting.WHITE}${TextFormatting.BOLD}${it.itemStack.getUnfamiliarName()}")
        add(" ")
        add(I18n.format(TOOLTIP_DESC))
    }
    
    /**
     * Add Stage requirement information at the end of the tooltip
     * if stages' size >= 3, will show only two stage unless **Shift** key is down
     *
     * Example:
     * Stage Requite:
     * Stage_1
     * Stage_2
     * <Shift...>
     */
    @Suppress("UNCHECKED_CAST")
    @SideOnly(Side.CLIENT)
    private fun MutableList<String>.addStageRequirement(
        needToRestrict: ICondition<EntityPlayer>,
        entityPlayer: EntityPlayer
    ) {
        val stageConditions = mutableSetOf<StageCondition>()
        when (needToRestrict) {
            is AbstractConditionGroup -> stageConditions.addAll(needToRestrict.filter {
                it.value && it.key is StageCondition //Filter StageCondition that do required
            }.keys as Collection<StageCondition>)
            is StageCondition -> stageConditions.add(needToRestrict)
        }
        
        stageConditions.apply {
            if (isEmpty()) return
            //Stage Require:
            add(I18n.format(TOOLTIP_STAGE))
            //Stages
            forEach {
                it.pattern.filter { stage ->
                    !GameStageHelper.hasStage(entityPlayer, stage)
                }.apply {
                    if (size >= 3 && GuiScreen.isShiftKeyDown()) //Hide if Stages exceed 2
                        subList(0, 2).apply { add("${TextFormatting.GRAY}<Shift...>") }.forEach(::add)
                    else
                        forEach(::add)
                }
            }
        }
    }
    
    /**
     * Get the restricted item's name
     * can be custom by...
     */
    @SideOnly(Side.CLIENT)
    private fun ItemStack.getUnfamiliarName(): String {
        return customItemName.keys.find { it.match(MCItemStack(this)) }.let {
            customItemName[it]
        } ?: I18n.format(ITEMNAME_DEFAULT)
    }
    
    /**
     * Drop the item in the slot and send a message
     *
     * Example:
     * You dropped the dirt.
     */
    private fun EntityPlayer.dropSlot(slot: EntityEquipmentSlot) {
        with(getItemStackFromSlot(slot)) {
            dropItem(this, false, false)
            setItemStackToSlot(slot, ItemStack.EMPTY)
            sendActionBarMsg(
                MESSAGE_DROP,
                "${TextFormatting.GOLD}${TextFormatting.BOLD}$displayName"
            )
        }
    }
    
    /**
     * Send translated actionbar message
     * @see net.minecraft.entity.player.EntityPlayerMP.sendStatusMessage
     */
    private fun EntityPlayer.sendActionBarMsg(translationKey: String, vararg args: Any) {
        sendStatusMessage(TextComponentTranslation(translationKey, *args), true)
    }
}
