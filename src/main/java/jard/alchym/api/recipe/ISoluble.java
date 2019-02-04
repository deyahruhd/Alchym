package jard.alchym.api.recipe;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.GlassContainerBlock;
import jard.alchym.blocks.blockentities.GlassContainerBlockEntity;
import net.minecraft.fluid.Fluid;

/***
 *  ISoluble.java
 *  Interface class for any solute that can be dissolved within a GlassContainerBlock.
 *
 *  Describes the solubility of a solute with respect to some Fluid by returning the amount of solute that can
 *  dissolve within that Fluid. 0 indicates insolubility while -1 indicates complete miscibility.
 *
 *  Created by jard at 12:43 PM on January 17, 2019.
 ***/

public interface ISoluble {
    boolean canInsert (GlassContainerBlockEntity container);

    AlchymReference.Materials getMaterial ();

    long getSolubility (Fluid fluid);

    // Returns a unit volume with respect to the number of millibuckets in a bucket of fluid (1000).
    // For liquid solutes, this number is always 1 (indicating 1 bucket of liquid has the same volume as 1 bucket of water),
    // since getAmount will return the amount in millibuckets anyways.
    long getVolume ();
}
