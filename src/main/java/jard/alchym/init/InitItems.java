package jard.alchym.init;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.MaterialBlock;
import jard.alchym.items.MaterialItem;
import jard.alchym.items.PhilosophersStoneItem;
import net.minecraft.item.Item;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.ArrayList;

/***
 *  InitItems.java
 *  The initializing module that initializes every item in the mod.
 *
 *  Created by jard at 12:48 AM on December 21, 2018.
 ***/

public class InitItems extends InitAbstract <Item> {
    protected static final Item.Settings DEFAULT_ITEM_SETTINGS = new Item.Settings ().itemGroup (AlchymReference.ALCHYM_GROUP);
    private static final Item.Settings PHILOSOPHERS_STONE_SETTINGS = new Item.Settings ().itemGroup (AlchymReference.ALCHYM_GROUP)
            .rarity (Rarity.EPIC).stackSize (1);
    private static final Item.Settings PHILOSOPHERS_STONE_SETTINGS$1 = new Item.Settings ().rarity (Rarity.EPIC).stackSize (1);


    final Item  lesserPhilosophersStone = new PhilosophersStoneItem (PHILOSOPHERS_STONE_SETTINGS$1, AlchymReference.PhilosophersStoneCharges.LESSER);
    final Item        philosophersStone = new PhilosophersStoneItem (PHILOSOPHERS_STONE_SETTINGS, AlchymReference.PhilosophersStoneCharges.NORMAL);
    final Item greaterPhilosophersStone = new PhilosophersStoneItem (PHILOSOPHERS_STONE_SETTINGS$1, AlchymReference.PhilosophersStoneCharges.GREATER);

    private final List <Pair <String, BlockItem>> queuedBlockItems = new ArrayList <> ();
    final void queueBlockItem (String id, BlockItem block) {
        queuedBlockItems.add (Pair.of (id, block));

        if (block.getBlock () instanceof MaterialBlock)
            materialItems.put (Pair.of (((MaterialBlock) block.getBlock ()).material, AlchymReference.Materials.Forms.BLOCK), block);
    }

    public InitItems (InitAlchym alchym) {
        super (Registry.ITEM, alchym);
    }

    private static final Map <Pair <AlchymReference.Materials, AlchymReference.Materials.Forms>, Item> materialItems = new HashMap<> ();
    static {
        for (AlchymReference.Materials material : AlchymReference.Materials.values ()) {
            if (material.forms == null)
                continue;

            for (AlchymReference.Materials.Forms form : material.forms) {
                if (form.isItem ())
                    materialItems.put (Pair.of (material, form), new MaterialItem (DEFAULT_ITEM_SETTINGS, material, form));
            }
        }
    }
    public Item getMaterial (AlchymReference.Materials material, AlchymReference.Materials.Forms form) {
        return materialItems.get (Pair.of (material, form));
    }

    @Override
    public void initialize () {
        register ("lesser_" + AlchymReference.Items.PHILOSOPHERS_STONE.getName (), lesserPhilosophersStone);
        register (AlchymReference.Items.PHILOSOPHERS_STONE.getName (), philosophersStone);
        register ("greater_" + AlchymReference.Items.PHILOSOPHERS_STONE.getName (), greaterPhilosophersStone);

        for (Map.Entry<Pair<AlchymReference.Materials, AlchymReference.Materials.Forms>, Item> e : materialItems.entrySet ()) {
            register (e.getKey ().getLeft ().getName () + "_" + e.getKey ().getRight ().getName (), e.getValue ());
        }

        for (Pair <String, BlockItem> item : queuedBlockItems) {
            register (item.getLeft (), item.getRight ());
        }
    }
}
