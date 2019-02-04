package jard.alchym.api.recipe;

import com.google.common.collect.Lists;
import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.AlchymReference;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;

import java.util.*;

/***
 *  IngredientGroup.java
 *  A generic class for storing a group of FluidInstances or ItemStacks, with functionality implement to check equality between
 *  IngredientGroup in the "subset" sense, that is, two StackGroups A and B are equal if A ⊆ B.
 *
 *  Created by jared at 12:01 AM on May 06, 2018. Yarn'd at 9:48 AM on January 18, 2019.
 *
 ***/
public class IngredientGroup {
    final boolean isRecipeGroup;
    final Comparator <Ingredient> ingredientOrdering =
            // Comparator sorts Ingredients by the following rules:
            //    - FluidInstances are always greater than ItemStacks
            //    - Higher amounts are always greater than lower amounts
            (o1, o2) -> {
                if (o1.equals (o2))
                    return 0;
                else {
                    if (o1.type == o2.type) {
                        int initialCompare = Integer.compare (o2.getAmount (), o1.getAmount ());

                        return initialCompare != 0 ? initialCompare :
                                // Need some way to differentiate between two Ingredients with differing instances but
                                // the same stack count - in this case, it does not matter what order they fall under.
                                // Just use their hash codes as the deciding factor
                                Integer.compare (o1.hashCode (), o2.hashCode ());
                    } else if (o1.type == FluidInstance.class)
                        return 1;
                    else
                        return - 1;
                }
            };
    final Set <Ingredient> stacks = new TreeSet<> (ingredientOrdering);

    public IngredientGroup () {
        this.isRecipeGroup = false;
    }

    IngredientGroup (boolean isRecipeGroup, Ingredient... stacks) {
        this.isRecipeGroup = isRecipeGroup;
        this.stacks.addAll (Lists.newArrayList (stacks));
    }

    public static IngredientGroup fromIngredients (Ingredient... ingredients) {
        return new IngredientGroup (false, ingredients);
    }

    public static IngredientGroup fromItemStacks (ItemStack... stacks) {
        return fromItemStacks (false, stacks);
    }

    public static IngredientGroup fromItemStacks (boolean isRecipeGroup, ItemStack... stacks) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (ItemStack item : stacks) {
            list.add (new ItemStackIngredient (item));
        }

        return new IngredientGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }

    public static IngredientGroup fromFluidInstances (boolean isRecipeGroup, FluidInstance ... fluids) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (FluidInstance fluid : fluids) {
            list.add (new FluidInstanceIngredient (fluid));
        }

        return new IngredientGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }

    public static IngredientGroup fromItemEntities (ItemEntity ... entities) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (ItemEntity entity : entities) {
            list.add (new ItemStackIngredient (entity.getStack ()));
        }

        return new IngredientGroup (false, list.toArray (new Ingredient[]{}));
    }





    /** Begin transmutation-specific implementation details **/

    public boolean isEmpty () {
        if (stacks.size () > 0) {
            for (Ingredient e : stacks) {
                if (!e.isEmpty ())
                    return false;
            }
        }

        return true;
    }

    public int getCount () {
        return stacks.size ();
    }

    public boolean matches (IngredientGroup rhs) {
        // The implementation of this method essentially boils down to "is this IngredientGroup a subset of rhs", based
        // on the fact that this ⊆ rhs ⇔ (rhs ∪ this) == rhs.

        // The reason I chose this implementation is that, in the context of a transmutation in the mod, two groups of items matching
        // doesn't entail that they are the exact same group of items. rhs may contain more items, but because every item in this group
        // is also contained in rhs, the rhs IngredientGroup is valid to use as a transmutation, and it will be transformed into
        // the items that are not consumed by a transmutation recipe.
        if (isEmpty () || rhs == null || rhs.isEmpty ())
            return false;

        Set <Ingredient> thisSet = new HashSet <> (stacks);
        Set <Ingredient> rhsSet = new HashSet <> (rhs.stacks);
        Set <Ingredient> union = new HashSet <> (thisSet);
        union.addAll (rhsSet);

        return union.equals (rhsSet);
    }

    // Returns true if item ∈ this IngredientGroup.
    public boolean isInGroup (Ingredient t) {
        return stacks.contains (t);
    }

    public Ingredient findMatchingIngredient (Ingredient t) {
        if (isInGroup (t)) {
            for (Ingredient i : stacks) {
                if (i.equals (t))
                    return i;
            }
        }

        return t.getDefaultEmpty ();
    }

    // Unwraps all Ingredients contained in the items set into their respective objects, and returns them as
    // an array of objects.
    public Object[] asArray () {
        Set <Object> ret = new HashSet <> ();

        for (Ingredient wrapper : stacks) {
            ret.add (wrapper.instance);
        }

        return ret.toArray (new Object[0]);
    }

    public Ingredient addStack (Ingredient stack) {
        if (stack.isEmpty ())
            return stack;

        for (Ingredient ref : stacks) {
            if (ref.equals (stack)) {
                // Merge stack into ref and return it
                ref.mergeExistingStack (stack);
                return ref;
            }
        }

        stacks.add (stack);
        return stack;
    }

    void removeStack (Ingredient stack) {
        stacks.remove (stack);
    }
}
