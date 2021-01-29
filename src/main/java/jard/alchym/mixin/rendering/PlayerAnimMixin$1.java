package jard.alchym.mixin.rendering;

import jard.alchym.client.ExtraPlayerDataAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/***
 *  PlayerAnimMixin$1
 *  Adds an additional previous velocity variable to player entities, which are then ticked at the beginning
 *  of every movement logic tick, to permit smooth interpolation of the velocity for rendering purposes.
 *
 *  Created by jard at 19:32 on January, 02, 2021.
 ***/
@Mixin (PlayerEntity.class)
public abstract class PlayerAnimMixin$1 extends LivingEntity implements ExtraPlayerDataAccess {
    Vec3d previousVel = Vec3d.ZERO;

    protected PlayerAnimMixin$1 (EntityType<? extends LivingEntity> entityType, World world) {
        super (entityType, world);
    }

    @Inject (method = "tickMovement", at = @At ("HEAD"))
    private void tickMovement (CallbackInfo info) {
        previousVel = getVelocity ();
    }

    public Vec3d getPrevVel () {
        return previousVel;
    }
}
