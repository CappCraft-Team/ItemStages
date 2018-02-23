package net.darkhax.itemstages.compat.crt;

import java.util.StringJoiner;

import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ActionItemStage implements IAction {

    private final IIngredient restricted;
    private final ItemStack[] restrictions;

    public ActionItemStage (IIngredient restricted) {

        this.restricted = restricted;

        if (this.restricted instanceof IItemStack && ((IItemStack) this.restricted).getDamage() == OreDictionary.WILDCARD_VALUE) {

            this.restrictions = StackUtils.getAllItems(CraftTweakerMC.getItemStack(this.restricted).getItem());
        }

        else {

            this.restrictions = CraftTweakerMC.getItemStacks(this.restricted.getItems());
        }
    }

    protected ItemStack[] getRestrictedItems () {

        return this.restrictions;
    }

    private String describeStack (ItemStack stack) {

        return String.format("%s:%d%s", StackUtils.getStackIdentifier(stack), stack.getMetadata(), stack.hasTagCompound() ? stack.getTagCompound().toString() : "");
    }

    protected String describeRestrictedStacks () {

        final StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");

        for (final ItemStack stack : this.getRestrictedItems()) {

            joiner.add(this.describeStack(stack));
        }

        return this.getRestrictedItems().length + " entries: " + joiner.toString();
    }

    protected void validate () {

        if (this.restrictions.length == 0) {

            throw new IllegalArgumentException("No items or blocks found for this entry!");
        }

        for (final ItemStack stack : this.restrictions) {

            if (stack.isEmpty()) {

                throw new IllegalArgumentException("Entry contains an empty/air stack!");
            }
        }
    }
}
