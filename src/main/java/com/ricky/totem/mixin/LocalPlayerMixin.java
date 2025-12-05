package com.ricky.totem.mixin;

import com.ricky.totem.client.TotemTextureTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * LocalPlayerのMixin
 * エンティティイベント35（トーテムアニメーション）を処理し、
 * カスタムトーテムのテクスチャを使用するように変更
 *
 * LocalPlayerはLivingEntityのサブクラスで、handleEntityEventをオーバーライドしているため、
 * LivingEntityへのMixinでは処理できない
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    /**
     * handleEntityEventの最初に呼び出され、event 35の場合にカスタム処理を行う
     */
    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void onHandleEntityEvent(byte id, CallbackInfo ci) {
        if (id == 35) {
            // カスタムトーテムのテクスチャが設定されているか確認
            ResourceLocation customItemId = TotemTextureTracker.consumeCustomTotemTexture();
            if (customItemId != null) {
                LocalPlayer self = (LocalPlayer) (Object) this;

                // カスタムアイテムのItemStackを取得
                ItemStack customStack = TotemTextureTracker.getItemStackForTexture(customItemId);
                if (customStack != null && !customStack.isEmpty()) {
                    // パーティクルを表示
                    self.level().addParticle(
                            ParticleTypes.TOTEM_OF_UNDYING,
                            self.getX(),
                            self.getY() + 1.0,
                            self.getZ(),
                            0.0, 0.0, 0.0
                    );

                    // アニメーションを表示
                    Minecraft minecraft = Minecraft.getInstance();
                    minecraft.gameRenderer.displayItemActivation(customStack);

                    // サウンドを再生
                    if (!self.isSilent()) {
                        self.level().playLocalSound(
                                self.getX(), self.getY(), self.getZ(),
                                SoundEvents.TOTEM_USE, self.getSoundSource(),
                                1.0F, 1.0F, false
                        );
                    }

                    // バニラの処理をキャンセル
                    ci.cancel();
                }
            }
        }
    }
}
