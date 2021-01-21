package jard.alchym.api.exception;

import jard.alchym.api.transmutation.TransmutationAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/***
 *  InvalidRecipeException
 *  A special exception thrown when a {@link jard.alchym.api.transmutation.TransmutationAction}
 *  attempts to apply invalid transmutation parameters.
 *
 *  @see TransmutationAction#apply(ItemStack, BlockPos)
 *
 *  Created by jard at 1:08 PM on September 1, 2018.
 ***/
public class InvalidActionException extends Exception {
    public InvalidActionException (String message) {
        super (message);
    }
}
