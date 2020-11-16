package jard.alchym.init;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.GlassContainerBlock;
import jard.alchym.blocks.MaterialBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
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
                                                           AlchymReference.GlassContainers.VAT);
    public final Block niterRichRock = new Block (FabricBlockSettings.of (Material.STONE).strength (0.6f, 12.f).build());

    public void initialize () {
        // Ore
        register (AlchymReference.Blocks.NITER_RICH_ROCK.getName (), niterRichRock);

        // Glasswares
        register (AlchymReference.Blocks.VAT_CONTAINER.getName (), vatBlock);

        // Material enumerations
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
        String identifier = id;

        if (obj instanceof MaterialBlock)
            identifier = id + "_block";

        alchym.items.queueBlockItem (identifier,
                new BlockItem (obj, alchym.items.DEFAULT_ITEM_SETTINGS));
    }
}
