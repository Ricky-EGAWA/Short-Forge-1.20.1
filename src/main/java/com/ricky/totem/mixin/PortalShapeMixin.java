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
 * isValidで独自のフレーム検証を行い、完全な黒曜石フレームがある場合のみ有効にする
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
     * ポータルの有効性チェックを修正
     * 独自のフレーム検証を行い、黒曜石フレームが完全に形成されている場合のみ有効にする
     * 最小サイズを1x1に変更
     */
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void onIsValid(CallbackInfoReturnable<Boolean> cir) {
        // bottomLeftがnullまたはlevelがnullの場合は無効
        if (this.bottomLeft == null || this.totem$capturedLevel == null) {
            cir.setReturnValue(false);
            return;
        }

        // 独自のフレーム検証を行い、正しいサイズを計算
        int validWidth = totem$calculateValidWidth();
        if (validWidth < 1 || validWidth > 21) {
            cir.setReturnValue(false);
            return;
        }

        int validHeight = totem$calculateValidHeight(validWidth);
        if (validHeight < 1 || validHeight > 21) {
            cir.setReturnValue(false);
            return;
        }

        // widthとheightを更新
        this.width = validWidth;
        this.height = validHeight;

        cir.setReturnValue(true);
    }

    /**
     * 独自の幅計算（フレーム検証付き）
     * bottomLeftから右方向にスキャンし、完全な黒曜石フレームがある幅を計算
     */
    @Unique
    private int totem$calculateValidWidth() {
        // 左端のフレーム（黒曜石）を確認
        BlockPos leftFramePos = this.bottomLeft.relative(this.rightDir, -1);
        if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(leftFramePos))) {
            return 0;
        }

        int maxWidth = 0;

        // 最大21ブロックまでスキャン
        for (int x = 0; x < 21; x++) {
            BlockPos currentPos = this.bottomLeft.relative(this.rightDir, x);
            BlockPos belowPos = currentPos.below();

            // 下のブロックが黒曜石でなければ終了
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(belowPos))) {
                break;
            }

            // 現在位置が空気、ポータル、または火でなければ終了（右端の黒曜石に当たった）
            BlockState currentState = this.totem$capturedLevel.getBlockState(currentPos);
            if (!totem$isEmpty(currentState)) {
                // 右端に黒曜石がある場合は有効な幅
                if (totem$isObsidian(currentState) && maxWidth > 0) {
                    return maxWidth;
                }
                break;
            }

            maxWidth = x + 1;
        }

        // 右端のフレーム（黒曜石）を確認
        if (maxWidth > 0) {
            BlockPos rightFramePos = this.bottomLeft.relative(this.rightDir, maxWidth);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(rightFramePos))) {
                return 0;
            }
        }

        return maxWidth;
    }

    /**
     * 独自の高さ計算（フレーム検証付き）
     * bottomLeftから上方向にスキャンし、完全な黒曜石フレームがある高さを計算
     */
    @Unique
    private int totem$calculateValidHeight(int width) {
        int maxHeight = 0;

        // 最大21ブロックまでスキャン
        for (int y = 0; y < 21; y++) {
            // 左辺のフレーム確認
            BlockPos leftPos = this.bottomLeft.above(y).relative(this.rightDir, -1);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(leftPos))) {
                break;
            }

            // 右辺のフレーム確認
            BlockPos rightPos = this.bottomLeft.above(y).relative(this.rightDir, width);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(rightPos))) {
                break;
            }

            // 各行で、全ての幅について空気/ポータル/火であることを確認
            boolean rowValid = true;
            for (int x = 0; x < width; x++) {
                BlockPos currentPos = this.bottomLeft.relative(this.rightDir, x).above(y);
                BlockState currentState = this.totem$capturedLevel.getBlockState(currentPos);

                if (!totem$isEmpty(currentState)) {
                    // 上辺に黒曜石がある場合はこの高さで終了
                    if (totem$isObsidian(currentState) && maxHeight > 0) {
                        // 上辺全体が黒曜石か確認
                        if (totem$validateTopFrame(maxHeight, width)) {
                            return maxHeight;
                        }
                    }
                    rowValid = false;
                    break;
                }
            }

            if (!rowValid) {
                break;
            }

            maxHeight = y + 1;
        }

        // 上辺のフレーム（黒曜石）を確認
        if (maxHeight > 0 && totem$validateTopFrame(maxHeight, width)) {
            return maxHeight;
        }

        return 0;
    }

    /**
     * 上辺のフレームが全て黒曜石か確認
     */
    @Unique
    private boolean totem$validateTopFrame(int height, int width) {
        for (int x = 0; x < width; x++) {
            BlockPos topFramePos = this.bottomLeft.relative(this.rightDir, x).above(height);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(topFramePos))) {
                return false;
            }
        }
        return true;
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
