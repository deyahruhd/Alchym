package jard.alchym.mixin;

import jard.alchym.Alchym;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/***
 *  RevolverPlayerAnimMixin
 *  TODO: Add a description for this code
 *
 *  Created by jard at 7:36 AM on April 18, 2019.
 ***/
@Mixin (BipedEntityModel.class)
public class RevolverPlayerAnimMixin<T extends LivingEntity> extends EntityModel<T> implements ModelWithArms, ModelWithHead {
    @Shadow
    public Cuboid rightArm;
    @Shadow
    public Cuboid leftArm;
    @Shadow
    public Cuboid head;

    @Shadow
    public void setArmAngle (float var1, Arm var2) {}

    @Shadow
    public Cuboid getHead () { return null; }

    @Inject (method = "method_17087", at = @At ("RETURN"))
    public void method_17087(T entity, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6,
                             CallbackInfo info) {
        ItemStack mainItem = entity.getItemsHand ().iterator ().next ();
        if (mainItem != null && ! mainItem.isEmpty () && mainItem.getItem () == Alchym.content ().items.revolver) {
            Cuboid sel = entity.getMainArm() == Arm.LEFT ? leftArm : rightArm;

            sel.pitch = head.pitch - 90 * 0.017453292F;
            sel.yaw   = head.yaw;
        }
    }
}
