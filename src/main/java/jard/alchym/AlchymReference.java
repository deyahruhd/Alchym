package jard.alchym;

import jard.alchym.items.ISoluble;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/***
 *  AlchymReference.java
 *  Central repository for all constant values used by the mod. Stores block and item identifiers, material
 *  types, entities, and such.
 *
 *  Created by jard at 9:15 PM on December 20, 2018.
 ***/
public class AlchymReference {
    public static final String MODID = "alchym";

    public static final ItemGroup ALCHYM_GROUP = FabricItemGroupBuilder.build (
            new Identifier (MODID, "main"),
            () -> new ItemStack (Alchym.getPhilosophersStone ())
    );

    // Any non-material block should have its definitions placed here.
    public enum Blocks {
        VAT_CONTAINER,
        FLASK_CONTAINER;

        public String getName () {
            return name ().toLowerCase ();
        }
    }

    public enum BlockEntities {
        GLASS_CONTAINER;

        public String getName () {
            return name ().toLowerCase ();
        }
    }

    // Any non-material item should have its definitions placed here.
    public enum Items {
        PHILOSOPHERS_STONE;

        public String getName () {
            return name ().toLowerCase ();
        }
    }

    public enum Reagents {
        UNKNOWN,
        NITER,
        PHILOSOPHERS_STONE
    }

    public enum Materials {
        // Metals
        ALCHYMIC_GOLD (Forms.BLOCK, Forms.INGOT, Forms.NUGGET, Forms.POWDER, Forms.SMALL_POWDER),
        ALCHYMIC_SILVER (Forms.BLOCK, Forms.INGOT, Forms.NUGGET, Forms.POWDER, Forms.SMALL_POWDER),
        ALCHYMIC_STEEL (Forms.BLOCK, Forms.INGOT, Forms.NUGGET, Forms.POWDER, Forms.SMALL_POWDER),
        COPPER (Forms.BLOCK, Forms.INGOT, Forms.NUGGET, Forms.POWDER, Forms.SMALL_POWDER),
        GOLD (Forms.POWDER, Forms.SMALL_POWDER),
        IRON (Forms.POWDER, Forms.SMALL_POWDER),
        LEAD (Forms.BLOCK, Forms.INGOT, Forms.NUGGET, Forms.POWDER, Forms.SMALL_POWDER),
        MERCURY (Forms.LIQUID),

        // Reagent powders
        NITER (Forms.CRYSTAL, Forms.REAGENT_POWDER, Forms.REAGENT_SMALL_POWDER),
        PROJECTION_POWDER (Forms.REAGENT_POWDER, Forms.REAGENT_SMALL_POWDER);

        public enum Forms {
            /* BLOCK:                   A block of the material.
             *
             * INGOT:                   An ingot of the material.
             *
             * NUGGET:                  A nugget of the material, being 1/9th of a regular ingot.
             *
             * POWDER:                  A powdered form of the material.
             *
             * SMALL_POWDER:            A small powdered form of the material, being 1/4th of regular powder.
             *
             * REAGENT_POWDER:          A powdered form of the material, except that it also overrides
             *                          MaterialItem#isTransmutationReagent to return true.
             *                            * Note: POWDER and REAGENT_POWDER are mutually exclusive, and this is enforced.
             *                              An exception will be raised if a material contains both of these.
             *
             * REAGENT_SMALL_POWDER:    A small powdered form of the material, except that it also overrides
             *                          MaterialItem#isTransmutationReagent to return true.
             *                            * Note: SMALL_POWDER and REAGENT_SMALL_POWDER are mutually exclusive, and this
             *                              is enforced. An exception will be raised if a material contains both of these.
             *
             * CRYSTAL:                 A crystalline form of the material.
             *
             * LIQUID:                  A liquid form of the material.
             */
            BLOCK (CorrespondingItem.BLOCK, 1000),
            INGOT (CorrespondingItem.ITEM, 360),
            NUGGET (CorrespondingItem.ITEM, 40),
            POWDER (CorrespondingItem.ITEM, 360),
            REAGENT_POWDER (CorrespondingItem.ITEM, 360),
            SMALL_POWDER (CorrespondingItem.ITEM, 90),
            REAGENT_SMALL_POWDER (CorrespondingItem.ITEM, 90),
            CRYSTAL (CorrespondingItem.ITEM, 500),
            LIQUID (CorrespondingItem.LIQUID, -1);

            Forms (CorrespondingItem correspondingItem, long volume) {
                this.correspondingItem = correspondingItem;
                this.volume = volume;
            }

            private final CorrespondingItem correspondingItem;

            public final long volume;

            private enum CorrespondingItem {
                BLOCK, ITEM, LIQUID
            }

            public String getName ( ) {
                return name ().toLowerCase ().replace ("reagent_", "");
            }

            public boolean isBlock ( ) {
                return correspondingItem == CorrespondingItem.BLOCK;
            }

            public boolean isItem ( ) {
                return correspondingItem == CorrespondingItem.ITEM;
            }

            public boolean isLiquid ( ) {
                return correspondingItem == CorrespondingItem.LIQUID;
            }
        }

        public final java.util.List<Forms> forms;

        Materials (Forms... formsArgs) {
            if (formsArgs == null)
                forms = null;
            else
                forms = Collections.unmodifiableList (new ArrayList<> (Arrays.asList (formsArgs)));

            if (forms != null &&
                    (forms.contains (Forms.POWDER) || forms.contains (Forms.SMALL_POWDER)) &&
                    (forms.contains (Forms.REAGENT_POWDER) || forms.contains (Forms.REAGENT_SMALL_POWDER)))
                throw new RuntimeException ("The material '" + getName () + "' is in an illegal state: " +
                        "\"contains both a POWDER and REAGENT_POWDER form\"!");
        }

        public String getName ( ) {
            return name ().toLowerCase ().replace ("_powder", "");
        }
    }

    public enum PhilosophersStoneCharges {
        LESSER  (0, 16 * 16 * 16),
        NORMAL  (LESSER.max, LESSER.max + 79 * 79 * 79),
        GREATER (NORMAL.max, NORMAL.max + 80 * 80 * 80);

        public final long min, max;

        PhilosophersStoneCharges (long min, long max) {
            this.min = min;
            this.max = max;
        }
    }

    public enum GlassContainers {
        VAT (1000 * 100, Block.createCubeShape (1, 0, 1, 15, 13, 15));

        public final long capacity;
        public final VoxelShape boundingBox;

        GlassContainers (long capacity, VoxelShape boundingBox) {
            this.capacity = capacity;
            this.boundingBox = boundingBox;
        }
    }

    public enum FluidSolubilities {
        WATER (
                Fluids.WATER,
                Pair.of (Materials.NITER, 360)
        );

        public final Fluid fluid;
        private final Map<Materials, Integer> solubilities;

        @SafeVarargs
        FluidSolubilities (Fluid fluid, Pair <Materials, Integer> ... solubilitiesArgs) {
            this.fluid = fluid;

            if (solubilitiesArgs == null)
                solubilities = null;
            else {
                HashMap<Materials, Integer> toMap = new HashMap<> ();
                for (Pair <Materials, Integer> entry : solubilitiesArgs) {
                    toMap.put (entry.getKey (), entry.getValue ());
                }
                solubilities = Collections.unmodifiableMap (toMap);
            }
        }

        int getSolubility (Materials material) {
            return solubilities.getOrDefault (material, 0);
        }

        public static int getSolubility (Fluid fluid, Materials material) {
            for (FluidSolubilities solubility : FluidSolubilities.values ()) {
                if (solubility.fluid.equals (fluid))
                    return solubility.getSolubility (material);
            }

            return 0;
        }
    }
}