package com.ricky.totem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ricky.totem.client.renderer.CustomEntityRendererManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EndermanRendererのMixin
 * 「Minnie」という名前のエンダーマンをプレイヤーモデルでレンダリング
 */
@Mixin(EndermanRenderer.class)
public abstract class EndermanRendererMixin {

    /**
     * renderメソッドをインターセプト（親クラスMobRendererのメソッド）
     * エンダーマンの名前が「Minnie」の場合、プレイヤーモデルでレンダリング
     */
    @Inject(method = "render(Lnet/minecraft/world/entity/Mob;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRender(Mob mob, float entityYaw, float partialTicks, PoseStack poseStack,
                          MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (mob instanceof EnderMan enderman && CustomEntityRendererManager.isMinnie(enderman)) {
            // プレイヤーモデルでレンダリング（エンダーマンは大きいのでスケール0.9）
            CustomEntityRendererManager.renderAsPlayer(
                    enderman, partialTicks, poseStack, buffer, packedLight,
                    CustomEntityRendererManager.MINNIE_TEXTURE, 0.9F
            );
            // デフォルトのレンダリングをキャンセル
            ci.cancel();
        }
    }
}
