package jard.alchym.api.transmutation;

import jard.alchym.AlchymReference;

/***
 *  ReagentItem
 *  Interface class for items that participate in alchemical transmutation as a reagent (niter, Philosopher's Stone, ...)
 *
 *  Created by jard at 11:23 PM on September 1, 2019.
 ***/
public interface ReagentItem {
    /**
     * Determines if this {@code ReagentItem} is a reagent. Used primarily by {@linkplain jard.alchym.items.MaterialItem}s, as not all
     * material types are reagents.
     *
     * @return true if this item is an alchemical reagent
     */
    boolean isReagent ();

    /**
     * Gets a per-unit alchemical charge based on the {@link net.minecraft.item.Item} type of this {@code ReagentItem}.
     *
     * @return the unit charge as a long
     */
    long getUnitCharge ();

    /**
     * Returns the type of reagent this {@code ReagentItem} corresponds to.
     * @see jard.alchym.AlchymReference.Reagents
     *
     * @return An enumerator from {@link AlchymReference.Reagents}, or {@linkplain jard.alchym.AlchymReference.Reagents#UNKNOWN}
     * if this {@code ReagentItem} does not correspond to anything.
     */
    AlchymReference.Reagents getReagentType ();
}
