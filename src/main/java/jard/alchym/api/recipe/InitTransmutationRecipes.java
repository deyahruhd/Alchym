package jard.alchym.api.recipe;

import jard.alchym.AlchymReference;
import jard.alchym.api.exception.InvalidRecipeException;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import jard.alchym.api.transmutation.TransmutationInterface;
import jard.alchym.init.InitAlchym;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;

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

        // Sample recipes to test the dry transmutation mechanic
        try {
            register (new TransmutationRecipe ("make_gold_from_lead",
                    accessor.createRecipeGroup (new ItemStackIngredient (
                            new ItemStack (alchym.items.getMaterial (AlchymReference.Materials.LEAD, AlchymReference.Materials.Forms.POWDER)))),
                    AlchymReference.Reagents.PHILOSOPHERS_STONE,
                    TransmutationRecipe.TransmutationMedium.DRY,
                    4L,
                    accessor.createRecipeGroup (new ItemStackIngredient (
                            new ItemStack (alchym.items.getMaterial (AlchymReference.Materials.ALCHYMIC_GOLD, AlchymReference.Materials.Forms.POWDER)))),
                    null));
        } catch (InvalidRecipeException e) {
            throw new RuntimeException ("An invalid recipe was supplied when registering transmutation recipes. Stacktrace: ", e);
        }
    }

    public TransmutationRecipe getClosestRecipe (TransmutationInterface source, ItemStack reagent, TransmutationRecipe.TransmutationMedium medium, IWorld world) {
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
