package com.ricky.totem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.client.renderer.StoneMapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TotemItemsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemFrame itemFrame = event.getItemFrame();
        ItemStack stack = itemFrame.getItem();

        // 石テクスチャの地図かチェック
        if (stack.hasTag() && stack.getTag().getBoolean("StoneTextured")) {
            // デフォルトのレンダリングをキャンセル
            event.setCanceled(true);

            // カスタム石テクスチャをレンダリング
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            int combinedLight = event.getPackedLight();

            poseStack.pushPose();

            // 地図のレンダリングと同じ変換を適用
            // 額縁内の地図は0.0から1.0の範囲でレンダリングされる
            poseStack.translate(0.0, 0.0, -0.001); // わずかに前に移動して額縁の前面に表示

            StoneMapRenderer.renderStoneTexture(poseStack, bufferSource, combinedLight);

            poseStack.popPose();
        }
    }
}
