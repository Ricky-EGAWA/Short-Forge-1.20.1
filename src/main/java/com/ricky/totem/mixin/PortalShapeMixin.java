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
 * 黒曜石フレームの検証を独自に行い、正しいフレームがある場合のみポータルを生成する
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

    @Shadow
    protected abstract int getDistanceUntilEdgeAboveFrame(BlockPos pos, Direction direction);

    @Shadow
    protected abstract int getDistanceUntilTop(BlockPos.MutableBlockPos pos);

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
     * ただし、黒曜石フレームの検証を行う
     */
    @Inject(method = "calculateWidth", at = @At("RETURN"), cancellable = true)
    private void onCalculateWidth(CallbackInfoReturnable<Integer> cir) {
        // 元のメソッドが0を返した場合、実際の幅を再計算
        if (cir.getReturnValue() == 0 && this.bottomLeft != null) {
            int actualWidth = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
            if (actualWidth >= 1 && actualWidth <= 21) {
                // 下辺のフレームを検証
                if (totem$validateBottomFrame(actualWidth)) {
                    this.width = actualWidth;
                    cir.setReturnValue(actualWidth);
                }
            }
        }
    }

    /**
     * calculateHeightの戻り値を修正
     * 通常は高さ<3で0を返すが、高さ>=1なら実際の高さを返すようにする
     * ただし、黒曜石フレームの検証を行う
     */
    @Inject(method = "calculateHeight", at = @At("RETURN"), cancellable = true)
    private void onCalculateHeight(CallbackInfoReturnable<Integer> cir) {
        // 元のメソッドが0を返した場合、実際の高さを再計算
        if (cir.getReturnValue() == 0 && this.bottomLeft != null && this.width > 0) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            mutablePos.set(this.bottomLeft);
            int actualHeight = this.getDistanceUntilTop(mutablePos);
            if (actualHeight >= 1 && actualHeight <= 21) {
                // 上辺と左右のフレームを検証
                if (totem$validateTopFrame(actualHeight) && totem$validateSideFrames(actualHeight)) {
                    this.height = actualHeight;
                    cir.setReturnValue(actualHeight);
                }
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
     * 下辺のフレーム（黒曜石）を検証
     */
    @Unique
    private boolean totem$validateBottomFrame(int width) {
        if (this.totem$capturedLevel == null || this.bottomLeft == null) {
            return false;
        }
        for (int x = 0; x < width; x++) {
            BlockPos pos = this.bottomLeft.relative(this.rightDir, x).below();
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(pos))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 上辺のフレーム（黒曜石）を検証
     */
    @Unique
    private boolean totem$validateTopFrame(int height) {
        if (this.totem$capturedLevel == null || this.bottomLeft == null) {
            return false;
        }
        for (int x = 0; x < this.width; x++) {
            BlockPos pos = this.bottomLeft.relative(this.rightDir, x).above(height);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(pos))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 左右のフレーム（黒曜石）を検証
     */
    @Unique
    private boolean totem$validateSideFrames(int height) {
        if (this.totem$capturedLevel == null || this.bottomLeft == null) {
            return false;
        }
        // 左辺
        for (int y = 0; y < height; y++) {
            BlockPos pos = this.bottomLeft.above(y).relative(this.rightDir, -1);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(pos))) {
                return false;
            }
        }
        // 右辺
        for (int y = 0; y < height; y++) {
            BlockPos pos = this.bottomLeft.above(y).relative(this.rightDir, this.width);
            if (!totem$isObsidian(this.totem$capturedLevel.getBlockState(pos))) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static boolean totem$isObsidian(BlockState state) {
        return state.is(Blocks.OBSIDIAN);
    }
}
