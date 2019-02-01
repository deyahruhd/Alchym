package jard.alchym.api.recipe;

import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/***
 *  ItemStackIngredient.java
 *  TODO: Add a description for this file.
 *
 *  Created by jard at 1:55 AM on November 19, 2018.
 ***/

public class ItemStackIngredient extends Ingredient<ItemStack> {
    public ItemStackIngredient (ItemStack instance) {
        super (instance, ItemStack.class);
    }

    public ItemStackIngredient (ItemStack instance, IngredientGroup parent) {
        super (instance, ItemStack.class, parent);
    }

    public ItemStackIngredient (CompoundTag tag, Class<ItemStack> parameterType) {
        super (tag, parameterType);
    }

    @Override
    protected int getAmount () {
        return instance.getAmount ();
    }

    @Override
    public int hashCode () {
        return instance.getItem ().hashCode ();
    }

    @Override
    protected boolean areInstancesEqual (ItemStack lhs, ItemStack rhs) {
        return ItemStack.areEqualIgnoreDurability (lhs, rhs);
    }

    @Override
    protected boolean isEmpty () {
        return instance.isEmpty ();
    }

    @Override
    protected void mergeExistingStack (Ingredient<ItemStack> in) {
        instance.addAmount (in.getAmount ());
    }

    @Override
    protected CompoundTag toTag (CompoundTag tag) {
        tag.put ("InnerItemStack", instance.toTag (new CompoundTag ()));

        return tag;
    }

    @Override
    protected void fromTag (CompoundTag tag) {
        if (tag == null || ! tag.containsKey ("InnerItemStack"))
            return;

        this.instance = ItemStack.fromTag (tag.getCompound ("InnerItemStack"));
    }
}
