package jard.alchym.blocks.blockentities;

import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.Alchym;
import jard.alchym.api.ingredient.*;
import jard.alchym.api.ingredient.impl.FluidInstanceIngredient;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/***
 *  GlassContainerBlockEntity.java
 *  The corresponding BlockEntity for a GlassContainerBlock.
 *
 *  Created by jard at 2:17 PM on January 17, 2019.
 ***/

public class GlassContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private List <SolutionGroup> contents = new ArrayList<> ();
    private boolean containsInsoluble = false;
    public final long capacity;

    public GlassContainerBlockEntity () {
        this (0);
    }

    public GlassContainerBlockEntity (long capacity) {
        super (Alchym.content ().blockEntities.glassContainerBlockEntity);
        contents = new ArrayList<> ();
        this.capacity = capacity;
    }

    public ItemStack insertHeldItem (BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack item) {
        ItemStack ret = ItemStack.EMPTY;

        if (isLiquidContainer (item)) {
            if (insertIngredient (new FluidInstanceIngredient (new FluidInstance (getFluidFromBucket (item.getItem ()), 1000))).isEmpty ())
                ret = new ItemStack (Items.BUCKET);
        } else {
            ret = (ItemStack) insertIngredient (new ItemStackIngredient (item)).unwrap ();
        }

        overflow ();

        player.addChatMessage (new LiteralText("This container's volume is now: " + getVolume () + " / " + capacity), true);

        markDirty ();

        return ret;
    }

    public Ingredient insertIngredient (Ingredient ingredient) {
        markDirty ();

        if (contents.isEmpty ()) {
            contents.add (SolutionGroup.fromIngredients (ingredient));
            this.containsInsoluble = true;
        } else {
            // We must perform two loops one after another: first loop is ran over all groups to check if this ingredient
            // is a solvent that can be merged into an IngredientGroup of the same solvent. In this case, we just
            // merge the solvent in the group with ingredient, then pull out any solute in an (applicable) insoluble group
            // into this solution to match the solubility rule.

            if (ingredient instanceof FluidInstanceIngredient) {
                if (containsInsoluble && contents.size () == 1) {
                    FluidInstance zeroedIngredient = ((FluidInstance) ingredient.unwrap ()).copy ().setAmount (0);
                    contents.add (SolutionGroup.fromIngredients (new FluidInstanceIngredient (zeroedIngredient)));
                }

                for (SolutionGroup group : contents) {
                    if (group.hasLiquid () && group.mergeSolvent ((FluidInstanceIngredient) ingredient)) {
                        if (containsInsoluble) {
                            for (Pair<Ingredient, Integer> insoluble : getInsolubleGroup ().getSolubles ((Fluid) group.getLargest ().unwrapSpecies ())) {
                                Ingredient solvent = group.getLargest ();
                                Ingredient dissolvedInsoluble = group.getMatchingIngredient (insoluble.getLeft ());
                                long solubility = insoluble.getRight ();

                                // Calculate if the insoluble part must be trimmed off to re-establish the solubility constant
                                long maxDissolvedVol = (long) ((float) solvent.getAmount () / 1000.f * solubility);
                                // Dissolved insoluble that existed in this new group

                                long totalDissolvedVol = 0;
                                if (! dissolvedInsoluble.isEmpty ())
                                    totalDissolvedVol = dissolvedInsoluble.getAmount () * ((SolubleIngredient) dissolvedInsoluble.unwrapSpecies ()).getVolume ();

                                // Perform trim and add it as a soluble to the SolutionGroup
                                if (totalDissolvedVol < maxDissolvedVol) {
                                    Ingredient trimmed = insoluble.getLeft ().trim (maxDissolvedVol - totalDissolvedVol);
                                    group.addSoluble (trimmed);
                                }
                            }

                            if (getInsolubleGroup ().isEmpty ()) {
                                contents.remove (0);
                                containsInsoluble = false;
                            }
                        }

                        return ingredient.getDefaultEmpty ();
                    }
                }
            }

            // Second loop attempts to insert the ingredient as a normal solute.

            for (SolutionGroup group : contents) {
                if (group.isSolubleIn (ingredient)) {
                    addInsoluble (group.addSoluble (ingredient));
                    return ingredient.getDefaultEmpty ();
                }
            }

            addInsoluble (ingredient);
        }
        return ingredient.getDefaultEmpty ();
    }

    public void pullIngredient (Ingredient ingredient) {
        // TODO: Implement the following subroutine:
        // 1. Determine total amount of ingredient this GlassContainerBlockEntity contains -> total
        // 2. Determine the "excess" solute by calculating the quantity `total - ingredient.getAmount ()`
        // 3. Remove all instances of the ingredient from this entity's SolutionGroups, then call insertIngredient with
        //    a newly instantiated Ingredient consisting of the excess solute

        int total = 0;

        for (SolutionGroup group : contents) {
            if (group.isInGroup (ingredient)) {
                Ingredient match = group.getMatchingIngredient (ingredient);

                total += match.getAmount ();
                group.removeIngredient (match);
            }
        }

        int delta = total - ingredient.getAmount ();

        if (delta > 1)
            insertIngredient (ingredient.dup (delta));
    }


    public void addInsoluble (Ingredient insoluble) {
        if (contents.isEmpty () || insoluble.isEmpty ()) return;

        if (containsInsoluble)
            contents.get (0).addIngredient (insoluble);
        else
            contents.add (0, SolutionGroup.fromIngredients (insoluble));

        containsInsoluble = true;
    }

    public DefaultedList <ItemStack> getDrops () {
        DefaultedList <ItemStack> accumulatedDrops = DefaultedList.of ();

        for (SolutionGroup group : contents) {
            accumulatedDrops.addAll (group.getDroppableIngredients ());
        }

        return accumulatedDrops;
    }

    public boolean canAccept (ItemStack stack) {
        return ! stack.isEmpty () && (
                (stack.getItem () instanceof SolubleIngredient && ((SolubleIngredient) stack.getItem ()).canInsert (this)) || // SolubleIngredient check
                (stack.getItem () instanceof BucketItem)); // Bucket check
    }

    public CompoundTag toTag (CompoundTag tag) {
        tag = super.toTag (tag);

        ListTag contentsList = new ListTag ();

        for (SolutionGroup group : contents) {
            contentsList.add (group.toTag (new CompoundTag ()));
        }

        tag.put ("Contents", contentsList);
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        super.fromTag (tag);

        if (tag.containsKey ("Contents")) {
            ListTag contentsList = (ListTag) tag.getTag ("Contents");

            for (Tag group : contentsList) {
                SolutionGroup deserializedGroup = new SolutionGroup ();
                if (deserializedGroup.fromTag ((CompoundTag) group))
                    contents.add (deserializedGroup);
            }
        }
    }

    public long getVolume () {
        long volume = 0;
        for (SolutionGroup group : contents) {
            volume += group.getVolume ();
        }

        return volume;
    }

    private SolutionGroup getInsolubleGroup () {
        if (containsInsoluble)
            return contents.get (0);

        return null;
    }

    private boolean isLiquidContainer (ItemStack item) {
        return item.getItem () instanceof BucketItem && item.getItem () != Items.BUCKET;
    }

    private Fluid getFluidFromBucket (Item bucket) {
        if (bucket == Items.WATER_BUCKET)
            return Fluids.WATER;
        else if (bucket == Items.LAVA_BUCKET)
            return Fluids.LAVA;
        else
            return null;
    }

    private void overflow () {
        long volume = getVolume ();

        if (volume > capacity) {
            // Decant the lowest-density-solvent Solution
            // TODO: Implement this
        }
    }

    @Override
    public void fromClientTag (CompoundTag tag) {
        fromTag (tag);
    }

    @Override
    public CompoundTag toClientTag (CompoundTag tag) {
        return toTag (tag);
    }

    public boolean isInSolution (Ingredient ingredient) {
        for (SolutionGroup group : contents) {
            if (group.isInGroup (ingredient) && group.getLargest () instanceof FluidInstanceIngredient)
                return true;
        }

        return false;
    }
}
