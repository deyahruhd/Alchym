package jard.alchym.blocks;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.blockentities.GlassContainerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/***
 *  GlassContainerBlock
 *  A generic solution container block which is instantiated with a capacity and a list of actions it can perform on its contents.
 *
 *  Created by jard at 12:43 PM on January 17, 2019.
 ***/
public class GlassContainerBlock extends BlockWithEntity {
    private final long capacity;
    private final VoxelShape boundingBox;


    public GlassContainerBlock (Settings settings, AlchymReference.GlassContainers container) {
        super (settings);
        this.capacity = container.capacity;
        this.boundingBox = container.boundingBox;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity (BlockView var1) {
        return new GlassContainerBlockEntity (capacity);
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

        ItemStack heldItem = player.getEquippedStack (hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        if (!heldItem.isEmpty ()
                && world.getBlockEntity (pos) instanceof GlassContainerBlockEntity
                && ((GlassContainerBlockEntity) world.getBlockEntity (pos)).canAccept (heldItem)) {
            player.setEquippedStack (hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND,
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
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return boundingBox;
    }
}
