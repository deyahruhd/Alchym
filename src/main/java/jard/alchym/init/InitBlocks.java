package jard.alchym.init;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.GlassContainerBlock;
import jard.alchym.blocks.MaterialBlock;
import jard.alchym.items.MaterialItem;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.block.BlockItem;
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

    public final Block vatBlock = new GlassContainerBlock (FabricBlockSettings.of (Material.GLASS).strength (1.0f, 0.5f).build (),
                                                           Block.createCubeShape (1, 0, 1, 15, 13, 15));

    public void initialize () {
        register (AlchymReference.Blocks.VAT_CONTAINER.getName (), vatBlock);

        for (AlchymReference.Materials material : AlchymReference.Materials.values ()) {
            if (material.forms == null)
                continue;

            for (AlchymReference.Materials.Forms form : material.forms) {
                if (form.isBlock ())
                    register (material.getName (),
                            new MaterialBlock (Block.Settings.of (Material.METAL), material, form));
            }
        }
    }

    @Override
    void preRegister (String id, Block obj) {
        alchym.items.queueBlockItem (id + "_block",
                new BlockItem (obj, alchym.items.DEFAULT_ITEM_SETTINGS));
    }
}
