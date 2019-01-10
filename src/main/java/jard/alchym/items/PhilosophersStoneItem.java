package jard.alchym.items;

import jard.alchym.AlchymReference;
import net.minecraft.item.Item;

/***
 *  PhilosophersStoneItem.java
 *  The philosopher's stone item.
 *
 *  Created by jard at 9:02 PM on December 30, 2018.
 ***/
public class PhilosophersStoneItem extends Item {
    public final long minCharge, maxCharge;

    public PhilosophersStoneItem (Settings settings, AlchymReference.PhilosophersStoneCharges charge) {
        super (settings);

        minCharge = charge.min;
        maxCharge = charge.max;
    }
}
