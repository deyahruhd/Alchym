package jard.alchym.init;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.MaterialBlock;
import jard.alchym.items.AlchymicReferenceItem;
import jard.alchym.items.RevolverItem;
import jard.alchym.items.MaterialItem;
import jard.alchym.items.PhilosophersStoneItem;
import net.minecraft.block.HopperBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
    public final Item  lesserPhilosophersStone =
            new PhilosophersStoneItem (AlchymReference.PHILOSOPHERS_STONE_SETTINGS$1, AlchymReference.PhilosophersStoneCharges.LESSER);
    public final Item        philosophersStone = new PhilosophersStoneItem (AlchymReference.PHILOSOPHERS_STONE_SETTINGS, AlchymReference.PhilosophersStoneCharges.NORMAL);
    public final Item greaterPhilosophersStone = new PhilosophersStoneItem (AlchymReference.PHILOSOPHERS_STONE_SETTINGS$1, AlchymReference.PhilosophersStoneCharges.GREATER);

    public final Item                 revolver = new RevolverItem (AlchymReference.TOOL_SETTINGS);

    public final Item           chymicalTubing = new Item (AlchymReference.SMALL_GLASSWARE_SETTINGS);

    public final Item        alchymicReference = new AlchymicReferenceItem (AlchymReference.TOOL_SETTINGS);

    private final List <Pair <String, BlockItem>> queuedBlockItems = new ArrayList <> ();
    final void queueBlockItem (String id, BlockItem block) {
        if (block.getBlock () instanceof MaterialBlock)
            materialItems.put (Pair.of (((MaterialBlock) block.getBlock ()).material, AlchymReference.Materials.Forms.BLOCK), block);
        else
            queuedBlockItems.add (Pair.of (id, block));
    }

    public InitItems (InitAlchym alchym) {
        super (Registry.ITEM, alchym);
    }

    private static final Map <Pair <AlchymReference.Materials, AlchymReference.Materials.Forms>, Item> materialItems = new LinkedHashMap<> ();
    static {
        for (AlchymReference.Materials material : AlchymReference.Materials.values ()) {
            if (material.forms == null)
                continue;

            for (AlchymReference.Materials.Forms form : material.forms) {
                if (form.isItem ()) {
                    Item.Settings settings = AlchymReference.DEFAULT_ITEM_SETTINGS;
                    if (form == AlchymReference.Materials.Forms.NUGGET)
                        settings = new Item.Settings ();


                    materialItems.put (Pair.of (material, form), new MaterialItem (settings, material, form));
                }
            }
        }
    }
    public Item getMaterial (AlchymReference.Materials material, AlchymReference.Materials.Forms form) {
        return materialItems.get (Pair.of (material, form));
    }

    @Override
    public void initialize () {
        register (AlchymReference.Items.ALCHYMIC_REFERENCE.getName (), alchymicReference);

        register (AlchymReference.Items.REVOLVER.getName (), revolver);

        register ("lesser_" + AlchymReference.Items.PHILOSOPHERS_STONE.getName (), lesserPhilosophersStone);
        register (AlchymReference.Items.PHILOSOPHERS_STONE.getName (), philosophersStone);
        register ("greater_" + AlchymReference.Items.PHILOSOPHERS_STONE.getName (), greaterPhilosophersStone);

        for (Pair <String, BlockItem> item : queuedBlockItems) {
            register (item.getLeft (), item.getRight ());
        }

        register (AlchymReference.Items.CHYMICAL_TUBING.getName (), chymicalTubing);

        for (Map.Entry<Pair<AlchymReference.Materials, AlchymReference.Materials.Forms>, Item> e : materialItems.entrySet ()) {
            String name = e.getKey ().getLeft ().getName () + "_" + e.getKey ().getRight ().getName ();
            name = name.replaceAll ("glass_crystal", "glass");

            register (name, e.getValue ());
        }
    }
}
