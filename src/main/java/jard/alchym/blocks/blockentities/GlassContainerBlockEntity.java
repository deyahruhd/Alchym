package jard.alchym.blocks.blockentities;

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import jard.alchym.Alchym;
import jard.alchym.api.ingredient.*;
import jard.alchym.api.ingredient.impl.FluidVolumeIngredient;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import jard.alchym.helper.TransmutationHelper;
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
 *  GlassContainerBlockEntity
 *  The corresponding BlockEntity for a GlassContainerBlock.
 *
 *  Created by jard at 2:17 PM on January 17, 2019.
 ***/
public class GlassContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private List <SolutionGroup> contents = new ArrayList<> ();
    private boolean containsInsoluble = false;
    public final boolean transmutationCapable;
    public final long capacity;

    public GlassContainerBlockEntity () {
        this (0, false);
    }

    public GlassContainerBlockEntity (long capacity, boolean transmutationCapable) {
        super (Alchym.content ().blockEntities.glassContainerBlockEntity);
        contents = new ArrayList<> ();
        this.capacity = capacity;
        this.transmutationCapable = transmutationCapable;
    }

    public ItemStack insertHeldItem (BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack item) {
        ItemStack ret = ItemStack.EMPTY;

        if (isLiquidContainer (item)) {
            if (insertIngredient (new FluidVolumeIngredient(FluidKeys.get (getFluidFromBucket (item.getItem ())).withAmount (FluidAmount.BUCKET))).isEmpty ())
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
            if (!(ingredient instanceof FluidVolumeIngredient))
                this.containsInsoluble = true;
        } else {
            // We must perform two loops one after another: first loop is ran over all groups to check if this ingredient
            // is a solvent that can be merged into an IngredientGroup of the same solvent. In this case, we just
            // merge the solvent in the group with ingredient, then pull out any solute in an (applicable) insoluble group
            // into this solution to match the solubility rule.

            if (ingredient instanceof FluidVolumeIngredient) {
                if (containsInsoluble && contents.size () == 1) {
                    FluidVolume zeroedIngredient = ((FluidVolume) ingredient.unwrap ()).withAmount (FluidAmount.ZERO);
                    contents.add (SolutionGroup.fromIngredients (new FluidVolumeIngredient(zeroedIngredient)));
                }

                for (SolutionGroup group : contents) {
                    if (group.hasLiquid () && group.mergeSolvent ((FluidVolumeIngredient) ingredient)) {
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
                            postInsertSoluble (group);

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
                    postInsertSoluble (group);
                    return ingredient.getDefaultEmpty ();
                }
            }

            addInsoluble (ingredient);
        }
        return ingredient.getDefaultEmpty ();
    }

    public void pullIngredient (Ingredient ingredient) {
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

    protected boolean postInsertSoluble (SolutionGroup targetGroup) {
        if (! transmutationCapable)
            return false;

        for (Ingredient reagent : targetGroup) {
            if (reagent instanceof ItemStackIngredient && TransmutationHelper.isReagent (((ItemStackIngredient) reagent).unwrap ())) {
                return TransmutationHelper.tryWetTransmute (world, this, reagent);
            }
        }

        return false;
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

        if (tag.contains ("Contents")) {
            ListTag contentsList = (ListTag) tag.get ("Contents");

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
            if (group.isInGroup (ingredient) && group.getLargest () instanceof FluidVolumeIngredient)
                return true;
        }

        return false;
    }
}
