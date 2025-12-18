package com.ricky.totem.mixin;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * プレイヤーの泳いでいるときの当たり判定と目線を変更するMixin
 * 大釜の下の隙間（約3/16ブロック）を通れるサイズに縮小
 */
@Mixin(Player.class)
public abstract class PlayerSwimmingDimensionsMixin {

    // 泳いでいるときのカスタムサイズ
    private static final float SWIMMING_WIDTH = 0.3F;
    private static final float SWIMMING_HEIGHT = 0.15F;
    private static final float SWIMMING_EYE_HEIGHT = 0.075F;

    /**
     * getDimensionsメソッドをインターセプトし、
     * 泳いでいる状態の当たり判定を小さくする
     */
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifySwimmingDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == Pose.SWIMMING) {
            // 大釜の下の隙間を通れるサイズ
            // 幅0.3、高さ0.15、目線0.075に設定
            cir.setReturnValue(EntityDimensions.scalable(SWIMMING_WIDTH, SWIMMING_HEIGHT).withEyeHeight(SWIMMING_EYE_HEIGHT));
        }
    }
}
