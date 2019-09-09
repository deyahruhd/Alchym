package jard.alchym.init;

import jard.alchym.api.recipe.InitTransmutationRecipes;
import net.minecraft.item.Item;

/***
 *  InitAlchym
 *  Main container for all initialization modules.
 *
 *  Created by jard at 8:54 PM on December 22, 2018.
 ***/
public class InitAlchym {
    public InitItems items = new InitItems (this);
    public InitBlocks blocks = new InitBlocks (this);
    public InitBlockEntities blockEntities = new InitBlockEntities (this);
    public InitTransmutationRecipes transmutationRecipes = new InitTransmutationRecipes (this);

    public void initialize () {
        blocks.initialize ();
        items.initialize ();
        blockEntities.initialize ();
        transmutationRecipes.initialize ();
    }

    public Item getPhilosophersStone () {
        return items.philosophersStone;
    }

    public InitTransmutationRecipes getTransmutations () { return transmutationRecipes; }
}
