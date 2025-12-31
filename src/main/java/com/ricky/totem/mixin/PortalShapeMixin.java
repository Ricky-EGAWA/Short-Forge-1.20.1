package com.ricky.totem.mixin;

import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * ネザーポータルの最小サイズを1x1に変更するMixin
 * 通常: 最小2x3、最大21x21
 * 変更後: 最小1x1、最大21x21
 */
@Mixin(PortalShape.class)
public class PortalShapeMixin {

    /**
     * 最小幅の定数(2)を1に変更
     */
    @ModifyConstant(method = "calculateBottomLeft", constant = @Constant(intValue = 2))
    private int modifyMinWidth(int original) {
        return 1;
    }

    /**
     * 最小高さの定数(3)を1に変更
     */
    @ModifyConstant(method = "calculateHeight", constant = @Constant(intValue = 3))
    private int modifyMinHeight(int original) {
        return 1;
    }
}
