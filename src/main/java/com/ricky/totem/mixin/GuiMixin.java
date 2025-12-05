package com.ricky.totem.mixin;

import com.ricky.totem.client.HeartStyleTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * GuiのMixin
 * ハートのレンダリング時にハードコアスタイルを適用
 */
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    protected Minecraft minecraft;

    /**
     * renderHearts メソッド内の LevelData.isHardcore() チェックをインターセプト
     * ハードコアハートが有効な場合は true を返すようにする
     */
    @Redirect(
            method = "renderHearts",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/LevelData;isHardcore()Z"
            )
    )
    private boolean redirectIsHardcore(LevelData levelData) {
        // カスタム設定が有効な場合はハードコアハートを表示
        if (HeartStyleTracker.isHardcoreHeartsEnabled()) {
            return true;
        }
        // それ以外は元の判定を使用
        return levelData.isHardcore();
    }
}
