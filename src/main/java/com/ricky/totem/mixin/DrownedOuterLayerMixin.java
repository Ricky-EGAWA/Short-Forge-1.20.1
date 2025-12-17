package com.ricky.totem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;

import net.minecraft.world.entity.monster.Drowned;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**
 * DrownedOuterLayerのMixin
 * 「Minnie」という名前のドラウンドの外側レイヤーを非表示にする
 */

@Mixin(DrownedOuterLayer.class)
public abstract class DrownedOuterLayerMixin<T extends Drowned> {
    /**
     * renderメソッドをインターセプト
     * ドラウンドの名前が「Minnie」の場合、外側レイヤーのレンダリングをキャンセル
     */
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/monster/Drowned;FFFFFF)V",
            at = @At("HEAD"),
            cancellable = true)

    private void onRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                          T drowned, float limbSwing, float limbSwingAmount,
                          float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
                          CallbackInfo ci) {
        if (drowned.hasCustomName()) {
            String name = drowned.getCustomName().getString();
            if ("Minnie".equals(name)) {
                ci.cancel();
            }
        }
    }
}