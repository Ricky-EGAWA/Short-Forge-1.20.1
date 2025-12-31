package com.ricky.totem.util;

import com.google.common.base.Predicates;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

/**
 * エンドポータルのヘルパークラス
 * 1x1ポータル用のパターンを提供
 */
public class EndPortalHelper {

    private static BlockPattern smallPortalShape;

    /**
     * 1x1ポータル用の小さいパターンを取得
     * パターン:
     * ?v?
     * >?<
     * ?^?
     */
    public static BlockPattern getOrCreateSmallPortalShape() {
        if (smallPortalShape == null) {
            smallPortalShape = BlockPatternBuilder.start()
                    .aisle("?v?", ">?<", "?^?")
                    .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .where('^', BlockInWorld.hasState(
                            BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                                    .where(EndPortalFrameBlock.HAS_EYE, Predicates.equalTo(true))
                                    .where(EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.SOUTH))))
                    .where('>', BlockInWorld.hasState(
                            BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                                    .where(EndPortalFrameBlock.HAS_EYE, Predicates.equalTo(true))
                                    .where(EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.WEST))))
                    .where('v', BlockInWorld.hasState(
                            BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                                    .where(EndPortalFrameBlock.HAS_EYE, Predicates.equalTo(true))
                                    .where(EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.NORTH))))
                    .where('<', BlockInWorld.hasState(
                            BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                                    .where(EndPortalFrameBlock.HAS_EYE, Predicates.equalTo(true))
                                    .where(EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.EAST))))
                    .build();
        }
        return smallPortalShape;
    }
}
