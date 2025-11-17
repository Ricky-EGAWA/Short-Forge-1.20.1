package com.ricky.totem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = TotemItemsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();

        // 石テクスチャの地図かチェック
        if (stack.hasTag() && stack.getTag().getBoolean("StoneTextured")) {
            // デフォルトのレンダリングをキャンセル
            event.setCanceled(true);

            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            int combinedLight = event.getPackedLight();

            poseStack.pushPose();

            // 地図のレンダリングと同じ変換を適用
            // 額縁内で地図は特別な変換を受ける
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F); // 128分の1（地図サイズ）

            // 石ブロックのテクスチャを取得
            TextureAtlasSprite stoneSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.STONE.defaultBlockState())
                .getParticleIcon();

            Matrix4f matrix4f = poseStack.last().pose();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.text(InventoryMenu.BLOCK_ATLAS));

            float minU = stoneSprite.getU0();
            float maxU = stoneSprite.getU1();
            float minV = stoneSprite.getV0();
            float maxV = stoneSprite.getV1();

            // 128x128ピクセルの地図サイズで石テクスチャを描画
            float min = -64.0F;
            float max = 64.0F;
            float z = -0.01F;

            // 4つの頂点で四角形を描画（地図と同じサイズ）
            vertexConsumer.vertex(matrix4f, min, max, z).color(255, 255, 255, 255).uv(minU, maxV).uv2(combinedLight).endVertex();
            vertexConsumer.vertex(matrix4f, max, max, z).color(255, 255, 255, 255).uv(maxU, maxV).uv2(combinedLight).endVertex();
            vertexConsumer.vertex(matrix4f, max, min, z).color(255, 255, 255, 255).uv(maxU, minV).uv2(combinedLight).endVertex();
            vertexConsumer.vertex(matrix4f, min, min, z).color(255, 255, 255, 255).uv(minU, minV).uv2(combinedLight).endVertex();

            poseStack.popPose();
        }
    }
}
