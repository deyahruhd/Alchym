package jard.alchym.items;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.ingredient.Ingredient;
import jard.alchym.api.ingredient.SolutionGroup;
import jard.alchym.api.ingredient.impl.FluidVolumeIngredient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/***
 *  ChymicalFlaskItem
 *  Generic flask item that can store up to one bucket's (1000 mB or 81000 units) worth of a SolutionGroup.
 *
 *  Created by jard at 02:14 on February, 04, 2021.
 ***/
public class ChymicalFlaskItem extends Item {
    public ChymicalFlaskItem (Settings settings) {
        super (settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText (
                this.getTranslationKey (stack),
                new TranslatableText (getSolventKey (stack)));
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

    private String getSolventKey (ItemStack stack) {
        Fluid solvent = getSolvent (stack);

        if (solvent != null) {
            Identifier solventId = Registry.FLUID.getId (solvent);

            return String.format ("block.%s.%s", solventId.getNamespace (), solventId.getPath ());
        }

        return "item.alchym.chymical_flask.no_solvent_prefix";
    }

    public static SolutionGroup getSolutionGroup (ItemStack stack) {
        if (! stack.hasTag ()) {
            stack.setTag (new CompoundTag ());
        }

        SolutionGroup group = new SolutionGroup ();

        if (! stack.getTag ().contains ("ContainedGroup"))
            stack.setTag (group.toTag (new CompoundTag ()));
        else
            group.fromTag ((CompoundTag) stack.getTag ().get ("ContainedGroup"));

        return group;
    }

    public static Fluid getSolvent (ItemStack stack) {
        if (! (stack.hasTag () && stack.getTag ().contains ("ContainedGroup")))
            return null;

        SolutionGroup group = getSolutionGroup (stack);

        if (group.hasLiquid ())
            for (Ingredient i : group) {
                return ((FluidVolume) i.unwrap ()).getFluidKey ().getRawFluid ();
            }

        return null;
    }
}
