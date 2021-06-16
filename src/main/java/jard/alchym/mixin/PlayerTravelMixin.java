package jard.alchym.mixin;

import jard.alchym.helper.MovementHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/***
 *  PlayerTravelMixin
 *  Mixes in Quake movement physics into the player movement logic.
 *
 *  Created by jard at 23:51 on June, 15, 2021.
 ***/
@Mixin (PlayerEntity.class)
public abstract class PlayerTravelMixin extends LivingEntity {
    private static final float WALKSPEED = MovementHelper.upsToSpt (320.f);
    private static final float AIRSPEED  = MovementHelper.upsToSpt (240.f);
    private static final float STOPSPEED = MovementHelper.upsToSpt (100.f);


    private static final float AIRSTRAFE_SPEED = MovementHelper.upsToSpt (60.f);

    private static final float GROUND_ACCEL    = 12.5f / 20.f;
    private static final float AIR_ACCEL       = 0.75f / 20.f;
    private static final float AIRSTRAFE_ACCEL = 9.0f  / 20.f;
    private static final float FRICTION        = 4.5f  / 20.f;

    private static boolean wasOnGround = true;
    private static int skimTimer = 20;

    @Shadow
    @Final
    public PlayerAbilities abilities;

    @Shadow
    public void increaseTravelMotionStats(double d, double e, double f) {}

    protected PlayerTravelMixin (EntityType<? extends LivingEntity> entityType, World world) {
        super (entityType, world);
    }

    @Inject (method = "travel", at = @At ("HEAD"), cancellable = true)
    public void hookTravel (Vec3d movementIn, CallbackInfo info) {
        if (!world.isClient)
            return;

        if (this.isSwimming () && !this.hasVehicle ())
            return;

        if (this.abilities.flying && !this.hasVehicle ())
            return;

        setSprinting (false);

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        double prevX = getX ();
        double prevY = getY ();
        double prevZ = getZ ();

        Vec3d wishDir = MovementHelper.getWishDir (player.getYaw (0.f), movementIn);


        if (player.isOnGround () && ! wasOnGround)
            skimTimer = 0;

        wasOnGround = player.isOnGround ();
        skimTimer ++;

        if (quakeMovement (player, wishDir)) {
            Vec3d preSkimVel = getVelocity ();


            player.move (MovementType.SELF, player.getVelocity ());

            if (player.isOnGround ())
                preSkimVel = preSkimVel.multiply (1.f, 0.f, 1.f);

            if (skimTimer <= 5)
                player.setVelocity (preSkimVel);
        } else
            super.travel(wishDir);


        method_29242 (this, this instanceof Flutterer);

        increaseTravelMotionStats (getX () - prevX, this.getY () - prevY, this.getZ () - prevZ);

        info.cancel ();
    }

    @Inject (method = "jump", at = @At ("HEAD"))
    public void stopSprintJump (CallbackInfo info) {
        setSprinting (false);
    }

    private boolean quakeMovement (ClientPlayerEntity player, Vec3d wishDir) {
        if (player.isOnGround () && ! jumping)
            playerWalkMove (player, wishDir);
        else
            playerAirMove (player, wishDir);

        player.setVelocity (player.getVelocity ().multiply (1.f, 0.9800000190734863f, 1.f));

        return true;
    }

    private void playerWalkMove (ClientPlayerEntity player, Vec3d wishDir) {
        float multiplier = 1.f;
        if (player.isSneaking ())
            multiplier = 0.25f;

        player.addVelocity (0.f, -0.0784f, 0.f);
        MovementHelper.playerFriction (player, FRICTION, (wishDir.lengthSquared () > 0.f ? STOPSPEED : WALKSPEED) * multiplier);

        float accel = GROUND_ACCEL;
        // TODO: Apply slick and ground accel whenever the player receives knockback.

        MovementHelper.playerAccelerate (player, wishDir, WALKSPEED * multiplier, accel);
    }

    private void playerAirMove (ClientPlayerEntity player, Vec3d wishDir) {
        player.addVelocity (0.f, player.getVelocity ().y > 0.f ? -0.0584f : -0.0784f, 0.f);

        float multiplier = 1.f;
        if (player.isSneaking ())
            multiplier = 0.25f;

        MovementHelper.playerAccelerate (player, wishDir, AIRSPEED * multiplier, AIR_ACCEL);
        MovementHelper.playerAccelerate (player, wishDir, AIRSTRAFE_SPEED * multiplier, AIRSTRAFE_ACCEL);
    }
}
