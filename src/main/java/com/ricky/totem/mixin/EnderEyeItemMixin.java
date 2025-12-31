package com.ricky.totem.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import com.ricky.totem.util.EndPortalHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EnderEyeItemのuseOnメソッドを修正し、1x1エンドポータルを可能にする
 */
@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {

    /**
     * エンダーアイを設置した後、小さいポータルパターンもチェックする
     */
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void onUseOn(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);

        if (blockstate.is(Blocks.END_PORTAL_FRAME) && !blockstate.getValue(EndPortalFrameBlock.HAS_EYE)) {
            if (level.isClientSide) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            // エンダーアイを設置
            BlockState blockstate1 = blockstate.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(true));
            Block.pushEntitiesUp(blockstate, blockstate1, level, blockpos);
            level.setBlock(blockpos, blockstate1, 2);
            level.updateNeighbourForOutputSignal(blockpos, Blocks.END_PORTAL_FRAME);
            pContext.getItemInHand().shrink(1);
            level.levelEvent(1503, blockpos, 0);

            // まず通常の3x3パターンをチェック
            BlockPattern.BlockPatternMatch normalMatch = EndPortalFrameBlock.getOrCreatePortalShape().find(level, blockpos);
            if (normalMatch != null) {
                // 通常の3x3ポータルを生成
                BlockPos portalPos = normalMatch.getFrontTopLeft().offset(-3, 0, -3);
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        level.setBlock(portalPos.offset(i, 0, j), Blocks.END_PORTAL.defaultBlockState(), 2);
                    }
                }
                level.globalLevelEvent(1038, portalPos.offset(1, 0, 1), 0);
                cir.setReturnValue(InteractionResult.CONSUME);
                return;
            }

            // 小さい1x1パターンをチェック
            BlockPattern.BlockPatternMatch smallMatch = EndPortalHelper.getOrCreateSmallPortalShape().find(level, blockpos);
            if (smallMatch != null) {
                // 1x1ポータルを生成
                BlockPos portalPos = smallMatch.getFrontTopLeft().offset(-1, 0, -1);
                level.setBlock(portalPos, Blocks.END_PORTAL.defaultBlockState(), 2);
                level.globalLevelEvent(1038, portalPos, 0);
                cir.setReturnValue(InteractionResult.CONSUME);
                return;
            }

            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
