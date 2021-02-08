package net.darkhax.itemstages;

import net.minecraftforge.common.config.Config;

@Config(modid = "itemstages")
public class ConfigurationHandler {
    public static Configuration configuration = new Configuration();

    public static class Configuration {
        @Config.Name("Allow Holding Restricted")
        @Config.Comment("Should players be allowed to hold items that are restricted to them.")
        public boolean allowHoldingRestricted = false;

        @Config.Name("Allow Holding Restricted Enchant")
        @Config.Comment("Should players be allowed to hold items that have an enchantment they is restricted to them.")
        public boolean allowHoldingRestrictedEnchant = false;

        @Config.Name("Allow Equip Restricted")
        @Config.Comment("Should players be allowed to equip items that are restricted to them.")
        public boolean allowEquipRestricted = false;

        @Config.Name("Allow Interact With Restricted")
        @Config.Comment("Should players be allowed to interact (left/right click) with items that are restricted to them.")
        public boolean allowInteractRestricted = false;

        @Config.Name("Change Restriction ToolTip")
        @Config.Comment("Should restricted items have their tooltips changed?")
        public boolean changeRestrictionTooltip = true;

        @Config.Name("Hide Restrictions In JEI")
        @Config.Comment("Should restricted items be hidden in JEI?")
        public boolean hideRestrictionsInJEI = true;
    }
}