package com.ricky.totem.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * クライアント側でカスタムトーテムのテクスチャを追跡するクラス
 * トーテム発動時にサーバーから受信したアイテム情報を保持し、
 * GameRendererのMixinで使用する
 */
@OnlyIn(Dist.CLIENT)
public class TotemTextureTracker {

    // アイテムのレジストリ名を保持
    @Nullable
    private static ResourceLocation customTotemItem = null;

    // テクスチャが有効な時間（ミリ秒）
    private static long textureSetTime = 0;
    private static final long TEXTURE_VALIDITY_MS = 500; // 500ms間有効

    /**
     * カスタムトーテムアイテムを設定
     * サーバーからパケットを受信した際に呼び出される
     * @param itemId アイテムのレジストリ名（例: "totem:totem_sword"）
     */
    public static void setCustomTotemTexture(ResourceLocation itemId) {
        customTotemItem = itemId;
        textureSetTime = System.currentTimeMillis();
    }

    /**
     * 現在のカスタムトーテムアイテムIDを取得
     * 有効期限内であればIDを返し、消費（nullに設定）する
     */
    @Nullable
    public static ResourceLocation consumeCustomTotemTexture() {
        if (customTotemItem != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - textureSetTime <= TEXTURE_VALIDITY_MS) {
                ResourceLocation itemId = customTotemItem;
                customTotemItem = null;
                return itemId;
            } else {
                // 有効期限切れ
                customTotemItem = null;
            }
        }
        return null;
    }

    /**
     * アイテムIDからItemStackを取得
     * @param itemId アイテムのレジストリ名
     * @return 対応するItemStack、見つからない場合はnull
     */
    @Nullable
    public static ItemStack getItemStackForTexture(ResourceLocation itemId) {
        if (itemId == null) {
            return null;
        }
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item != null) {
            return new ItemStack(item);
        }
        return null;
    }

    /**
     * カスタムトーテムテクスチャが設定されているか確認（消費せずに）
     */
    public static boolean hasCustomTotemTexture() {
        if (customTotemItem != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - textureSetTime <= TEXTURE_VALIDITY_MS) {
                return true;
            } else {
                customTotemItem = null;
            }
        }
        return false;
    }

    /**
     * テクスチャをクリア
     */
    public static void clear() {
        customTotemItem = null;
    }
}
