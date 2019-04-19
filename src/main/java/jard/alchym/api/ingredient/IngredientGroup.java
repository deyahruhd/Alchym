package jard.alchym.api.ingredient;

import com.google.common.collect.Lists;
import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.api.ingredient.impl.FluidInstanceIngredient;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import java.util.*;

/***
 *  IngredientGroup.java
 *
 *  A generic class for storing a group of {@link FluidInstance}s or {@link ItemStack}s, with functionality implement to check equality between
 *  {@code IngredientGroup}s in the "subset" sense, that is, two {@code IngredientGroup}s A and B are equal if A ⊆ B.
 *
 *  {@code IngredientGroup} does not have a public constructor. Instead, {@code IngredientGroups} are instantiated through use of the
 *  static methods {@code fromIngredients}, {@code fromItemStacks}, {@code fromFluidInstances}, and {@code fromItemEntities}.
 *
 *  @see IngredientGroup#fromIngredients(Ingredient...)
 *  @see IngredientGroup#fromItemStacks(ItemStack...)
 *  @see IngredientGroup#fromFluidInstances(FluidInstance...)
 *  @see IngredientGroup#fromItemEntities(ItemEntity...)
 *
 *  Created by jared at 12:01 AM on May 06, 2018. Yarn'd at 9:48 AM on January 18, 2019.
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
    final Set <Ingredient> contents = new TreeSet<> (ingredientOrdering);

    public IngredientGroup () {
        this.isRecipeGroup = false;
    }

    IngredientGroup (boolean isRecipeGroup, Ingredient... stacks) {
        this.isRecipeGroup = isRecipeGroup;
        this.contents.addAll (Lists.newArrayList (stacks));
    }

    /**
     * Generates an IngredientGroup from a list of {@linkplain Ingredient ingredients}.
     *
     * @param ingredients a varargs argument, representing the list of {@linkplain Ingredient ingredients} the new {@code IngredientGroup} should
     *                    have.
     * @return an {@code IngredientGroup} with the supplied ingredients.
     */
    public static IngredientGroup fromIngredients (Ingredient... ingredients) {
        return new IngredientGroup (false, ingredients);
    }

    /**
     * Generates an IngredientGroup from a list of {@linkplain ItemStack ItemStacks}.
     *
     * @param stacks a varargs argument, representing the list of {@linkplain ItemStack ItemStacks} the new {@code IngredientGroup} should
     *               have.
     * @return an {@code IngredientGroup} with the supplied stacks.
     */
    public static IngredientGroup fromItemStacks (ItemStack... stacks) {
        return fromItemStacks (false, stacks);
    }

    /**
     * Generates an IngredientGroup from a list of {@linkplain FluidInstance FluidInstances}.
     *
     * @param fluids a varargs argument, representing the list of {@linkplain FluidInstance FluidInstances} the new {@code IngredientGroup} should
     *      *        have.
     * @return
     */
    public static IngredientGroup fromFluidInstances (FluidInstance ... fluids) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (FluidInstance fluid : fluids) {
            list.add (new FluidInstanceIngredient (fluid));
        }

        return fromFluidInstances (false, fluids);
    }

    /**
     * Generates an IngredientGroup from a list of {@linkplain ItemEntity ItemEntities}, unwrapping them first to retrieve their inner
     * {@link ItemStack}.
     *
     * @param entities a varargs argument, representing the list of {@linkplain ItemEntity ItemEntities}s the new {@code IngredientGroup} should
     *                 have.
     * @return an {@code IngredientGroup} with the supplied items.
     */
    public static IngredientGroup fromItemEntities (ItemEntity ... entities) {
        ArrayList <ItemStack> list = new ArrayList <> ();
        for (ItemEntity entity : entities) {
            list.add (entity.getStack ());
        }

        return fromItemStacks (list.toArray (new ItemStack [0]));
    }

    static IngredientGroup fromItemStacks (boolean isRecipeGroup, ItemStack... stacks) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (ItemStack item : stacks) {
            list.add (new ItemStackIngredient (item));
        }

        return new IngredientGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }

    static IngredientGroup fromFluidInstances (boolean isRecipeGroup, FluidInstance ... fluids) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (FluidInstance fluid : fluids) {
            list.add (new FluidInstanceIngredient (fluid));
        }

        return new IngredientGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }


    /**
     * Indicates if this {@code IngredientGroup}'s {@code contents} is empty, or if {@code contents} contains only empty ingredients.
     *
     * @return true if this {@code IngredientGroup} is empty
     */
    public boolean isEmpty () {
        if (contents.size () > 0) {
            for (Ingredient e : contents) {
                if (!e.isEmpty ())
                    return false;
            }
        }

        return true;
    }

    /**
     * Returns the number of {@linkplain Ingredient ingredients} in {@code contents}.
     *
     * @return an int representing the size of {@code contents}
     */
    public int getCount () {
        return contents.size ();
    }

    /**
     * Determines if this {@code IngredientGroup} is a subset of the supplied {@code IngredientGroup}.
     *
     * @param rhs the superset {@code IngredientGroup} to compare to
     * @return true if {@code this} ⊆ {@code rhs}
     */
    public boolean subset (IngredientGroup rhs) {
        if (isEmpty () || rhs == null || rhs.isEmpty ()) {

            return false;
        }

        // this ⊆ rhs ⇔ (rhs ∪ this) == rhs
        Set <Ingredient> thisSet = new HashSet <> (contents);
        Set <Ingredient> rhsSet = new HashSet <> (rhs.contents);
        Set <Ingredient> union = new HashSet <> (thisSet);
        union.addAll (rhsSet);

        return union.equals (rhsSet);
    }

    /**
     * Determines if the supplied {@link Ingredient} exists in this {@code IngredientGroup}'s contents.
     *
     * @param t the {@linkplain Ingredient ingredient} to find
     *
     * @return true if item ∈ this IngredientGroup.
     */
    public boolean isInGroup (Ingredient t) {
        return contents.contains (t);
    }

    /**
     * Searches for an {@link Ingredient} in {@code contents} which matches the supplied ingredient.
     *
     * @param t the {@linkplain Ingredient ingredient} to match with
     * @return the {@link Ingredient} that matches {@code t}, or an empty ingredient if not found.
     */
    public Ingredient findMatchingIngredient (Ingredient t) {
        if (isInGroup (t)) {
            for (Ingredient i : contents) {
                if (i.equals (t))
                    return i;
            }
        }

        return t.getDefaultEmpty ();
    }

    /**
     * Unwraps all {@linkplain Ingredient ingredients} contained in the items set into their respective objects, and
     * returns them as an array of {@linkplain Object objects}.
     *
     * @return an array of {@linkplain Object Objects} representing the unwrapped instances
     * @see Ingredient#unwrap()
     */
    public Object[] asArray () {
        Set <Object> ret = new HashSet <> ();

        for (Ingredient wrapper : contents) {
            ret.add (wrapper.instance);
        }

        return ret.toArray (new Object[0]);
    }

    /**
     * Adds an {@link Ingredient} to {@code contents}, or merges it with an existing {@linkplain Ingredient ingredient}
     * that matches it.
     *
     * @param ingredient The {@link Ingredient} to insert.
     * @return ingredient if it was added to {@code contents}, or the {@link Ingredient} it was merged into.
     */
    public Ingredient addIngredient (Ingredient ingredient) {
        if (ingredient.isEmpty ())
            return ingredient;

        for (Ingredient ref : contents) {
            if (ref.instanceMatches (ingredient)) {
                // Merge stack into ref and return it
                ref.mergeExistingStack (ingredient);
                return ref;
            }
        }

        contents.add (ingredient);
        return ingredient;
    }

    void removeIngredient (Ingredient stack) {
        contents.remove (stack);
    }
}
