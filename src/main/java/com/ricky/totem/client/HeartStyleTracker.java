package com.ricky.totem.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * クライアント側でハートのスタイルを管理するクラス
 */
@OnlyIn(Dist.CLIENT)
public class HeartStyleTracker {

    private static boolean hardcoreHeartsEnabled = false;

    /**
     * ハードコアハートを有効/無効にする
     */
    public static void setHardcoreHeartsEnabled(boolean enabled) {
        hardcoreHeartsEnabled = enabled;
    }

    /**
     * ハードコアハートが有効かどうかを取得
     */
    public static boolean isHardcoreHeartsEnabled() {
        return hardcoreHeartsEnabled;
    }

    /**
     * ハードコアハートの状態を切り替え
     */
    public static void toggle() {
        hardcoreHeartsEnabled = !hardcoreHeartsEnabled;
    }
}
