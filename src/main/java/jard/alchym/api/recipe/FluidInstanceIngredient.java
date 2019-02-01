package jard.alchym.api.recipe;

import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.nbt.CompoundTag;

/***
 *  ItemStackIngredient.java
 *  TODO: Add a description for this file.
 *
 *  Created by jard at 1:55 AM on November 19, 2018.
 ***/
public class FluidInstanceIngredient extends Ingredient<FluidInstance> {
    public FluidInstanceIngredient (FluidInstance instance) {
        super (instance, FluidInstance.class);
    }

    public FluidInstanceIngredient (FluidInstance instance, IngredientGroup parent) {
        super (instance, FluidInstance.class, parent);
    }

    public FluidInstanceIngredient (CompoundTag tag, Class<FluidInstance> parameterType) {
        super (tag, parameterType);
    }

    @Override
    protected int getAmount () {
        return instance.getAmount ();
    }

    @Override
    public int hashCode () {
        return instance.getFluid ().hashCode ();
    }

    @Override
    protected boolean areInstancesEqual (FluidInstance lhs, FluidInstance rhs) {
        return lhs.equals (rhs);
    }

    @Override
    protected boolean isEmpty () {
        return instance.getAmount () == 0;
    }

    @Override
    protected void mergeExistingStack (Ingredient<FluidInstance> in) {
        instance.addAmount (in.getAmount ());
    }

    @Override
    protected CompoundTag toTag (CompoundTag tag) {
        tag.put ("InnerFluidInstance", instance.toTag (new CompoundTag ()));

        return tag;
    }

    @Override
    protected void fromTag (CompoundTag tag) {
        if (! tag.containsKey ("InnerFluidInstance"))
            return;

        FluidInstance instance = new FluidInstance ();
        instance.fromTag (tag.getCompound ("InnerFluidInstance"));

        this.instance = instance;
    }
}
