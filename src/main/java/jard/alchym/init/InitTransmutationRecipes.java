package jard.alchym.init;

import jard.alchym.AlchymReference;
import jard.alchym.api.exception.InvalidRecipeException;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import jard.alchym.api.recipe.TransmutationRecipe;
import jard.alchym.api.transmutation.TransmutationInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.WorldAccess;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/***
 *  InitTransmutationRecipes
 *  The initializing module that initializes every transmutation recipe in the mod.
 *
 *  Created by jard at 2:36 PM on May 5, 2018.
 ***/
public class InitTransmutationRecipes {
    protected SortedSet<TransmutationRecipe> transmutationSet = new TreeSet<>(new Comparator<TransmutationRecipe>() {
        @Override
        public int compare(TransmutationRecipe r1, TransmutationRecipe r2) {
            if (r1 == null || r2 == null)
                return 0;

            int complexityCheck = Integer.compare (r2.getInputs ().getCount (), r1.getInputs ().getCount ());

            if (complexityCheck != 0)
                return complexityCheck;

            if (r1.getOutputs () == null || r2.getOutputs () == null) {
                if (r2.getOutputs () != null)
                    return 1;
                else
                    return -1;
            }

            complexityCheck = Integer.compare (r2.getOutputs ().getCount (), r1.getOutputs ().getCount ());

            return complexityCheck != 0 ? complexityCheck : Integer.compare (r2.hashCode (), r1.hashCode ());
        }
    });

    protected final InitAlchym alchym;

    public InitTransmutationRecipes (InitAlchym alchym) {
        this.alchym = alchym;
    }

    public void initialize () {
        RecipeGroupAccessor accessor = RecipeGroupAccessor.getInstance ();

        try {
            register (new TransmutationRecipe ("make_alchymic_reference",
                    accessor.createRecipeGroup (
                            new ItemStackIngredient (
                                new ItemStack (alchym.items.getMaterial (AlchymReference.Materials.NITER, AlchymReference.Materials.Forms.CRYSTAL))),
                            new ItemStackIngredient (
                                    new ItemStack (Items.WRITABLE_BOOK))
                    ),
                    AlchymReference.Reagents.NITER,
                    TransmutationRecipe.TransmutationMedium.DRY,
                    TransmutationRecipe.TransmutationType.COAGULATION,
                    2L,
                    accessor.createRecipeGroup (new ItemStackIngredient (
                            new ItemStack (alchym.items.alchymicReference))),
                    null));
        } catch (InvalidRecipeException e) {
            throw new RuntimeException ("An invalid recipe was supplied when registering transmutation recipes. Stacktrace: ", e);
        }
    }

    public TransmutationRecipe getClosestRecipe (TransmutationInterface source, ItemStack reagent, TransmutationRecipe.TransmutationMedium medium, WorldAccess world) {
        for (TransmutationRecipe recipe : transmutationSet) {
            if (recipe.matches (source, reagent, medium, world))
                return recipe;
        }

        return null;
    }

    private void register (TransmutationRecipe recipe) {
        transmutationSet.add (recipe);
    }
}
