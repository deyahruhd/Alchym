package jard.alchym.helper;

import com.google.common.base.Predicates;
import jard.alchym.api.transmutation.ReagentItem;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/***
 *  TransmutationHelper
 *  Contains various helper methods relating to transmutation (e.g. handling of dry transmutation or item entity raytracing methods)
 *
 *  Created by jard at 5:18 PM on February 13, 2018.
 ***/
public class TransmutationHelper {
    public static void useReagentForTransmutation (World world, PlayerEntity player, ItemStack reagent) {
        if (! isReagent (reagent))
            return;

        ItemEntity itemEntity = getLookedAtItem (player, 1.f);

        if (itemEntity != null) {
            long reagentCharge = getReagentCharge (reagent);

            System.out.println ("Reagent charge is " + reagentCharge);

        }
    }

    public static boolean isReagent (ItemStack reagent) {
        return ! reagent.isEmpty() && reagent.getItem () instanceof ReagentItem && ((ReagentItem) reagent.getItem()).isReagent();
    }

    public static long getReagentCharge (ItemStack reagent) {
        if (! isReagent (reagent))
            return 0L;

        return ((ReagentItem) reagent.getItem ()).getUnitCharge() * reagent.getCount();
    }

    // Returns the ItemEntity that a player may be looking at, or null if the player is not looking at any ItemEntity.
    public static ItemEntity getLookedAtItem (PlayerEntity player, float partialTicks) {
        ItemEntity item = null;

        if (player != null && player.world != null) {
            double reach = player.getAttributeInstance
                    (com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.ATTACK_RANGE).getValue ();
            boolean flag = false;
            if (player.isCreative ())
                reach = 6.0D;
            else
                flag = reach > 3.0D;

            double reachSq = reach * reach;

            Vec3d pos  = player.getCameraPosVec (partialTicks);
            Vec3d look = player.getRotationVec (1.0F).multiply (reach);
            Box box = player.getBoundingBox ().stretch(look.multiply (reach)).expand(1.0D, 1.0D, 1.0D);
            EntityHitResult hitResult = ProjectileUtil.rayTrace (player, pos, pos.add (look), box, (entity) ->
                    entity instanceof ItemEntity, reachSq);
            if (hitResult != null) {
                ItemEntity entity = (ItemEntity) hitResult.getEntity ();
                double dist = pos.squaredDistanceTo (hitResult.getPos ());
                if (! (flag && dist > 9.0D) && dist < reachSq)
                    item = entity;
            }
        }

        return item;
    }
}