package jard.alchym;

import jard.alchym.api.ingredient.SolubleIngredient;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/***
 *  AlchymReference
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
        NITER_RICH_ROCK,

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
        ALCHYMIC_REFERENCE,
        REVOLVER,
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

    public enum Sounds {
        REVOLVER_FIRE (new Identifier (MODID, "item.revolver.fire")),
        HITSOUND_1 (new Identifier (MODID, "misc.hitsound.1")),
        HITSOUND_2 (new Identifier (MODID, "misc.hitsound.2")),
        HITSOUND_3 (new Identifier (MODID, "misc.hitsound.3")),
        HITSOUND_4 (new Identifier (MODID, "misc.hitsound.4")),

        TRANSMUTE_DRY (new Identifier (MODID, "transmute.dry"));

        public final Identifier location;

        Sounds (Identifier loc) {
            location = loc;
        }

        public String getRegistryId () {
            return location.getPath ();
        }
    }

    private static final Map <Object, AdditionalMaterials> existingSpeciesMaterials = new HashMap <> ();

    public static AdditionalMaterials getExistingSpeciesMaterial (Object species) {
        return existingSpeciesMaterials.getOrDefault (species, null);
    }

    public interface IMaterial {
        public String getName ();
    }

    public enum Materials implements IMaterial {
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
            BLOCK (CorrespondingItem.BLOCK, 1000, 9),
            INGOT (CorrespondingItem.ITEM, 360, 9),
            NUGGET (CorrespondingItem.ITEM, 40, 9),
            POWDER (CorrespondingItem.ITEM, 360, 4),
            REAGENT_POWDER (CorrespondingItem.ITEM, 360, 4),
            SMALL_POWDER (CorrespondingItem.ITEM, 90, 4),
            REAGENT_SMALL_POWDER (CorrespondingItem.ITEM, 90, 4),
            CRYSTAL (CorrespondingItem.ITEM, 500, 1),
            LIQUID (CorrespondingItem.LIQUID, -1, 1);

            Forms (CorrespondingItem correspondingItem, long volume, int conversionFactor) {
                this.correspondingItem = correspondingItem;
                this.volume = volume;
                this.conversionFactor = conversionFactor;
            }

            private final CorrespondingItem correspondingItem;

            public final long volume;
            public final int conversionFactor;

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

        public String getName ( ) { return name ().toLowerCase ().replace ("_powder", ""); }
    } //$

    public enum AdditionalMaterials implements IMaterial {
        WATER (Fluids.WATER);

        AdditionalMaterials (Object outer) {
            existingSpeciesMaterials.put (outer, this);
        }

        public String getName () { return name ().toLowerCase (); }
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

    // TODO: Move this to configuration file
    public static final double DRY_TRANSMUTATION_RADIUS = 4.00;

    public enum GlassContainers {
        VAT (1000 * 100, Block.createCuboidShape (1, 0, 1, 15, 13, 15), true);

        public final long capacity;
        public final VoxelShape boundingBox;
        public final boolean transmutationCapable;

        GlassContainers (long capacity, VoxelShape boundingBox, boolean transmutationCapable) {
            this.capacity = capacity;
            this.boundingBox = boundingBox;
            this.transmutationCapable = transmutationCapable;
        }
    }

    public enum FluidSolubilities {
        WATER (
                Fluids.WATER, 1.00f,
                Pair.of (Materials.NITER, (int) Materials.Forms.POWDER.volume * 2)),
        LAVA (
                Fluids.LAVA, 2.80f
        );

        public final Fluid fluid;
        public final float density;
        private final Map<IMaterial, Integer> solubilities;

        @SafeVarargs
        FluidSolubilities (Fluid fluid, float density, Pair <IMaterial, Integer> ... solubilitiesArgs) {
            this.fluid = fluid;
            this.density = density;

            if (solubilitiesArgs == null)
                solubilities = null;
            else {
                HashMap<IMaterial, Integer> toMap = new HashMap<> ();
                for (Pair <IMaterial, Integer> entry : solubilitiesArgs) {
                    toMap.put (entry.getKey (), entry.getValue ());
                }
                solubilities = Collections.unmodifiableMap (toMap);
            }
        }

        int getSolubility (IMaterial material) {
            return solubilities.getOrDefault (material, 0);
        }

        public static int getSolubility (Fluid fluid, SolubleIngredient solute) {
            if (solute.getMaterial () == null)
                return 0;

            for (FluidSolubilities solubility : FluidSolubilities.values ()) {
                if (solubility.fluid.equals (fluid))
                    return solubility.getSolubility (solute.getMaterial ());
            }

            // No entry was found that matches these. If it turns out that solute is a fluid and the input fluid is an SolubleIngredient,
            // we can then scan for the reverse scenario, with the caveat that we must normalize the resulting solubility so that
            // it describes the volume of fluid that can dissolve in solute.
            if (fluid instanceof SolubleIngredient && ((SolubleIngredient) fluid).getMaterial () != null && solute instanceof Fluid) {
                long val = 0;
                for (FluidSolubilities solubility : FluidSolubilities.values ()) {
                    if (solubility.fluid.equals (fluid))
                        val = solubility.getSolubility (((SolubleIngredient) fluid).getMaterial ());
                }

                if (val == -1)
                    return -1;
                else {
                    // val mB of solute / 1000 mB of solvent
                    // -> 1000 mB of solvent / val mB of solute
                    // -> 1000/val mB of solvent / 1 mB of solute
                    // -> 1000000/val mB of solvent / 1000 mB of solute

                    return (int) (1000000.f / (float) val);
                }
            }

            return 0;
        }
    }

    public enum Packets {
        OPEN_GUIDEBOOK (new Identifier (MODID, "open_guidebook"), PacketPath.S2C);

        enum PacketPath {
            C2S,
            S2C
        }

        public final Identifier id;
        final PacketPath path;

        Packets (Identifier id, PacketPath path) {
            this.id = id;
            this.path = path;
        }

        public boolean isClientbound () {
            return path == PacketPath.S2C;
        }

        public boolean isServerbound () {
            return path == PacketPath.C2S;
        }
    }

    public static class PageInfo {
        public enum BookSide {
            LEFT,
            RIGHT
        }

        public static final int PAGE_WIDTH = 110;
        public static final int BODY_TEXT_COLOR = 0xff230005;
    }
}
