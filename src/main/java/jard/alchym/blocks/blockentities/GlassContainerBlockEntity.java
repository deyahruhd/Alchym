package jard.alchym.blocks.blockentities;

import io.github.prospector.silk.fluid.FluidInstance;
import jard.alchym.Alchym;
import jard.alchym.api.recipe.FluidInstanceIngredient;
import jard.alchym.api.recipe.Ingredient;
import jard.alchym.api.recipe.IngredientGroup;
import jard.alchym.api.recipe.ItemStackIngredient;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/***
 *  GlassContainerBlockEntity.java
 *  The corresponding BlockEntity for a GlassContainerBlock.
 *
 *  Created by jard at 2:17 PM on January 17, 2019.
 ***/
public class GlassContainerBlockEntity extends BlockEntity {
    private IngredientGroup contents;

    public GlassContainerBlockEntity () {
        super (Alchym.content ().blockEntities.glassContainerBlockEntity);

        contents = new IngredientGroup ();
    }

    public ItemStack insertHeldItem (BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack item) {
        ItemStack ret = ItemStack.EMPTY;

        if (isLiquidContainer (item)) {
            ret = new ItemStack (Items.BUCKET);

            insertFluid (new FluidInstance (getFluidFromBucket (item.getItem ()), 1000));
        } else
            contents.addStack (new ItemStackIngredient (item));

        return ret;
    }

    public FluidInstance insertFluid (FluidInstance instance) {
        Ingredient ingredient = new FluidInstanceIngredient (instance);
        contents.addStack (ingredient);

        return null;
    }

    boolean isLiquidContainer (ItemStack item) {
        return item.getItem () instanceof BucketItem;
    }

    Fluid getFluidFromBucket (Item bucket) {
        if (bucket == Items.WATER_BUCKET)
            return Fluids.WATER;
        else if (bucket == Items.LAVA_BUCKET)
            return Fluids.LAVA;
        else
            return null;
    }

    public DefaultedList <ItemStack> getDrops () {
        return contents.getDroppableStacks ();
    }
}
