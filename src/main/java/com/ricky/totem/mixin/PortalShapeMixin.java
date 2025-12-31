package com.ricky.totem.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ネザーポータルの最小サイズを1x1に変更するMixin
 * 通常: 最小2x3、最大21x21
 * 変更後: 最小1x1、最大21x21
 */
@Mixin(PortalShape.class)
public class PortalShapeMixin {

    @Shadow
    @Final
    @Mutable
    private int width;

    @Shadow
    @Final
    @Mutable
    private int height;

    /**
     * ポータルが有効かどうかのチェックを修正
     * 通常は幅>=2、高さ>=3が必要だが、1x1でも有効にする
     */
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void onIsValid(CallbackInfoReturnable<Boolean> cir) {
        // 幅と高さが1以上あれば有効とする
        if (this.width >= 1 && this.height >= 1) {
            cir.setReturnValue(true);
        }
    }
}
