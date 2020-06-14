package jard.alchym.mixin;

import jard.alchym.Alchym;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/***
 *  RevolverPlayerAnimMixin
 *  Mixin which hooks into the player model rendering code to point the player's arm forward if they are holding a
 *  Chymical Revolver.
 *
 *  Created by jard at 7:36 AM on April 18, 2019.
 ***/
@Mixin (BipedEntityModel.class)
public abstract class RevolverPlayerAnimMixin<T extends LivingEntity> extends EntityModel<T> implements ModelWithArms,
        ModelWithHead {
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftArm;
    @Shadow
    public ModelPart head;

    @Shadow
    public ModelPart getHead () { return null; }

    @Inject (method = "setAngles", at = @At ("RETURN"))
    public void setAngles(T entity, float float_1, float float_2, float float_3, float float_4, float float_5,
                             CallbackInfo info) {
        ItemStack mainItem = entity.getItemsHand ().iterator ().next ();
        if (mainItem != null && ! mainItem.isEmpty () && mainItem.getItem () == Alchym.content ().items.revolver) {
            ModelPart sel = entity.getMainArm() == Arm.LEFT ? leftArm : rightArm;

            sel.pitch = head.pitch - 90 * 0.017453292F;
            sel.yaw   = head.yaw;
        }
    }
}
