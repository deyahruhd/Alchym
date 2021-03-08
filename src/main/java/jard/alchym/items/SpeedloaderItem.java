package jard.alchym.items;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.ingredient.SolutionGroup;
import jard.alchym.api.ingredient.impl.FluidVolumeIngredient;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

/***
 *  SpeedloaderItem
 *  TODO: Write a description for this file.
 *
 *  Created by jard at 02:02 on March, 07, 2021.
 ***/
public class SpeedloaderItem extends ContainsSolutionItem {
    public SpeedloaderItem (Settings settings) {
        super (settings);
    }

    @Override
    public void appendStacks (ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
        if (this.isIn (itemGroup)) {
            defaultedList.add (new ItemStack (this));

            for (AlchymReference.Materials material : AlchymReference.Materials.values ()) {
                if (material.forms == null || !material.forms.contains (AlchymReference.Materials.Forms.LIQUID))
                    continue;

                Fluid fluid = Alchym.content ().fluids.getMaterial (material);
                FluidKey key = FluidKeys.get (fluid);

                SolutionGroup group = new SolutionGroup ();
                group.addIngredient (new FluidVolumeIngredient (key.withAmount (FluidAmount.BUCKET)));

                CompoundTag tag = new CompoundTag ();
                tag.put ("ContainedGroup", group.toTag (new CompoundTag ()));

                ItemStack flaskWithFluid = new ItemStack (this);
                flaskWithFluid.setTag (tag);
                defaultedList.add (flaskWithFluid);
            }
        }
    }
}
