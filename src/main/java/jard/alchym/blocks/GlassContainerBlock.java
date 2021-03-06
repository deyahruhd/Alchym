package jard.alchym.blocks;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.blockentities.GlassContainerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;

/***
 *  GlassContainerBlock
 *  A generic solution container block which is instantiated with a capacity and a list of actions it can perform on its contents.
 *
 *  Created by jard at 12:43 PM on January 17, 2019.
 ***/
public class GlassContainerBlock extends BlockWithEntity {
    private final long capacity;
    private final VoxelShape boundingBox;
    private final boolean transmutationCapable;

    public GlassContainerBlock (Settings settings, AlchymReference.GlassContainers container) {
        super (settings);
        this.capacity = container.capacity;
        this.boundingBox = container.boundingBox;
        this.transmutationCapable = container.transmutationCapable;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity (BlockView var1) {
        return new GlassContainerBlockEntity (capacity, transmutationCapable);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) { return true; }

    @Override
    public ActionResult onUse (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.PASS;

        ItemStack heldItem = player.getEquippedStack (hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        if (!heldItem.isEmpty ()
                && world.getBlockEntity (pos) instanceof GlassContainerBlockEntity
                && ((GlassContainerBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).canAccept (heldItem)) {
            player.setStackInHand (hand,
                    ((GlassContainerBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).insertHeldItem (state, world, pos, player,
                            heldItem)
            );
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced (BlockState state, World world, BlockPos pos, BlockState state2, boolean b) {
        if (state.getBlock () != state.getBlock ()) {
            BlockEntity blockEntity = world.getBlockEntity (pos);
            if (blockEntity instanceof GlassContainerBlockEntity) {
                ItemScatterer.spawn(world, pos, ((GlassContainerBlockEntity) blockEntity).getDrops ());
            }

            super.onStateReplaced (state, world, pos, state2, b);
        }
    }

    @Override
    public VoxelShape getRaycastShape (BlockState state, BlockView view, BlockPos pos) {
        return boundingBox;
    }

    @Override
    public VoxelShape getCullingShape (BlockState state, BlockView view, BlockPos pos) {
        return boundingBox;
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView view, BlockPos pos) {
        return boundingBox;
    }
}
