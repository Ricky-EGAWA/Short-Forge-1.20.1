package com.ricky.totem.client.renderer;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.EnderMan;

/**
 * カスタムエンティティレンダラーの管理クラス
 */
public class CustomEntityRendererManager {

    private static NamedEntityPlayerRenderer<Chicken> donaldRenderer;
    private static NamedEntityPlayerRenderer<EnderMan> minnieRenderer;

    public static final ResourceLocation DONALD_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/chicken/donald.png");
    public static final ResourceLocation MINNIE_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/enderman/minnie.png");

    private static boolean initialized = false;

    /**
     * レンダラーを初期化（遅延初期化）
     */
    public static void ensureInitialized() {
        if (initialized) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getEntityRenderDispatcher() == null) return;

        EntityRendererProvider.Context context = new EntityRendererProvider.Context(
                mc.getEntityRenderDispatcher(),
                mc.getItemRenderer(),
                mc.getBlockRenderer(),
                mc.gameRenderer.itemInHandRenderer,
                mc.getResourceManager(),
                mc.getEntityModels(),
                mc.font
        );

        // Donald（鶏）用レンダラー - 鶏は小さいのでスケールを調整
        donaldRenderer = new NamedEntityPlayerRenderer<>(context, DONALD_TEXTURE, 0.5F);

        // Minnie（エンダーマン）用レンダラー - エンダーマンは大きいのでスケールを調整
        minnieRenderer = new NamedEntityPlayerRenderer<>(context, MINNIE_TEXTURE, 0.9F);

        initialized = true;
    }

    public static NamedEntityPlayerRenderer<Chicken> getDonaldRenderer() {
        ensureInitialized();
        return donaldRenderer;
    }

    public static NamedEntityPlayerRenderer<EnderMan> getMinnieRenderer() {
        ensureInitialized();
        return minnieRenderer;
    }

    /**
     * 鶏が「Donald」という名前かどうかをチェック
     */
    public static boolean isDonald(Chicken chicken) {
        return chicken.hasCustomName() && "Donald".equals(chicken.getCustomName().getString());
    }

    /**
     * エンダーマンが「Minnie」という名前かどうかをチェック
     */
    public static boolean isMinnie(EnderMan enderman) {
        return enderman.hasCustomName() && "Minnie".equals(enderman.getCustomName().getString());
    }
}
