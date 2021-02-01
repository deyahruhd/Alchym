package jard.alchym.init;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.blocks.AlchymBlock;
import jard.alchym.blocks.ChymicalContainerBlock;
import jard.alchym.blocks.MaterialBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

/***
 *  InitBlocks.java
 *  The initializing module that initializes every item in the mod.
 *
 *  Created by jard at 12:05 AM on December 21, 2018.
 ***/
public class InitBlocks extends InitAbstract <Block> {
    InitBlocks (InitAlchym alchym) {
        super (Registry.BLOCK, alchym);
    }

    public final Block copperCrucible = new ChymicalContainerBlock (FabricBlockSettings.of (Material.METAL).strength (3.0f, 0.75f).build (),
            AlchymReference.ChymicalContainers.COPPER_CRUCIBLE);

    public final Block alembic = new ChymicalContainerBlock (FabricBlockSettings.of (Material.GLASS)
            .strength (1.0f, 0.5f).nonOpaque ().build (),
            AlchymReference.ChymicalContainers.CHYMICAL_ALEMBIC);

    public final Block niterBearingStone = new Block (FabricBlockSettings.of (Material.STONE).strength (0.6f, 12.f).build());

    public void initialize () {
        // Ore
        register (AlchymReference.Blocks.NITER_BEARING_STONE.getName (), niterBearingStone);

        // Glasswares
        register (AlchymReference.Blocks.COPPER_CRUCIBLE.getName (), copperCrucible);
        register (AlchymReference.Blocks.CHYMICAL_ALEMBIC.getName (), alembic);

        // Material enumerations
        for (AlchymReference.Materials material : AlchymReference.Materials.values ()) {
            if (material.forms == null)
                continue;

            for (AlchymReference.Materials.Forms form : material.forms) {
                if (form.isBlock ())
                    register (material.getName () + "_block",
                            new MaterialBlock (Block.Settings.of (Material.METAL), material, form));
            }
        }
    }

    @Override
    void preRegister (String id, Block obj) {
        String identifier = id;

        Item.Settings blockItemSettings = AlchymReference.DEFAULT_ITEM_SETTINGS;

        if (obj instanceof MaterialBlock)
            identifier = id + "_block";

        if (obj instanceof AlchymBlock)
            blockItemSettings = ((AlchymBlock) obj).blockItemSettings ();

        alchym.items.queueBlockItem (identifier,
                new BlockItem (obj, blockItemSettings));
    }
}
