package team.cappcraft.itemstages

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.GuiConfig

class GuiFactory : IModGuiFactory {
    override fun initialize(minecraftInstance: Minecraft?) {
    }
    
    override fun hasConfigGui(): Boolean {
        return true
    }
    
    override fun createConfigGui(parentScreen: GuiScreen?): GuiScreen {
        return GuiConfig(parentScreen, "itemstages", "Item Stages Config")
    }
    
    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement> {
        return mutableSetOf()
    }
}