package jard.alchym.items;

import jard.alchym.AlchymReference;
import jard.alchym.api.ingredient.SolubleIngredient;
import jard.alchym.blocks.blockentities.GlassContainerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/***
 *  MaterialItem.java
 *  A generic material item which is instantiated with a certain material and form.
 *
 *  Created by jard at 1:36 PM on December 21, 2018.
 ***/
public class MaterialItem extends Item implements SolubleIngredient {
    public final AlchymReference.Materials material;
    public final AlchymReference.Materials.Forms form;

    public MaterialItem (Settings settings, AlchymReference.Materials material, AlchymReference.Materials.Forms form) {
        super (settings);

        this.material = material;
        this.form = form;
    }

    @Environment (EnvType.CLIENT)
    public boolean hasEnchantmentGlow (ItemStack itemStack) {
        return material == AlchymReference.Materials.ALCHYMIC_GOLD;
    }

    @Override
    public boolean canInsert (GlassContainerBlockEntity container) {
        return  form == AlchymReference.Materials.Forms.POWDER ||
                form == AlchymReference.Materials.Forms.REAGENT_POWDER ||
                form == AlchymReference.Materials.Forms.SMALL_POWDER ||
                form == AlchymReference.Materials.Forms.REAGENT_SMALL_POWDER ||
                form == AlchymReference.Materials.Forms.NUGGET ||

                (form == AlchymReference.Materials.Forms.INGOT && container.capacity >= AlchymReference.GlassContainers.VAT.capacity);
    }

    @Override
    public AlchymReference.Materials getMaterial ( ) {
        return material;
    }

    @Override
    public long getSolubility (Fluid fluid) {
        return AlchymReference.FluidSolubilities.getSolubility (fluid, this);
    }

    @Override
    public long getVolume ( ) {
        return form.volume;
    }
}
