package com.ricky.totem.mixin;

import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * ネザーポータルの最小サイズを1x1に変更するMixin
 * 通常: 最小2x3、最大21x21
 * 変更後: 最小1x1、最大21x21
 *
 * @ModifyConstantを使用してバニラの検証ロジックを維持しつつ、
 * 最小サイズの条件だけを変更する
 */
@Mixin(PortalShape.class)
public abstract class PortalShapeMixin {

    /**
     * calculateWidthの最小幅チェック (>= 2) を (>= 1) に変更
     * バニラは width >= 2 && width <= 21 でチェックしている
     */
    @ModifyConstant(method = "calculateWidth", constant = @Constant(intValue = 2))
    private int modifyMinWidth(int original) {
        return 1;
    }

    /**
     * calculateHeightの最小高さチェック (>= 3) を (>= 1) に変更
     * バニラは height >= 3 && height <= 21 でチェックしている
     */
    @ModifyConstant(method = "calculateHeight", constant = @Constant(intValue = 3))
    private int modifyMinHeight(int original) {
        return 1;
    }

    /**
     * isValidの幅チェック (>= 2) を (>= 1) に変更
     */
    @ModifyConstant(method = "isValid", constant = @Constant(intValue = 2))
    private int modifyIsValidMinWidth(int original) {
        return 1;
    }

    /**
     * isValidの高さチェック (>= 3) を (>= 1) に変更
     */
    @ModifyConstant(method = "isValid", constant = @Constant(intValue = 3))
    private int modifyIsValidMinHeight(int original) {
        return 1;
    }
}
