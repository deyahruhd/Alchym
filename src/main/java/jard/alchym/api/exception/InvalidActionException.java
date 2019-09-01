package jard.alchym.api.exception;

import jard.alchym.api.transmutation.TransmutationAction;

/***
 *  InvalidRecipeException.java
 *  A special exception thrown when a {@link jard.alchym.api.transmutation.TransmutationAction}
 *  attempts to apply invalid transmutation parameters.
 *
 *  @see TransmutationAction#apply()
 *
 *  Created by jared at 1:08 PM on September 1, 2018.
 ***/
public class InvalidActionException extends Exception {
    public InvalidActionException (String message) {
        super (message);
    }
}
