package jard.alchym.helper;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.exception.InvalidActionException;
import jard.alchym.api.exception.InvalidInterfaceException;
import jard.alchym.api.ingredient.Ingredient;
import jard.alchym.api.ingredient.SolutionGroup;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import jard.alchym.api.recipe.TransmutationRecipe;
import jard.alchym.api.transmutation.ReagentItem;
import jard.alchym.api.transmutation.TransmutationAction;
import jard.alchym.api.transmutation.TransmutationInterface;
import jard.alchym.api.transmutation.impl.DryTransmutationInterface;
import jard.alchym.api.transmutation.impl.WetTransmutationInterface;
import jard.alchym.blocks.blockentities.ChymicalContainerBlockEntity;
import jard.alchym.items.ChymicalFlaskItem;
import jard.alchym.items.MaterialItem;
import jard.alchym.items.PhilosophersStoneItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

/***
 *  TransmutationHelper
 *  Contains various helper methods relating to transmutation (e.g. handling of dry transmutation or item entity raytracing methods)
 *
 *  Created by jard at 5:18 PM on February 13, 2018.
 ***/
public class TransmutationHelper {
    public static boolean tryDryTransmute (World world, PlayerEntity player, ItemStack reagent) {
        if (!isReagent(reagent))
            return false;

        ItemEntity itemEntity = getLookedAtItem(player, 1.f);
        if (itemEntity == null)
            return false;

        Pair<World, Vec3d> endpoint = new Pair<>(world, itemEntity.getPos ());

        DryTransmutationInterface source, target;

        try {
            source = new DryTransmutationInterface (endpoint);
            target = new DryTransmutationInterface (endpoint);
        } catch (InvalidInterfaceException e) {
            return false;
        }

        TransmutationRecipe recipe = Alchym.content ().getTransmutations ()
                .getClosestRecipe (source, reagent, TransmutationRecipe.TransmutationMedium.DRY, world);

        if (recipe == null)
            return false;

        int recipeScale = recipe.getRecipeScale (source);
        int reagentScale = (int) (getReagentCharge (reagent) / recipe.getCharge ());

        if (reagentScale < recipeScale)
            recipeScale = reagentScale;

        if (recipeScale == 0)
            throw new RuntimeException ("Attempted transmutation recipe '" + recipe.getName () + "', which returned a " +
                    "0 recipe scale. This should never happen, something is gravely broken");

        TransmutationAction action = new TransmutationAction(source, target, recipe, world);

        try {
            if (action.apply(reagent, new BlockPos (itemEntity.getPos()))) {
                if (reagent.getItem () instanceof PhilosophersStoneItem) {
                    // Just subtract off the current recipe's cost times recipeScale
                } else if (reagent.getItem () instanceof MaterialItem) {
                    if (((MaterialItem) reagent.getItem ()).form == AlchymReference.Materials.Forms.REAGENT_POWDER) {
                        long newCharge = getReagentCharge (reagent) - (recipe.getCharge () * recipeScale);

                        reagent.setCount ((int) newCharge);
                    } else {
                        throw new IllegalStateException ("Player '" + player.getDisplayName () +
                                "' attempted a transmutation with a non-reagent item '" +
                                reagent.getItem ().getName () + "'!");
                    }
                }
            }
        }
        catch (InvalidActionException e) {
            return false;
        }

        world.playSound(null, new BlockPos (itemEntity.getPos ()), Alchym.content().sounds.dryTransmute, SoundCategory.PLAYERS, 1.f, 1.f);

        return true;
    }

    public static boolean tryWetTransmute (World world, ChymicalContainerBlockEntity container, Ingredient reagent) {
        if (! (reagent instanceof ItemStackIngredient) || !isReagent (((ItemStackIngredient) reagent).unwrap()))
            return false;

        WetTransmutationInterface source, target;

        try {
            source = new WetTransmutationInterface (container);
            target = new WetTransmutationInterface (container);
        } catch (InvalidInterfaceException e) {
            return false;
        }

        TransmutationRecipe recipe = Alchym.content ().getTransmutations ()
                .getClosestRecipe (source, ((ItemStackIngredient) reagent).unwrap(), TransmutationRecipe.TransmutationMedium.WET, world);

        if (recipe == null)
            return false;

        // Calcination can not happen if there is no insoluble group in the container
        if (recipe.type == TransmutationRecipe.TransmutationType.CALCINATION && ! container.hasOnlyInsoluble ())
            return false;

        int recipeScale = recipe.getRecipeScale (source);
        int reagentScale = (int) (getReagentCharge (((ItemStackIngredient) reagent).unwrap ()) / recipe.getCharge ());

        if (reagentScale < recipeScale)
            recipeScale = reagentScale;

        if (recipeScale == 0)
            throw new RuntimeException ("Attempted transmutation recipe '" + recipe.getName () + "', which returned a " +
                    "0 recipe scale. This should never happen, something is gravely broken");

        TransmutationAction action = new TransmutationAction(source, target, recipe, world);

        try {
            if (action.apply (((ItemStackIngredient) reagent).unwrap (), container.getPos ())) {
                container.pullIngredient (reagent);

                AlchymReference.Materials baseMaterial = ((MaterialItem) ((ItemStackIngredient) reagent).unwrap ().getItem ()).material;
                Item baseItem;
                int baseCount = (int) (getReagentCharge (((ItemStackIngredient) reagent).unwrap ()) - (recipe.getCharge () * recipeScale));

                baseItem = Alchym.content ().items.getMaterial (baseMaterial, AlchymReference.Materials.Forms.REAGENT_POWDER);

                ItemStackIngredient newReagent = new ItemStackIngredient (new ItemStack (baseItem, baseCount));

                if (! newReagent.isEmpty ()) {
                    SolutionGroup groupToTransmute = container.insertIngredient (newReagent);
                    container.postInsert (groupToTransmute);
                }
            }
        } catch (InvalidActionException e) {
            return false;
        }

        return true;
    }

    public static boolean isReagent (ItemStack reagent) {
        return ! reagent.isEmpty() && reagent.getItem () instanceof ReagentItem && ((ReagentItem) reagent.getItem()).isReagent();
    }

    public static long getReagentCharge (ItemStack reagent) {
        if (! isReagent (reagent))
            return 0L;

        if (reagent.getItem() instanceof PhilosophersStoneItem)
            PhilosophersStoneItem.setHeldStack (reagent);

        return ((ReagentItem) reagent.getItem ()).getUnitCharge() * reagent.getCount();
    }

    // Returns the ItemEntity that a player may be looking at, or null if the player is not looking at any ItemEntity.
    public static ItemEntity getLookedAtItem (PlayerEntity player, float partialTicks) {
        ItemEntity item = null;

        if (player != null && player.world != null) {
            double reach = 4.5;

            boolean flag = false;
            if (player.isCreative ())
                reach = 6.0D;
            else
                flag = reach > 3.0D;

            double reachSq = reach * reach;

            Vec3d pos  = player.getCameraPosVec (partialTicks);
            Vec3d look = player.getRotationVec (partialTicks);

            Box box = player.getBoundingBox ().stretch(look.multiply (reach)).expand(1.0D, 1.0D, 1.0D);
            EntityHitResult hitResult = ProjectileUtil.raycast (player, pos, pos.add (look.multiply (reach)), box, (entity) ->
                    entity instanceof ItemEntity, reachSq);
            if (hitResult != null) {
                ItemEntity entity = (ItemEntity) hitResult.getEntity ();
                double dist = pos.squaredDistanceTo (hitResult.getPos ());
                if (! (flag && dist > 9.0D) && dist < reachSq)
                    item = entity;
            }
        }

        return item;
    }

    // Calculates the averaged position of all of the ItemEntities in the argument.
    private static Vec3d getTransmutationCenter (ItemEntity [] items) {
        Vec3d transmutationCenter = Vec3d.ZERO;

        for (ItemEntity item : items) {
            transmutationCenter.add (new Vec3d (item.getX (), item.getY (), item.getZ ()));
        }

        if (items.length > 0)
            transmutationCenter.multiply (1.0 / ((double) items.length));

        return transmutationCenter;
    }

    public static Fluid getFluidFromBucket (Item bucket) {
        if (bucket == Items.WATER_BUCKET)
            return Fluids.WATER;
        else if (bucket == Items.LAVA_BUCKET)
            return Fluids.LAVA;
        else
            return null;
    }
}
