package jard.alchym.init;

import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.blocks.AlchymBlock;
import jard.alchym.blocks.ChymicalContainerBlock;
import jard.alchym.blocks.MaterialBlock;
import jard.alchym.blocks.MaterialFluidBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/***
 *  InitBlocks.java
 *  The initializing module that initializes every item in the mod.
 *
 *  Created by jard at 12:05 AM on December 21, 2018.
 ***/
public class InitBlocks extends InitAbstract <Block> {
    public final Block copperCrucible = new ChymicalContainerBlock (FabricBlockSettings.of (Material.METAL).strength (3.0f, 0.75f).build (),
            AlchymReference.ChymicalContainers.COPPER_CRUCIBLE);

    public final Block alembic = new ChymicalContainerBlock (FabricBlockSettings.of (Material.GLASS)
            .strength (1.0f, 0.5f).nonOpaque ().build (),
            AlchymReference.ChymicalContainers.CHYMICAL_ALEMBIC);

    private static final Map<Pair <AlchymReference.Materials, AlchymReference.Materials.Forms>, FluidBlock> materialFluids = new LinkedHashMap<> ();
    public FluidBlock getFluidBlock (AlchymReference.Materials material) {
        return materialFluids.get (Pair.of (material, AlchymReference.Materials.Forms.LIQUID));
    }
    final void queueFluidBlock (String id, FluidBlock block) {
        if (block instanceof MaterialFluidBlock)
            materialFluids.put (Pair.of (((MaterialFluidBlock) block).material, AlchymReference.Materials.Forms.LIQUID), block);
    }


    InitBlocks (InitAlchym alchym) {
        super (Registry.BLOCK, alchym);
    }

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

        // Fluid blocks
        for (Map.Entry <Pair <AlchymReference.Materials, AlchymReference.Materials.Forms>, FluidBlock> e : materialFluids.entrySet ()) {
            String name = e.getKey ().getLeft ().getName ();

            register (name, e.getValue ());
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

        if (obj instanceof FluidBlock)
            return;

        alchym.items.queueBlockItem (identifier,
                new BlockItem (obj, blockItemSettings));
    }
}
