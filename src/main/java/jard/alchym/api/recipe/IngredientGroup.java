package jard.alchym.api.recipe;

import com.google.common.collect.Lists;
import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.items.ISoluble;
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
        Set <Object> ret = new HashSet <> ();

        for (Ingredient wrapper : stacks) {
            ret.add (wrapper.instance);
        }

        return ret.toArray (new ItemStack[0]);
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

    /** End section for transmutation-specific implementation details of IngredientGroup **/



    /** Begin solution-specific implementation details **/

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
            Ingredient ingredient = attemptDeserialize ((CompoundTag) ingredientTag);

            if (ingredient != null) {
                addStack (ingredient);
            }
        });
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

    // Returns the largest element of the stacks TreeSet. In most applications, this is the solvent of the
    // solution this IngredientGroup describes.

    public Ingredient getLargest () {
        return Collections.max (stacks, ingredientOrdering);
    }

    public long getVolume () {
        return getLiquidVolume () + getSolidVolume ();
    }

    private long getLiquidVolume () {
        long sum = 0;

        if (hasLiquid ()) {
            for (Ingredient ing : stacks) {
                if (ing.instance instanceof FluidInstance)
                    sum += ing.getAmount ();
            }
        }

        return sum;
    }

    private long getSolidVolume () {
        long sum = 0;

        // For simplicity's sake, it is assumed that any arbitrary amount of ISolubles will not affect the volume of an IngredientGroup
        // which contains a FluidInstanceIngredient. Instead we determine the volume of any insoluble items, which will in practice
        // be sorted out into their own IngredientGroup by a GlassContainerBlockEntity.

        // It's also assumed that all ItemStackIngredients represent Items which implement ISolubles. In the case that there is an ItemStackIngredient
        // which doesn't follow this assumption, we just ignore it
        if (! hasLiquid () && hasSolid ()) {
            for (Ingredient ing : stacks) {
                ItemStack stack = ((ItemStack) ing.instance);
                if (stack.getItem () instanceof ISoluble)
                    sum += ((ISoluble) stack.getItem ()).getVolume () * ing.getAmount ();
            }
        }

        return sum;
    }
}
