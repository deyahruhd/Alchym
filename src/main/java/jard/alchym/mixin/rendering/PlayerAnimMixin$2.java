package jard.alchym.mixin.rendering;

import jard.alchym.Alchym;
import jard.alchym.client.ExtraPlayerDataAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/***
 *  PlayerAnimMixin$2
 *  Responsible for lifting the player's arm when holding a Chymical Revolver, ducking their head based on their
 *  interpolated velocity, and storing their dev cloak.
 *
 *  Created by jard at 19:32 on January, 02, 2021.
 ***/
@Mixin (BipedEntityModel.class)
public abstract class PlayerAnimMixin$2 <T extends LivingEntity> extends EntityModel<T> implements ModelWithArms,
        ModelWithHead, ExtraPlayerDataAccess {
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftArm;
    @Shadow
    public ModelPart torso;
    @Shadow
    public ModelPart head;
    @Shadow
    public ModelPart helmet;

    private ModelPart cloak = null;

    public PlayerAnimMixin$2 (EntityRenderDispatcher entityRenderDispatcher, PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
    }

    @Shadow
    public ModelPart getHead () { return null; }

    @Inject (method = "setAngles", at = @At ("RETURN"))
    public void setAngles(T entity, float float_1, float float_2, float float_3, float float_4, float float_5,
                          CallbackInfo info) {
        if (entity instanceof PlayerEntity) {
            if (cloak == null)
                cloak = getCloak ();

            PlayerEntity player = (PlayerEntity) entity;

            Vec3d look = player.getRotationVec (MinecraftClient.getInstance ().getTickDelta ()).multiply (1.0, 0.0, 1.0);

            Vec3d previousVel = ((ExtraPlayerDataAccess) player).getPrevVel ();

            Vec3d vel = jard.alchym.helper.MathHelper.lerp (previousVel, player.getVelocity (), MinecraftClient.getInstance ().getTickDelta ()).multiply (1.0, 0.0, 1.0);

            double dot = look.dotProduct (vel.normalize ());
            double angle = dot * Math.tanh (vel.length () * 0.75) * -75.0 * Math.PI / 180.0;

            // Set head pitch
            head.pitch += angle;

            ItemStack mainItem = entity.getItemsHand ().iterator ().next ();
            if (mainItem != null && !mainItem.isEmpty () && mainItem.getItem () == Alchym.content ().items.revolver) {
                ModelPart sel = entity.getMainArm () == Arm.LEFT ? leftArm : rightArm;

                // Set arm pitch
                sel.pitch = head.pitch - 90 * 0.017453292F;
                sel.yaw = head.yaw;
            }

            helmet.copyPositionAndRotation (head);
        }
    }

    public ModelPart getCloak () {
        if (cloak == null) {
            cloak = new ModelPart(this, 0, 0);
            cloak.setTextureSize(26, 23);
            cloak.addCuboid(-6.0F, 0.0F, -1.0F, 12.0F, 22.0F, 1.0F, 0.0f);
        }

        return cloak;
    }
}
