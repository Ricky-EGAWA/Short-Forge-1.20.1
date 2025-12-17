package com.ricky.totem.mixin;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * プレイヤーの泳いでいるときの当たり判定を変更するMixin
 * 大釜の下の隙間（約3/16ブロック）を通れるサイズに縮小
 */
@Mixin(Player.class)
public abstract class PlayerSwimmingDimensionsMixin {

    /**
     * getDimensionsメソッドをインターセプトし、
     * 泳いでいる状態の当たり判定を小さくする
     */
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifySwimmingDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == Pose.SWIMMING) {
            // 大釜の下の隙間（約3/16ブロック = 0.1875）を通れるサイズ
            // 幅は通常通り0.6、高さを0.15に縮小
            cir.setReturnValue(EntityDimensions.scalable(0.6F, 0.15F));
        }
    }
}
