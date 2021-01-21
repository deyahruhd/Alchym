package jard.alchym.mixin;

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

@Mixin (PlayerEntity.class)
public abstract class PlayerAnimMixin1 extends LivingEntity implements ExtraPlayerDataAccess {
    Vec3d previousVel = Vec3d.ZERO;

    protected PlayerAnimMixin1 (EntityType<? extends LivingEntity> entityType, World world) {
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
