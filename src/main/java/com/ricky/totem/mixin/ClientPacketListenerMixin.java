package com.ricky.totem.mixin;

import com.ricky.totem.client.TotemTextureTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ClientPacketListenerのMixin
 * エンティティイベント35（トーテムアニメーション）のパケット処理をインターセプトし、
 * カスタムトーテムのテクスチャを使用するように変更
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    /**
     * handleEntityEventの最初に呼び出され、event 35の場合にカスタム処理を行う
     */
    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void onHandleEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (packet.getEventId() == 35) {
            System.out.println("ClientPacketListener mixin called for event 35");

            // カスタムトーテムのテクスチャが設定されているか確認
            ResourceLocation customItemId = TotemTextureTracker.consumeCustomTotemTexture();
            if (customItemId != null) {
                System.out.println("Custom totem texture found: " + customItemId);

                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.level == null) return;

                Entity entity = minecraft.level.getEntity(packet.getEntity(minecraft.level).getId());
                if (entity == null) return;

                // カスタムアイテムのItemStackを取得
                ItemStack customStack = TotemTextureTracker.getItemStackForTexture(customItemId);
                if (customStack != null && !customStack.isEmpty()) {
                    // パーティクルを表示（30回）
                    minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);

                    // ローカルプレイヤーの場合はアニメーションを表示
                    if (entity == minecraft.player) {
                        System.out.println("Displaying custom totem animation: " + customStack);
                        minecraft.gameRenderer.displayItemActivation(customStack);
                    }

                    // サウンドを再生
                    minecraft.level.playLocalSound(
                            entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.TOTEM_USE, entity.getSoundSource(),
                            1.0F, 1.0F, false
                    );

                    // バニラの処理をキャンセル
                    ci.cancel();
                }
            }
        }
    }
}
