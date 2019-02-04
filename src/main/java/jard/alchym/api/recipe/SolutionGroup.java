package jard.alchym.api.recipe;

import com.google.common.collect.Lists;
import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.AlchymReference;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.*;

/***
 *  SolutionGroup.java
 *  A generic specialized implementation of IngredientGroup which handles grouping solvents and solutes into solutions.
 *
 *  Created by jard at 8:38 PM on February 03, 2019.
 ***/
public class SolutionGroup extends IngredientGroup {
    public SolutionGroup () {
        super ();
    }

    SolutionGroup (boolean isRecipeGroup, Ingredient... stacks) {
        super (isRecipeGroup, stacks);
    }

    public static SolutionGroup fromIngredients (Ingredient... ingredients) {
        return new SolutionGroup (false, ingredients);
    }

    public static SolutionGroup fromItemStacks (ItemStack... stacks) {
        return fromItemStacks (false, stacks);
    }

    public static SolutionGroup fromItemStacks (boolean isRecipeGroup, ItemStack... stacks) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (ItemStack item : stacks) {
            list.add (new ItemStackIngredient (item));
        }

        return new SolutionGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }

    public static SolutionGroup fromFluidInstances (boolean isRecipeGroup, FluidInstance ... fluids) {
        ArrayList <Ingredient> list = new ArrayList <> ();
        for (FluidInstance fluid : fluids) {
            list.add (new FluidInstanceIngredient (fluid));
        }

        return new SolutionGroup (isRecipeGroup, list.toArray (new Ingredient[]{}));
    }

    // Attempts to reconstruct the corresponding Ingredient from the tag. Returns the Ingredient if successful, or null
    // otherwise.
    private Ingredient attemptDeserialize  (CompoundTag tag) {
        // TODO: Find an elegant way to do this
        if (tag.containsKey ("InnerFluidInstance"))  return new FluidInstanceIngredient (tag, FluidInstance.class);
        else if (tag.containsKey ("InnerItemStack")) return new ItemStackIngredient     (tag, ItemStack.class);

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
    public boolean fromTag (CompoundTag tag) {
        if (! isEmpty () || tag == null || ! tag.containsKey ("Ingredients"))
            return true;

        ListTag serializedIngs = (ListTag) tag.getTag ("Ingredients");

        serializedIngs.forEach (ingredientTag -> {
            Ingredient ingredient = attemptDeserialize ((CompoundTag) ingredientTag);

            if (ingredient != null) {
                addStack (ingredient);
            }
        });

        return true;
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

    public DefaultedList<ItemStack> getDroppableStacks () {
        DefaultedList <ItemStack> drop = DefaultedList.create ();
        if (this.hasLiquid ())
            return drop;

        for (Ingredient ingredient : stacks) {
            drop.add ((ItemStack) ingredient.instance);
        }

        return drop;
    }

    public boolean mergeSolvent (FluidInstanceIngredient ingredient) {
        if (ingredient instanceof FluidInstanceIngredient && hasLiquid () && getLargest ().instanceMatches (ingredient)) {
            getLargest ().mergeExistingStack (ingredient);
            return true;
        }

        return false;
    }

    public boolean isSolubleIn (Ingredient ingredient) {
        if (ingredient.isISoluble () && hasLiquid ()) {
            FluidInstanceIngredient solvent = (FluidInstanceIngredient) getLargest ();
            if (solvent.instanceMatches (ingredient))
                return true;

            long solubility = AlchymReference.FluidSolubilities.getSolubility (solvent.instance.getFluid (),
                    (ISoluble) (ingredient.unwrapSpecies ()));

            return solubility > 0 || solubility == -1;
        }

        return false;
    }

    public Ingredient addSoluble (Ingredient ingredient) {
        if (hasLiquid () && ingredient.isISoluble ()) {
            FluidInstanceIngredient solvent = (FluidInstanceIngredient) getLargest ();
            long solubility = AlchymReference.FluidSolubilities.getSolubility (solvent.instance.getFluid (), ((ISoluble) ingredient.unwrapSpecies ()));

            if (solubility > 0) {
                Ingredient target = null;

                for (Ingredient ref : stacks) {
                    if (ref.instanceMatches (ingredient)) {
                        // Merge stack into ref, and re-add the excess as another soluble part
                        ref.mergeExistingStack (ingredient);

                        target = ref;
                        break;
                    }
                }

                // If target is still null, no matching ingredient was found, so just add it to the stacks and set it as the target
                if (target == null) {
                    target = ingredient;
                    addStack (target);
                }

                // Calculate if the target stack needs to be trimmed off to match solubility rules
                long maxDissolvedVol   = (long) ((float) solvent.getAmount () / 1000.f * solubility);
                long totalDissolvedVol = target.getAmount () * ((ISoluble) target.unwrapSpecies ()).getVolume ();

                System.out.println (totalDissolvedVol + "/" + maxDissolvedVol);

                if (totalDissolvedVol > maxDissolvedVol) {
                    Ingredient trimmed = target.trim (totalDissolvedVol - maxDissolvedVol);
                    System.out.println ("Trimming, resulting in an ingredient with amount " + trimmed.getAmount ());
                    return trimmed;
                } else
                    return ingredient.getDefaultEmpty ();
            } else if (solubility == -1) {
                addStack (ingredient);

                return ingredient.getDefaultEmpty ();
            }
        }

        return ingredient;
    }

    // Returns the largest element of the stacks TreeSet. In most applications, this is the solvent of the
    // solution this IngredientGroup describes.
    public Ingredient getLargest () {
        return Collections.max (stacks, ingredientOrdering);
    }

    public List <Pair <Ingredient, Integer>> getSolubles (Fluid fluid) {
        if (isEmpty ())
            return null;

        List <Pair <Ingredient, Integer>> solutes = new ArrayList <> ();

        for (Ingredient ing : stacks) {
            if (ing.isISoluble ()) {
                int solubility = AlchymReference.FluidSolubilities.getSolubility (fluid, (ISoluble) ing.unwrapSpecies ());
                if (solubility > 0)
                    solutes.add (Pair.of (ing, solubility));
            }
        }

        return solutes;
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
