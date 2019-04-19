package jard.alchym.api.transmutation.impl;

import jard.alchym.api.transmutation.TransmutationInterface;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/***
 *  DryTransmutationInterface
 *  Implementation of the TransmutationInterface class specifically for transmutations which manipulate ItemEntities in the world.
 *
 *  Created by jard at 6:28 PM on April 18, 2019.
 ***/

public class DryTransmutationInterface extends TransmutationInterface <ItemEntity, World> {
    public DryTransmutationInterface (World endpoint) {
        super (endpoint,
                // Push channel
                (itemEntity, world) -> {

                },

                // Pull channel
                (itemEntity, world) -> {

                },

                // Peek channel
                (itemEntity, world) ->
                    false
                );
    }
}
