package com.ricky.totem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class StoneMapRenderer {

    public static void renderStoneTexture(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
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

        // 地図と同じサイズ（128x128ピクセル、0.0から1.0の範囲）で石テクスチャを描画
        float min = 0.0F;
        float max = 1.0F;
        float z = 0.0F;

        // 4つの頂点で四角形を描画
        vertexConsumer.vertex(matrix4f, min, max, z).color(255, 255, 255, 255).uv(minU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, max, z).color(255, 255, 255, 255).uv(maxU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, min, z).color(255, 255, 255, 255).uv(maxU, minV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, min, min, z).color(255, 255, 255, 255).uv(minU, minV).uv2(combinedLight).endVertex();
    }
}
