package jard.alchym.api.ingredient.impl;

import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.api.ingredient.Ingredient;
import jard.alchym.api.ingredient.IngredientGroup;
import net.minecraft.nbt.CompoundTag;

/***
 *  FluidInstanceIngredient
 *  An implementation of {@link Ingredient} specialized for {@linkplain FluidInstance FluidInstances}.
 *
 *  Created by jard at 1:55 AM on November 19, 2018.
 ***/
public class FluidInstanceIngredient extends Ingredient<FluidInstance> {
    public static final FluidInstance EMPTY = new FluidInstance (null, 0);

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
    public int hashCode () {
        return instance.getFluid ().hashCode ();
    }

    @Override
    public FluidInstanceIngredient getDefaultEmpty () {
        return new FluidInstanceIngredient (EMPTY);
    }

    @Override
    public Ingredient<FluidInstance> dup(int count) {
        FluidInstance dupInstance = this.instance.copy ();
        dupInstance.setAmount (count);
        return new FluidInstanceIngredient (dupInstance);
    }

    @Override
    public boolean isEmpty () {
        return instance.getAmount () == 0 || instance == EMPTY;
    }

    @Override
    public int getAmount () {
        return instance.getAmount ();
    }

    @Override
    public Ingredient<FluidInstance> trim (long vol) {
        if (vol <= 0 || instance == EMPTY)
            return getDefaultEmpty ();

        vol = vol > getAmount () ? getAmount () : vol;

        FluidInstance trimmed = instance.copy ().setAmount ((int) vol);

        instance.addAmount (- (int) vol);
        if (getAmount () == 0)
            this.instance = EMPTY;

        return new FluidInstanceIngredient (trimmed);
    }

    @Override
    public boolean instanceMatches (Ingredient other) {
        if (! (other instanceof FluidInstanceIngredient))
            return false;

        return instance.getFluid () == other.unwrapSpecies ();
    }

    @Override
    protected boolean instanceEquals (Ingredient rhs) { return rhs instanceof FluidInstanceIngredient && instance.equals (((FluidInstanceIngredient) rhs).instance); }

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

    @Override
    public Object unwrapSpecies ( ) { return instance.getFluid (); }
}