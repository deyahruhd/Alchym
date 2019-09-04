package jard.alchym.items;

import jard.alchym.api.transmutation.ReagentItem;
import jard.alchym.helper.TransmutationHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/***
 *  TransmutableReagentItem
 *  Base class for items that are used as transmutation reagents (niter and the Philosopher's Stone)
 *
 *  Created by jard at 10:54 PM on February 18, 2018.
 ***/
public abstract class TransmutableReagentItem extends Item implements ReagentItem {
    public TransmutableReagentItem (Settings item$Settings_1) {
        super (item$Settings_1);
    }

    @Override
    public TypedActionResult<ItemStack> use (World world, PlayerEntity player, Hand hand) {
        if (! world.isClient) {
            TransmutationHelper.useReagentForTransmutation (world, player, player.getStackInHand (hand));
        }

        return new TypedActionResult(ActionResult.PASS, player.getStackInHand (hand));
    }
}
