package jard.alchym.api.recipe;

import com.google.common.collect.Lists;
import io.github.prospector.silk.fluid.FluidInstance;
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
    final Set<Ingredient> stacks = new HashSet<> ();

    public IngredientGroup () {
        this.isRecipeGroup = false;
    }

    IngredientGroup (boolean isRecipeGroup, Ingredient... stacks) {
        this.isRecipeGroup = isRecipeGroup;
        this.stacks.addAll (Lists.newArrayList (stacks));
    }

    public static IngredientGroup fromItemStacks (ItemStack... stacks) {
        return fromItemStacks (false, stacks);
    }

    public static IngredientGroup fromItemStacks (boolean isRecipeGroup, ItemStack... stacks) {
        ArrayList<Ingredient> list = new ArrayList <> ();
        for (ItemStack item : stacks) {
            list.add (new ItemStackIngredient (item));
        }

        return new IngredientGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }

    public static IngredientGroup fromEntityItems (ItemEntity ... entities) {
        ArrayList<Ingredient> list = new ArrayList <> ();
        for (ItemEntity entity : entities) {
            list.add (new ItemStackIngredient (entity.getStack ()));
        }

        return new IngredientGroup (false, list.toArray (new Ingredient[]{}));
    }

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

        Set<Ingredient> thisSet = new HashSet<> (stacks);
        Set<Ingredient> rhsSet = new HashSet<> (rhs.stacks);
        Set<Ingredient> union = new HashSet<> (thisSet);
        union.addAll (rhsSet);

        return union.equals (rhsSet);
    }

    // Returns true if item ∈ this IngredientGroup.
    public boolean isInGroup (Ingredient t) {
        return stacks.contains (t);
    }

    public int getStackCount (Ingredient t) {
        if (!isInGroup (t))
            return 0;

        for (Ingredient i : stacks) {
            if (i.equals (t))
                return i.getAmount ();
        }

        return 0;
    }

    // Unwraps all Ingredients contained in the items set into their respective objects, and returns them as
    // an array of objects.
    public Object[] asArray () {
        Set<Object> ret = new HashSet<> ();

        for (Ingredient wrapper : stacks) {
            ret.add (wrapper.instance);
        }

        return ret.toArray (new ItemStack[0]);
    }

    // Attempts to reconstruct the corresponding Ingredient from the tag. Returns the Ingredient if successful, or null
    // otherwise.

    private Ingredient attemptDeserialize  (CompoundTag tag) {

        return null;
    }

    // Writes this IngredientGroup to a compound tag.
    public CompoundTag toTag (CompoundTag tag) {
        ListTag serializedIngs = new ListTag ();


        stacks.forEach (ingredient -> {
            serializedIngs.add (ingredient.toTag (new CompoundTag ()));
        });

        tag.put ("Ingredients", serializedIngs);

        return tag;
    }

    // Initializes this IngredientGroup with the given CompoundTag.
    public void fromTag (CompoundTag tag) {
        if (! isEmpty () || tag == null || ! tag.containsKey ("Ingredients"))
            return;

        ListTag serializedIngs = tag.getList ( "Ingredients", 0);

        serializedIngs.forEach (ingredientTag -> {
            Ingredient ingredient;

        });
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

    public boolean hasSolid () {
        for (Ingredient i : stacks) {
            if (i.instance instanceof ItemStack)
                return true;
        }

        return false;
    }

    public boolean hasLiquid () {
        for (Ingredient i : stacks) {
            if (i.instance instanceof FluidInstance)
                return true;
        }

        return false;
    }

    public DefaultedList <ItemStack> getDroppableStacks () {
        DefaultedList <ItemStack> drop = DefaultedList.create ();
        if (this.hasLiquid ())
            return drop;

        for (Ingredient ingredient : stacks) {
            drop.add ((ItemStack) ingredient.instance);
        }

        return drop;
    }
}
