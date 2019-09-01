package jard.alchym.api.transmutation.impl;

import jard.alchym.AlchymReference;
import jard.alchym.api.ingredient.impl.ItemStackIngredient;
import jard.alchym.api.transmutation.TransmutationInterface;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/***
 *  DryTransmutationInterface
 *  Implementation of the TransmutationInterface class specifically for transmutations which manipulate ItemEntities in the world.
 *
 *  Created by jard at 6:28 PM on April 18, 2019.
 ***/

public class DryTransmutationInterface extends TransmutationInterface <ItemStackIngredient, Pair<World, Vec3d>> {
    public DryTransmutationInterface (Pair<World, Vec3d> endpoint) {
        super (endpoint,
                // Push channel
                (item, transmutationLoc) -> {
                    World world = transmutationLoc.getLeft();
                    Vec3d pos   = transmutationLoc.getRight();

                    ItemScatterer.spawn (world, pos.x, pos.y, pos.z, item.unwrap ());
                },

                // Pull channel
                (item, transmutationLoc) -> {
                    int max = item.getAmount ();

                    ItemEntity [] nearbyEntities = filterNearbyEntities (item.unwrap (), transmutationLoc,
                            AlchymReference.DRY_TRANSMUTATION_RADIUS);

                    for (ItemEntity itemEntity : nearbyEntities) {
                        ItemStack stack = itemEntity.getStack ();

                        if (max > stack.getCount ()) {
                            itemEntity.kill ();

                            max -= stack.getCount ();
                        } else {
                            stack.setCount (stack.getCount () - max);
                            break;
                        }
                    }
                },

                // Peek channel
                (item, transmutationLoc) -> {
                    World world = transmutationLoc.getLeft ();
                    Vec3d pos = transmutationLoc.getRight ();

                    ItemEntity [] nearbyEntities = filterNearbyEntities (item.unwrap (), transmutationLoc,
                            AlchymReference.DRY_TRANSMUTATION_RADIUS);

                    int accum = 0;

                    for (ItemEntity itemEntity : nearbyEntities) {
                        accum += itemEntity.getStack ().getCount ();
                        if (accum >= item.getAmount ())
                            return true;
                    }

                    return false;
                });
    }

    public static ItemEntity [] filterNearbyEntities (ItemStack item, Pair<World, Vec3d> transmutationLoc, double radius) {
        double radiusSq = radius * radius;
        World world = transmutationLoc.getLeft ();
        Vec3d pos = transmutationLoc.getRight ();

        Box bounds = new Box (pos.subtract (new Vec3d (0.325, 0.25, 0.325)), pos.add (0.325, 0.25, 0.325));

        List<ItemEntity> nearbyEntities = world.getEntities (ItemEntity.class, bounds);
        return nearbyEntities.stream ().filter (
                itemEntity ->
                        item.isItemEqualIgnoreDamage(itemEntity.getStack())
                           &&
                        itemEntity.squaredDistanceTo (transmutationLoc.getRight ()) < radiusSq
            ).toArray (ItemEntity[]::new);
    }
}
