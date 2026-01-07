package com.ricky.totem.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * ネザーポータルの最小サイズを1x1に変更するMixin
 * 通常: 最小2x3、最大21x21
 * 変更後: 最小1x1、最大21x21
 *
 * 独自のフレーム検証ロジックで黒曜石フレームを完全に検証する
 */
@Mixin(PortalShape.class)
public abstract class PortalShapeMixin {

    @Shadow
    @Final
    @Nullable
    private BlockPos bottomLeft;

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
    private Direction rightDir;

    // コンストラクタでキャプチャしたLevelAccessor
    @Unique
    private LevelAccessor totem$capturedLevel;

    /**
     * コンストラクタでLevelAccessorをキャプチャ
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(LevelAccessor level, BlockPos pos, Direction.Axis axis, CallbackInfo ci) {
        this.totem$capturedLevel = level;
    }

    /**
     * calculateWidthの戻り値を修正
     * 通常は幅<2で0を返すが、幅>=1なら実際の幅を返すようにする
     * 独自のフレーム検証を行う
     */
    @Inject(method = "calculateWidth", at = @At("RETURN"), cancellable = true)
    private void onCalculateWidth(CallbackInfoReturnable<Integer> cir) {
        // 元のメソッドが0を返した場合、独自の幅計算を行う
        if (cir.getReturnValue() == 0 && this.bottomLeft != null && this.totem$capturedLevel != null) {
            int actualWidth = totem$calculateActualWidth();
            if (actualWidth >= 1 && actualWidth <= 21) {
                this.width = actualWidth;
                cir.setReturnValue(actualWidth);
            }
        }
    }

    /**
     * calculateHeightの戻り値を修正
     * 通常は高さ<3で0を返すが、高さ>=1なら実際の高さを返すようにする
     * 独自のフレーム検証を行う
     */
    @Inject(method = "calculateHeight", at = @At("RETURN"), cancellable = true)
    private void onCalculateHeight(CallbackInfoReturnable<Integer> cir) {
        // 元のメソッドが0を返した場合、独自の高さ計算を行う
        if (cir.getReturnValue() == 0 && this.bottomLeft != null && this.width > 0 && this.totem$capturedLevel != null) {
            int actualHeight = totem$calculateActualHeight();
            if (actualHeight >= 1 && actualHeight <= 21) {
                this.height = actualHeight;
                cir.setReturnValue(actualHeight);
            }
        }
    }

    /**
     * ポータルの有効性チェックを修正
     * 通常は幅>=2、高さ>=3が必要だが、1x1でも有効にする
     */
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void onIsValid(CallbackInfoReturnable<Boolean> cir) {
        if (this.bottomLeft == null) {
            cir.setReturnValue(false);
            return;
        }

        // サイズチェック: 1x1以上21x21以下
        boolean isValid = this.width >= 1 && this.width <= 21
                && this.height >= 1 && this.height <= 21;
        cir.setReturnValue(isValid);
    }

    /**
     * 独自の幅計算
     * bottomLeftから右方向にスキャンし、下辺と上辺に黒曜石フレームがある幅を計算
     */
    @Unique
    private int totem$calculateActualWidth() {
        int maxWidth = 0;

        // 最大21ブロックまでスキャン
        for (int x = 0; x < 21; x++) {
            BlockPos currentPos = this.bottomLeft.relative(this.rightDir, x);
            BlockPos belowPos = currentPos.below();

            // 下のブロックが黒曜石でなければ終了
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(belowPos))) {
                break;
            }

            // 現在位置が空気またはポータルでなければ終了（右端の黒曜石に当たった）
            BlockState currentState = this.totem$capturedLevel.getBlockState(currentPos);
            if (!totem$isEmpty(currentState)) {
                break;
            }

            maxWidth = x + 1;
        }

        // 右端のフレーム（黒曜石）を確認
        if (maxWidth > 0) {
            BlockPos rightFramePos = this.bottomLeft.relative(this.rightDir, maxWidth);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(rightFramePos))) {
                return 0; // 右端にフレームがない
            }
        }

        // 左端のフレーム（黒曜石）を確認
        BlockPos leftFramePos = this.bottomLeft.relative(this.rightDir, -1);
        if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(leftFramePos))) {
            return 0; // 左端にフレームがない
        }

        return maxWidth;
    }

    /**
     * 独自の高さ計算
     * bottomLeftから上方向にスキャンし、左辺と右辺に黒曜石フレームがある高さを計算
     */
    @Unique
    private int totem$calculateActualHeight() {
        int maxHeight = 0;

        // 最大21ブロックまでスキャン
        for (int y = 0; y < 21; y++) {
            boolean rowValid = true;

            // 各行で、全ての幅について確認
            for (int x = 0; x < this.width; x++) {
                BlockPos currentPos = this.bottomLeft.relative(this.rightDir, x).above(y);
                BlockState currentState = this.totem$capturedLevel.getBlockState(currentPos);

                // 現在位置が空気またはポータルでなければこの行は無効
                if (!totem$isEmpty(currentState)) {
                    rowValid = false;
                    break;
                }
            }

            if (!rowValid) {
                break;
            }

            // 左辺のフレーム確認
            BlockPos leftPos = this.bottomLeft.above(y).relative(this.rightDir, -1);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(leftPos))) {
                break;
            }

            // 右辺のフレーム確認
            BlockPos rightPos = this.bottomLeft.above(y).relative(this.rightDir, this.width);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(rightPos))) {
                break;
            }

            maxHeight = y + 1;
        }

        // 上辺のフレーム（黒曜石）を確認
        if (maxHeight > 0) {
            for (int x = 0; x < this.width; x++) {
                BlockPos topFramePos = this.bottomLeft.relative(this.rightDir, x).above(maxHeight);
                if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(topFramePos))) {
                    return 0; // 上辺にフレームがない
                }
            }
        }

        return maxHeight;
    }

    @Unique
    private static boolean totem$isObsidian(BlockState state) {
        return state.is(Blocks.OBSIDIAN);
    }

    @Unique
    private static boolean totem$isEmpty(BlockState state) {
        return state.isAir() || state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.FIRE);
    }
}
