package com.ricky.totem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ricky.totem.client.renderer.CustomEntityRendererManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ChickenRendererのMixin
 * 「Donald」という名前の鶏をプレイヤーモデルでレンダリング
 */
@Mixin(ChickenRenderer.class)
public abstract class ChickenRendererMixin {

    /**
     * renderメソッドをインターセプト（親クラスMobRendererのメソッド）
     * 鶏の名前が「Donald」の場合、プレイヤーモデルでレンダリング
     */
    @Inject(method = "render(Lnet/minecraft/world/entity/Mob;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRender(Mob mob, float entityYaw, float partialTicks, PoseStack poseStack,
                          MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (mob instanceof Chicken chicken && CustomEntityRendererManager.isDonald(chicken)) {
            // プレイヤーモデルでレンダリング（鶏は小さいのでスケール0.5）
            CustomEntityRendererManager.renderAsPlayer(
                    chicken, partialTicks, poseStack, buffer, packedLight,
                    CustomEntityRendererManager.DONALD_TEXTURE, 0.5F
            );
            // デフォルトのレンダリングをキャンセル
            ci.cancel();
        }
    }
}
