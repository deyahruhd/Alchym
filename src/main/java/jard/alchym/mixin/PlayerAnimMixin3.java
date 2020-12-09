package jard.alchym.mixin;

import jard.alchym.client.ExtraPlayerDataAccess;
import jard.alchym.helper.MathHelper;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin (PlayerEntityRenderer.class)
public abstract class PlayerAnimMixin3 extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerAnimMixin3 (EntityRenderDispatcher entityRenderDispatcher, PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super (entityRenderDispatcher, entityModel, f);
    }

    @Inject (method = "render", at = @At ("HEAD"))
    public void renderHead(AbstractClientPlayerEntity player, float f, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider
        vertexConsumerProvider, int i, CallbackInfo info) {

        Vec3d previousVel = ((ExtraPlayerDataAccess) player).getPrevVel ();
        Vec3d vel = MathHelper.lerp (previousVel, player.getVelocity (), partialTicks).multiply (1.0, 0.0, 1.0);

        Vec3d right = vel.crossProduct (new Vec3d (0.0, 1.0, 0.0)).normalize ();
        Vector3f axis = new Vector3f (right);

        matrixStack.multiply(new Quaternion (axis, (float) Math.tanh (vel.length () * 0.75) * -75.f, true));
    }
}
