package com.ricky.totem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EndermanModelのMixin
 * 「Minnie」という名前のエンダーマンをプレイヤーモデルでレンダリング
 */
@Mixin(EndermanModel.class)
public abstract class EndermanModelMixin<T extends LivingEntity> {

    @Unique
    private static PlayerModel<LivingEntity> totem$playerModel;

    @Unique
    private static boolean totem$initialized = false;

    @Unique
    private boolean totem$isMinnie = false;

    @Unique
    private static void totem$ensurePlayerModelInitialized() {
        if (totem$initialized) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getEntityModels() != null) {
            totem$playerModel = new PlayerModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
            totem$initialized = true;
        }
    }

    /**
     * setupAnimの開始時に、Minnieかどうかをチェック
     * EndermanModel<T extends LivingEntity> なのでLivingEntity型を使用
     */
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("HEAD"))
    private void onSetupAnimStart(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        totem$isMinnie = false;

        if (entity instanceof EnderMan enderman) {
            if (enderman.hasCustomName() && "Minnie".equals(enderman.getCustomName().getString())) {
                totem$isMinnie = true;
                totem$ensurePlayerModelInitialized();

                // プレイヤーモデルのアニメーションを設定
                if (totem$playerModel != null) {
                    totem$playerModel.young = false;
                    totem$playerModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                }
            }
        }
    }

    /**
     * renderToBufferをインターセプトして、Minnieの場合はプレイヤーモデルでレンダリング
     * renderToBufferはModelクラスに定義されている
     */
    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRenderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (totem$isMinnie && totem$playerModel != null) {
            poseStack.pushPose();
            // エンダーマンのサイズに合わせてスケール調整
            poseStack.scale(0.9F, 0.9F, 0.9F);
            poseStack.translate(0, 0.2F, 0);
            totem$playerModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
