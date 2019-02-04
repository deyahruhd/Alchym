package jard.alchym.api.recipe;

import net.minecraft.nbt.CompoundTag;

/***
 *  Ingredient.java
 *  Generic, abstract class intended to wrap an ItemStack class or equivalent for usage in StackGroups.
 *  This allows for cleaner code that makes use of the existing generic Collection objects, like HashSet.
 *
 *  Created by jard at 8:14 PM on November 18, 2018. Fabric'd at 9:48 AM on January 18, 2019.
 ***/

public abstract class Ingredient <T> {
    // Override these in subclasses which specify the T parameter.
    public abstract int hashCode ();
    public abstract Ingredient<T> getDefaultEmpty ();
    public abstract boolean isEmpty ();
    public abstract int getAmount ();
    public abstract Ingredient<T> trim (long vol);
    abstract boolean instanceMatches (Ingredient other);
    abstract boolean instanceEquals  (Ingredient other);
    abstract void mergeExistingStack (Ingredient<T> in);
    abstract CompoundTag toTag (CompoundTag tag);
    abstract void fromTag (CompoundTag tag);

    T instance;
    Class <T> type;
    // Whether this Ingredient is present in the inputs or outputs IngredientGroup members of a
    // TransmutationRecipe. Used for comparison via the overriden equals operator.
    private boolean isRecipeInstance = false;

    Ingredient (T instance, Class<T> parameterType) {
        this.instance = instance;
        type = parameterType;
    }

    Ingredient (T instance, Class<T> parameterType, IngredientGroup parent) {
        this.instance = instance;
        type = parameterType;
        isRecipeInstance = parent.isRecipeGroup;
    }

    Ingredient (CompoundTag tag, Class<T> parameterType) {
        type = parameterType;
        fromTag (tag);
    }

    @Override
    public final boolean equals (Object rhs) {
        // Preliminary check to make sure we are checking two wrappers of the same type: FluidInstances are not ItemStacks
        if (!(rhs instanceof Ingredient) || !type.isInstance (((Ingredient) rhs).instance))
            return false;

        boolean flag1 = instanceMatches ((Ingredient) rhs);

        // If both this Ingredient and rhs are present in a recipe, we are comparing two
        // recipes together. In this case, neither ItemStack describes an actual entity in the world, and so
        // item counts can be ignored.
        // In the other possible case where this and rhs are not part of a recipe, we simply return true.
        // We are comparing two Ingredients that exist in the world, and so we do not need to check
        // for subsets.
        if (flag1 && isRecipeInstance == ((Ingredient) rhs).isRecipeInstance)
            return true;

        // Otherwise, compare amounts.
        int thisCount = getAmount ();
        int rhsCount = ((Ingredient) rhs).getAmount ();

        return flag1 &&
                // If this item is part of a transmutation recipe, we ALWAYS check if rhs has as many items as this, as
                // we are trying to determine if a supplied ItemStack meets or exceeds the required count of items of the recipe.
                // Otherwise, perform normal comparison (rhs can be "contained" in this ItemStackIngredient)
                (isRecipeInstance ? rhsCount >= thisCount : rhsCount <= thisCount);
    }

    boolean isISoluble () {
        return unwrapSpecies () instanceof ISoluble && ((ISoluble) unwrapSpecies ()).getMaterial () != null;
    }

    // Unwraps this Ingredient, yielding the instance of the
    public final T unwrap () {
        return instance;
    }
    // Returns the inner Item/Fluid of the instance.
    public abstract Object unwrapSpecies ();
}
