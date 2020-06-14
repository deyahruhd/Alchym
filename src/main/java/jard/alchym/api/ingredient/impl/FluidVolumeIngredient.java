package jard.alchym.api.ingredient.impl;

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import jard.alchym.api.ingredient.Ingredient;
import jard.alchym.api.ingredient.IngredientGroup;
import jard.alchym.helper.MathHelper;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/***
 *  FluidVolumeIngredient
 *  An implementation of {@link Ingredient} specialized for {@linkplain FluidVolume FluidVolumes}.
 *
 *  Created by jard at 1:55 AM on November 19, 2018.
 ***/
public class FluidVolumeIngredient extends Ingredient<FluidVolume> {
    public FluidVolumeIngredient(FluidVolume instance) {
        super (instance, FluidVolume.class);
    }

    public FluidVolumeIngredient(FluidVolume instance, IngredientGroup parent) {
        super (instance, FluidVolume.class, parent);
    }

    public FluidVolumeIngredient(CompoundTag tag, Class<FluidVolume> parameterType) {
        super (tag, parameterType);
    }

    @Override
    public int hashCode () {
        return Objects.requireNonNull(instance.getFluidKey().getRawFluid()).hashCode ();
    }

    @Override
    public FluidVolumeIngredient getDefaultEmpty () {
        return new FluidVolumeIngredient (FluidVolumeUtil.EMPTY);
    }

    @Override
    public Ingredient<FluidVolume> dup(int count) {
        FluidVolume dupInstance = this.instance.withAmount (FluidAmount.of1620 (count));
        return new FluidVolumeIngredient(dupInstance);
    }

    @Override
    public boolean isEmpty () {
        return instance.getAmount () == 0 || instance == FluidVolumeUtil.EMPTY;
    }

    @Override
    public int getAmount () {
        return instance.getAmount_F ().as1620();
    }

    @Override
    public Ingredient<FluidVolume> trim (long vol) {
        if (vol <= 0 || instance == FluidVolumeUtil.EMPTY)
            return getDefaultEmpty ();

        vol = vol > getAmount () ? getAmount () : vol;


        FluidVolume trimmed = instance.withAmount (FluidAmount.of1620 ((int) vol));

        int newAmount = MathHelper.rectify (instance.getAmount_F ().as1620 () - (int) vol);

        instance = instance.withAmount (FluidAmount.of1620 (newAmount));
        if (getAmount () == 0)
            this.instance = FluidVolumeUtil.EMPTY;

        return new FluidVolumeIngredient(trimmed);
    }

    @Override
    public boolean instanceMatches (Ingredient other) {
        if (! (other instanceof FluidVolumeIngredient))
            return false;

        return instance.getFluidKey ().getRawFluid () == other.unwrapSpecies ();
    }

    @Override
    protected boolean instanceEquals (Ingredient rhs) { return rhs instanceof FluidVolumeIngredient && instance.equals (((FluidVolumeIngredient) rhs).instance); }

    @Override
    protected void mergeExistingStack (Ingredient<FluidVolume> in) {
        instance = instance.withAmount (FluidAmount.of1620 (instance.getAmount_F ().as1620 () + in.getAmount ()));
    }

    @Override
    protected CompoundTag toTag (CompoundTag tag) {
        tag.put ("InnerFluidVolume", instance.toTag (new CompoundTag ()));

        return tag;
    }

    @Override
    protected void fromTag (CompoundTag tag) {
        if (! tag.contains ("InnerFluidVolume"))
            return;

        this.instance = FluidVolume.fromTag (tag.getCompound ("InnerFluidVolume"));
    }

    @Override
    public Object unwrapSpecies ( ) { return instance.getFluidKey ().getRawFluid (); }
}
