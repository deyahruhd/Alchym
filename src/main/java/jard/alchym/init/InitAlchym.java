package jard.alchym.init;

import net.minecraft.item.Item;

/***
 *  InitAlchym.java
 *  Main container for all initialization modules.
 *
 *  Created by jard at 8:54 PM on December 22, 2018.
 ***/
public class InitAlchym {
    protected InitItems items = new InitItems (this);
    protected InitBlocks blocks = new InitBlocks (this);

    public void initialize () {
        blocks.initialize ();
        items.initialize ();
    }

    public Item getPhilosophersStone () {
        return items.philosophersStone;
    }
}
