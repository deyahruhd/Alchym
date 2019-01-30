package jard.alchym.blocks;

import jard.alchym.blocks.blockentities.GlassContainerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/***
 *  GlassContainerBlock.java
 *  A generic solution container block which is instantiated with a capacity and a list of actions it can perform on its contents.
 *
 *  Created by jard at 12:43 PM on January 17, 2019.
 ***/
public class GlassContainerBlock extends BlockWithEntity {

    private final VoxelShape boundingBox;


    public GlassContainerBlock (Settings settings, VoxelShape boundingBox) {
        super (settings);
        this.boundingBox = boundingBox;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity (BlockView var1) {
        return new GlassContainerBlockEntity ();
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) { return true; }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return true;

        ItemStack heldItem = player.getEquippedStack (hand == Hand.MAIN ? EquipmentSlot.HAND_MAIN : EquipmentSlot.HAND_OFF);

        if (!heldItem.isEmpty () && world.getBlockEntity (pos) != null) {
            player.setEquippedStack (hand == Hand.MAIN ? EquipmentSlot.HAND_MAIN : EquipmentSlot.HAND_OFF,
                    ((GlassContainerBlockEntity) world.getBlockEntity (pos)).insertHeldItem (state, world, pos, player,
                            heldItem)
            );
        }

        return true;
    }

    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState blockState2, boolean b) {
        if (state.getBlock () != blockState2.getBlock ()) {
            BlockEntity blockEntity = world.getBlockEntity (pos);
            if (blockEntity instanceof GlassContainerBlockEntity) {
                ItemScatterer.spawn(world, pos, ((GlassContainerBlockEntity) blockEntity).getDrops ());
            }

            super.onBlockRemoved (state, world, pos, blockState2, b);
        }
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
        return boundingBox;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, VerticalEntityPosition vertPos) {
        return boundingBox;
    }
}
