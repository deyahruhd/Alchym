package jard.alchym.api.transmutation.impl;

import jard.alchym.api.ingredient.Ingredient;
import jard.alchym.api.transmutation.TransmutationInterface;
import jard.alchym.blocks.blockentities.GlassContainerBlockEntity;

import java.util.function.BiPredicate;

/***
 *  WetTransmutationInterface
 *  Implementation of the TransmutationInterface class specifically for transmutations which manipulate ItemEntities in the world.
 *
 *  Created by jard at 6:31 PM on April 18, 2019.
 ***/
public class WetTransmutationInterface extends TransmutationInterface <Ingredient, GlassContainerBlockEntity> {
    public WetTransmutationInterface (GlassContainerBlockEntity endpoint) {
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
