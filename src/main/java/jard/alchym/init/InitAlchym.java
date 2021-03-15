package jard.alchym.init;

import jard.alchym.AlchymReference;
import net.minecraft.item.Item;

/***
 *  InitAlchym
 *  Main container for all initialization modules.
 *
 *  Created by jard at 8:54 PM on December 22, 2018.
 ***/
public class InitAlchym {
    public InitBlocks blocks = new InitBlocks (this);
    public InitBlockEntities blockEntities = new InitBlockEntities (this);
    public InitBookPages pages = new InitBookPages (this);
    public InitFluids fluids = new InitFluids (this);
    public InitItems items = new InitItems (this);
    public InitPackets packets = new InitPackets (this);
    public InitSounds sounds = new InitSounds (this);
    public InitTransmutationRecipes transmutationRecipes = new InitTransmutationRecipes (this);
    public InitWorldgenFeatures features = new InitWorldgenFeatures (this);

    public void initialize () {
        fluids.initialize ();
        blocks.initialize ();
        items.initialize ();
        blockEntities.initialize ();
        transmutationRecipes.initialize ();
        sounds.initialize ();
        packets.initialize ();
        features.initialize ();
    }

    public Item getPhilosophersStone () {
        return items.philosophersStone;
    }

    public InitTransmutationRecipes getTransmutations () { return transmutationRecipes; }
}
