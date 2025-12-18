package com.ricky.totem.client;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * クライアント側のMODイベント（MODバス）
 */
@Mod.EventBusSubscriber(modid = TotemItemsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    // カスタムスキンテクスチャのマッピング
    private static final ResourceLocation MICKY_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/micky.png");
    private static final ResourceLocation MINNIE_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/minnie.png");
    private static final ResourceLocation DONALD_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/donald.png");

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // ゾンビ用のテクスチャマップ
        Map<String, ResourceLocation> zombieTextures = new HashMap<>();
        zombieTextures.put("Micky", MICKY_TEXTURE);

        // ドラウンド用のテクスチャマップ
        Map<String, ResourceLocation> drownedTextures = new HashMap<>();
        drownedTextures.put("Minnie", MINNIE_TEXTURE);

        // ハスク用のテクスチャマップ
        Map<String, ResourceLocation> huskTextures = new HashMap<>();
        huskTextures.put("Donald", DONALD_TEXTURE);

        // ゾンビレンダラーにレイヤーを追加
        ZombieRenderer zombieRenderer = event.getRenderer(net.minecraft.world.entity.EntityType.ZOMBIE);
        if (zombieRenderer != null) {
            zombieRenderer.addLayer(new CustomSkinOuterLayer<>(zombieRenderer, zombieTextures));
        }

        // ドラウンドレンダラーにレイヤーを追加
        DrownedRenderer drownedRenderer = event.getRenderer(net.minecraft.world.entity.EntityType.DROWNED);
        if (drownedRenderer != null) {
            drownedRenderer.addLayer(new CustomSkinOuterLayer<>(drownedRenderer, drownedTextures));
        }

        // ハスクレンダラーにレイヤーを追加
        HuskRenderer huskRenderer = event.getRenderer(net.minecraft.world.entity.EntityType.HUSK);
        if (huskRenderer != null) {
            huskRenderer.addLayer(new CustomSkinOuterLayer<>(huskRenderer, huskTextures));
        }
    }
}
