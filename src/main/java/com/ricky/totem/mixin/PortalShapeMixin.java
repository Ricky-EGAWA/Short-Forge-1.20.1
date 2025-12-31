package com.ricky.totem.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * ネザーポータルの最小サイズを1x1に変更するMixin
 * 通常: 最小2x3、最大21x21
 * 変更後: 最小1x1、最大21x21
 */
@Mixin(PortalShape.class)
public abstract class PortalShapeMixin {

    @Shadow
    @Final
    @Mutable
    private int width;

    @Shadow
    @Final
    @Mutable
    private int height;

    @Shadow
    @Final
    @Nullable
    private BlockPos bottomLeft;

    @Shadow
    @Final
    private Direction rightDir;

    @Shadow
    private int getDistanceUntilEdge(BlockPos pos, Direction direction);

    @Shadow
    private int getDistanceUntilTop(BlockPos pos);

    /**
     * calculateWidthの戻り値を修正
     * 通常は幅<2で0を返すが、幅>=1なら実際の幅を返すようにする
     */
    @Inject(method = "calculateWidth", at = @At("RETURN"), cancellable = true)
    private void onCalculateWidth(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0 && this.bottomLeft != null) {
            // 元のメソッドが0を返した場合、実際の幅を再計算
            int actualWidth = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if (actualWidth >= 1 && actualWidth <= 21) {
                cir.setReturnValue(actualWidth);
            }
        }
    }

    /**
     * calculateHeightの戻り値を修正
     * 通常は高さ<3で0を返すが、高さ>=1なら実際の高さを返すようにする
     */
    @Inject(method = "calculateHeight", at = @At("RETURN"), cancellable = true)
    private void onCalculateHeight(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0 && this.bottomLeft != null) {
            // 元のメソッドが0を返した場合、実際の高さを再計算
            int actualHeight = this.getDistanceUntilTop(this.bottomLeft);
            if (actualHeight >= 1 && actualHeight <= 21) {
                cir.setReturnValue(actualHeight);
            }
        }
    }

    /**
     * ポータルが有効かどうかのチェックを修正
     * 通常は幅>=2、高さ>=3が必要だが、1x1でも有効にする
     * bottomLeftがnullでないことで黒曜石フレームの検証を維持
     */
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void onIsValid(CallbackInfoReturnable<Boolean> cir) {
        // bottomLeftがnullでないことを確認（黒曜石フレームが見つかったことを示す）
        // 幅と高さの最小値を1に変更（通常は2x3）
        boolean isValid = this.bottomLeft != null
                && this.width >= 1 && this.width <= 21
                && this.height >= 1 && this.height <= 21;
        cir.setReturnValue(isValid);
    }
}
