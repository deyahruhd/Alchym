package jard.alchym.api.ingredient;

import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.AlchymReference;
import jard.alchym.api.ingredient.impl.FluidInstanceIngredient;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
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
 *  A specialized implementation of {@link IngredientGroup} which groups solvents and solutes into solutions.
 *
 *  {@code SolutionGroup} does not have a public constructor. Instead, {@code SolutionGroups} are instantiated through use of the
 *  static method {@code fromIngredients}.
 *
 *  @see SolutionGroup#fromIngredients(Ingredient[])
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

    /**
     * Attempts to deserialize a {@linkplain CompoundTag tag} into its respective {@link Ingredient}.
     *
     * @param tag the {@link CompoundTag} to use for deserialization
     * @return the {@link Ingredient} if successful, or null otherwise
     */
    private Ingredient attemptDeserialize  (CompoundTag tag) {
        // TODO: Find an elegant way to do this
        if (tag.containsKey ("InnerFluidInstance"))  return new FluidInstanceIngredient (tag, FluidInstance.class);
        else if (tag.containsKey ("InnerItemStack")) return new ItemStackIngredient (tag, ItemStack.class);

        return null;
    }

    /**
     * Serializes this {@code SolutionGroup} into the supplied {@linkplain CompoundTag tag}.
     *
     * @param tag the {@link CompoundTag} to use when serializing
     * @return the resulting {@link CompoundTag}
     */
    public CompoundTag toTag (CompoundTag tag) {
        ListTag serializedIngs = new ListTag ();

        contents.forEach (ingredient -> {
            serializedIngs.add (ingredient.toTag (new CompoundTag ()));
        });

        tag.put ("Ingredients", serializedIngs);

        return tag;
    }

    /**
     * Deserializes the supplied {@link CompoundTag} into this {@code SolutionGroup}, overriding any existing
     * {@linkplain Ingredient ingredients}.
     *
     * @param tag the {@link CompoundTag} to use for deserialization
     */
    public boolean fromTag (CompoundTag tag) {
        if (! isEmpty () || tag == null || ! tag.containsKey ("Ingredients"))
            return true;

        contents.clear ();

        ListTag serializedIngs = (ListTag) tag.getTag ("Ingredients");

        serializedIngs.forEach (ingredientTag -> {
            Ingredient ingredient = attemptDeserialize ((CompoundTag) ingredientTag);

            if (ingredient != null) {
                addIngredient (ingredient);
            }
        });

        return true;
    }

    /**
     * Determines if this {@code SolutionGroup} contains an {@link ItemStackIngredient}.
     *
     * @return true if this {@code SolutionGroup} contains an {@link ItemStackIngredient}
     */
    public boolean hasSolid () {
        for (Ingredient i : contents) {
            if (i.instance instanceof ItemStack)
                return true;
        }

        return false;
    }

    /**
     * Determines if this {@code SolutionGroup} contains an {@link FluidInstanceIngredient}.
     *
     * @return true if this {@code SolutionGroup} contains an {@link FluidInstanceIngredient}
     */
    public boolean hasLiquid () {
        for (Ingredient i : contents) {
            if (i.instance instanceof FluidInstance)
                return true;
        }

        return false;
    }

    /**
     * Returns any insoluble {@linkplain Ingredient ingredients} in {@code contents} - an insoluble ingredient is any
     * {@link ItemStackIngredient} without a paired solvent.
     *
     * This thus implies that any solution with a liquid is assumed to be a valid solution, which is honored by
     * {@link jard.alchym.blocks.blockentities.GlassContainerBlockEntity}.
     *
     * @return a {@link DefaultedList} of {@linkplain ItemStack ItemStacks}
     */
    public DefaultedList<ItemStack> getDroppableIngredients () {
        DefaultedList <ItemStack> drop = DefaultedList.create ();
        if (this.hasLiquid ())
            return drop;

        for (Ingredient ingredient : contents) {
            drop.add ((ItemStack) ingredient.instance);
        }

        return drop;
    }

    /**
     * If applicable, merges the supplied {@link FluidInstanceIngredient} into this {@code SolutionGroup}'s solvent if they match.
     *
     * @param ingredient the {@link FluidInstanceIngredient} to merge.
     *
     * @return true if the merge was successful
     */
    public boolean mergeSolvent (FluidInstanceIngredient ingredient) {
        if (ingredient instanceof FluidInstanceIngredient && hasLiquid () && getLargest ().instanceMatches (ingredient)) {
            getLargest ().mergeExistingStack (ingredient);
            return true;
        }

        return false;
    }

    /**
     * Retrieves the corresponding solubility rule from {@link AlchymReference.FluidSolubilities} given this
     * {@code SolutionGroup}'s solvent and determines if the supplied ingredient is soluble in this solution.
     *
     * @param ingredient the {@link Ingredient} in question
     * @return true if the solubility rule is -1 or greater than 0.
     */
    public boolean isSolubleIn (Ingredient ingredient) {
        if (ingredient.isSolubleIngredient () && hasLiquid ()) {
            FluidInstanceIngredient solvent = (FluidInstanceIngredient) getLargest ();
            if (solvent.instanceMatches (ingredient))
                return true;

            long solubility = AlchymReference.FluidSolubilities.getSolubility (solvent.instance.getFluid (),
                    (SolubleIngredient) (ingredient.unwrapSpecies ()));

            return solubility > 0 || solubility == -1;
        }

        return false;
    }

    /**
     * Inserts the supplied {@link Ingredient} into this {@SolutionGroup}.
     *
     * @param ingredient the {@link Ingredient} to insert
     * @return any excess portion of the supplied ingredient that can not be inserted
     */
    public Ingredient addSoluble (Ingredient ingredient) {
        if (hasLiquid () && ingredient.isSolubleIngredient ()) {
            FluidInstanceIngredient solvent = (FluidInstanceIngredient) getLargest ();
            long solubility = AlchymReference.FluidSolubilities.getSolubility (solvent.instance.getFluid (), ((SolubleIngredient) ingredient.unwrapSpecies ()));

            if (solubility > 0) {
                Ingredient target = null;

                for (Ingredient ref : contents) {
                    if (ref.instanceMatches (ingredient)) {
                        // Merge stack into ref
                        ref.mergeExistingStack (ingredient);
                        target = ref;
                        break;
                    }
                }

                // If target is still null, no matching ingredient was found, so just add it to the stacks and set it as the target
                if (target == null) {
                    target = ingredient;
                    contents.add (target);
                }

                // Calculate if the target stack needs to be trimmed off to match solubility rules
                long maxDissolvedVol   = (long) ((float) solvent.getAmount () / 1000.f * solubility);
                long totalDissolvedVol = target.getAmount () * ((SolubleIngredient) target.unwrapSpecies ()).getVolume ();

                System.out.println (totalDissolvedVol + "/" + maxDissolvedVol);

                if (totalDissolvedVol > maxDissolvedVol) {
                    Ingredient trimmed = target.trim (totalDissolvedVol - maxDissolvedVol);
                    System.out.println ("Trimming, resulting in an ingredient with amount " + trimmed.getAmount ());
                    return trimmed;
                } else
                    return ingredient.getDefaultEmpty ();
            } else if (solubility == -1) {
                // Ignore solubility rules
                addIngredient (ingredient);

                return ingredient.getDefaultEmpty ();
            }
        }

        return ingredient;
    }

    /**
     * Returns the largest {@link Ingredient} in {@code contents} based on the {@link IngredientGroup#ingredientOrdering}
     * comparator. If this {@code SolutionGroup} contains a liquid, the returned Ingredient is by definition the solvent
     * of this group.
     *
     * @return the largest {@link Ingredient} in {@code contents}
     * @see IngredientGroup#ingredientOrdering
     */
    public Ingredient getLargest () {
        return Collections.max (contents, ingredientOrdering);
    }

    /**
     * Given a {@linkplain Fluid fluid}, compiles all soluble {@linkplain Ingredient ingredients} in {@code contents} into
     * a list of {@linkplain Pair Pairs}, where the left element is the {@link Ingredient} and the right element is the
     * solubility.
     *
     * @param fluid the {@link Fluid} to compare to
     * @return all {@linkplain Ingredient ingredients} in {@code contents} which are soluble in the fluid.
     */
    public List <Pair <Ingredient, Integer>> getSolubles (Fluid fluid) {
        if (isEmpty ())
            return null;

        List <Pair <Ingredient, Integer>> solutes = new ArrayList <> ();

        for (Ingredient ing : contents) {
            if (ing.isSolubleIngredient ()) {
                int solubility = AlchymReference.FluidSolubilities.getSolubility (fluid, (SolubleIngredient) ing.unwrapSpecies ());
                if (solubility > 0)
                    solutes.add (Pair.of (ing, solubility));
            }
        }

        return solutes;
    }

    /**
     * Returns the volume of this {@code SolutionGroup}.
     *
     * @return this {@code SolutionGroup}'s liquid volume summed with its insoluble solid volume.
     *
     * @see SolutionGroup#getLiquidVolume()
     * @see SolutionGroup#getSolidVolume()
     */
    public long getVolume () {
        return getLiquidVolume () + getSolidVolume ();
    }

    /**
     * Returns the volume of all {@linkplain FluidInstanceIngredient FluidInstanceIngredients} in this {@code SolutionGroup}.
     *
     * @return this {@code SolutionGroup}'s liquid volume
     *
     * @see SolutionGroup#getVolume()
     */
    private long getLiquidVolume () {
        long sum = 0;

        if (hasLiquid ()) {
            for (Ingredient ing : contents) {
                if (ing.instance instanceof FluidInstance)
                    sum += ing.getAmount ();
            }
        }

        return sum;
    }

    /**
     * Returns the volume of all insoluble {@linkplain ItemStackIngredient ItemStackIngredients} in this {@code SolutionGroup}.
     * Note that the presence of a liquid implies all solutes are dissolved, meaning that this function would return 0.
     *
     * @return 0 if {@link SolutionGroup#hasLiquid()} returns true, or this {@code SolutionGroup}'s insoluble volume otherwise
     *
     * @see SolutionGroup#getVolume()
     */
    private long getSolidVolume () {
        long sum = 0;

        // For simplicity's sake, it is assumed that any arbitrary amount of ISolubles will not affect the volume of an IngredientGroup
        // which contains a FluidInstanceIngredient. Instead we determine the volume of any insoluble items, which will in practice
        // be sorted out into their own IngredientGroup by a GlassContainerBlockEntity.

        // It's also assumed that all ItemStackIngredients represent Items which implement ISolubles. In the case that there is an ItemStackIngredient
        // which doesn't follow this assumption, we just ignore it
        if (! hasLiquid () && hasSolid ()) {
            for (Ingredient ing : contents) {
                ItemStack stack = ((ItemStack) ing.instance);
                if (stack.getItem () instanceof SolubleIngredient)
                    sum += ((SolubleIngredient) stack.getItem ()).getVolume () * ing.getAmount ();
            }
        }

        return sum;
    }
}
