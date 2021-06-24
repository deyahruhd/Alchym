package jard.alchym.mixin;

import jard.alchym.helper.MathHelper;
import jard.alchym.helper.MovementHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/***
 *  PlayerTravelMixin
 *  Mixes in Quake movement physics into the player movement logic.
 *
 *  Created by jard at 23:51 on June, 15, 2021.
 ***/
@Mixin (PlayerEntity.class)
public abstract class PlayerTravelMixin extends LivingEntity {
    private static final float WALKSPEED               = MovementHelper.upsToSpt (320.f);
    private static final float STOPSPEED               = MovementHelper.upsToSpt (320.f);
    private static final float AIRSPEED                = MovementHelper.upsToSpt (240.f);
    private static final float AIRSTRAFE_SPEED         = MovementHelper.upsToSpt (40.f);

    private static final float CROUCH_SLIDE_MIN_SPEED  = MovementHelper.upsToSpt (415.f);

    private static final float GROUND_ACCEL            = 9.5f / 20.f;
    private static final float AIR_ACCEL               = 0.75f / 20.f;
    private static final float AIRSTRAFE_ACCEL         = 12.0f  / 20.f;
    private static final float FRICTION                = 3.5f  / 20.f;

    private static boolean wasOnGround = true;
    private static Vec3d [] stepTracker = { Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO };
    private static int skimTimer      = 0;
    private static int gbTimer        = 0;
    private static int rampslideTimer = 0;

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

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        /*
        MinecraftClient client = MinecraftClient.getInstance ();
        int speed = (int) (1600000.d * getVelocity ().multiply (1.f, 0.f, 1.f).length () / 1403.0);
        client.inGameHud.setOverlayMessage (new LiteralText (String.valueOf (speed)), false);
        */


        setSprinting (false);

        Vec3d prevPos = player.getPos ();

        Vec3d wishDir = MovementHelper.getWishDir (player.getYaw (0.f), movementIn);

        if (quakeMovement (player, wishDir)) {
            Vec3d preSkimVel = player.getVelocity ();

            if (isOnGround () && ! wasOnGround)
                skimTimer = 5;

            // Update all tracking variables
            wasOnGround = player.isOnGround ();

            player.move (MovementType.SELF, player.getVelocity ());

            if (player.isOnGround ())
                preSkimVel = preSkimVel.multiply (1.f, 0.f, 1.f);

            if (0 < skimTimer && skimTimer <= 5)
                player.setVelocity (preSkimVel);
        } else
            super.travel(wishDir);

        Vec3d step = getPos ().subtract (prevPos);

        // Shift over all step variables
        for (int i = 0; i < 4; ++ i) {
            stepTracker [i + 1] = stepTracker [i];
        }
        stepTracker [0] = step;

        increaseTravelMotionStats (step.x, step.y, step.z);
        // Move limbs
        method_29242 (this, this instanceof Flutterer);

        if (skimTimer > 0)
            skimTimer --;

        if (gbTimer > 0)
            gbTimer --;

        if (rampslideTimer > 0)
            rampslideTimer --;

        info.cancel ();
    }

    @Inject (method = "jump", at = @At ("HEAD"))
    public void stopSprintJump (CallbackInfo info) {
        setSprinting (false);
    }

    @Inject (method = "clipAtLedge", at = @At ("HEAD"), cancellable = true)
    public void dontClipOnLedgeIfSliding (CallbackInfoReturnable <Boolean> info) {
        if (isSneaking () && getVelocity ().multiply (1.f, 0.f, 1.f).length () >= CROUCH_SLIDE_MIN_SPEED) {
            System.out.println ("Cancelling");
            info.setReturnValue (false);
            info.cancel ();
        }
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
        float walkSpeed = WALKSPEED;
        float frictionSpeed = STOPSPEED;

        float accel = GROUND_ACCEL;
        float frictionAccel = FRICTION;

        if (player.isSneaking () || player.isInSneakingPose ()) {
            walkSpeed *= 0.25f;
            frictionSpeed *= 0.25f;
        }

        if (player.isSneaking () && getVelocity ().multiply (1.f, 0.f, 1.f).length () >= CROUCH_SLIDE_MIN_SPEED) {
            double yStep = stepTracker [0].y;
            if (yStep > MathHelper.FLOAT_ZERO_THRESHOLD && yStep <= player.stepHeight) {
                skimTimer = 5;
                rampslideTimer = 5;
            } else if (skimTimer == 0 || skimTimer >= 5)
                skimTimer = 6;
            frictionAccel *= 0.05f;
            walkSpeed = AIRSPEED;
            accel = AIR_ACCEL;
        }
        // TODO: Apply slick and ground accel whenever the player receives knockback.
        if (0 < gbTimer && gbTimer <= 5) {
            frictionAccel = 0.0f;
            walkSpeed = WALKSPEED;
            accel = GROUND_ACCEL;
        }

        MovementHelper.playerFriction (player, frictionAccel, frictionSpeed);

        player.addVelocity (0.f, -0.0784f, 0.f);

        MovementHelper.playerAccelerate (player, wishDir, walkSpeed, accel);
    }

    private void playerAirMove (ClientPlayerEntity player, Vec3d wishDir) {
        if (player.isOnGround () && jumping && wasOnGround) {
            playerWalkMove (player, wishDir);
            return;
        }

        if (player.isSneaking ())
            MovementHelper.playerFriction (player, FRICTION * 0.05f, STOPSPEED);

        if (rampslideTimer > 0) {
            Vec3d rampslideDir = getRampslideVector ();
            double playerSpeed = player.getVelocity ().multiply (1.0, 0.0, 1.0).length ();
            double addVertSpeed = playerSpeed * rampslideDir.y / rampslideDir.multiply (1.0, 0.0, 1.0).length ();

            player.addVelocity (0.0, addVertSpeed - (player.getVelocity ().y / 2.0), 0.0);

            skimTimer = 5;
            rampslideTimer = 0;
        }

        player.addVelocity (0.f, player.getVelocity ().y > 0.f ? -0.0524f : -0.0784f, 0.f);

        MovementHelper.playerAccelerate (player, wishDir, AIRSPEED, AIR_ACCEL);
        MovementHelper.playerAccelerate (player, wishDir, AIRSTRAFE_SPEED, AIRSTRAFE_ACCEL);
    }

    private Vec3d getRampslideVector () {
        Vec3d sum = Vec3d.ZERO;
        for (int i = 0; i < 5; ++ i) {
            sum = sum.add (stepTracker [i]);;
        }
        sum.multiply (0.2);

        return sum.normalize ();
    }
}