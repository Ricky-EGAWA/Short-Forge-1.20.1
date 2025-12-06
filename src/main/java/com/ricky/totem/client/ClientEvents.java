package com.ricky.totem.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
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
public class ClientEvents extends RenderStateShard {

    // RenderStateShardの継承に必要なダミーコンストラクタ
    public ClientEvents() {
        super("dummy", () -> {}, () -> {});
    }

    // カリングを有効にしたカスタムRenderType（裏側から透明にするため）
    private static final RenderType MAP_TEXTURE_CULLED = RenderType.create(
            "map_texture_culled",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_TEXT_SHADER)
                    .setTextureState(new TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setCullState(CULL)
                    .createCompositeState(false)
    );

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.hasTag()) {
            return;
        }

        TextureAtlasSprite textureSprite = null;

        // 石テクスチャの地図かチェック
        if (stack.getTag().getBoolean("StoneTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.STONE.defaultBlockState())
                .getParticleIcon();
        }
        // ネザーラックテクスチャの地図かチェック
        else if (stack.getTag().getBoolean("NetherrackTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.NETHERRACK.defaultBlockState())
                .getParticleIcon();
        }

        // カスタムテクスチャがある場合はレンダリング
        if (textureSprite != null) {
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

            renderBlockTexture(poseStack, bufferSource, combinedLight, textureSprite);

            poseStack.popPose();
        }
    }

    private static void renderBlockTexture(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, TextureAtlasSprite textureSprite) {
        Matrix4f matrix4f = poseStack.last().pose();
        // カリングを有効にしたカスタムRenderTypeを使用（裏側から見ると透明になる）
        VertexConsumer vertexConsumer = bufferSource.getBuffer(MAP_TEXTURE_CULLED);

        float minU = textureSprite.getU0();
        float maxU = textureSprite.getU1();
        float minV = textureSprite.getV0();
        float maxV = textureSprite.getV1();

        // 128x128ピクセルの地図サイズでテクスチャを描画
        float min = -64.0F;
        float max = 64.0F;
        float z = -0.01F;

        // 暗い色で描画（128/255 = 約50%の明るさ）
        int darkness = 148;

        // 4つの頂点で四角形を描画（地図と同じサイズ）
        vertexConsumer.vertex(matrix4f, min, max, z).color(darkness, darkness, darkness, 255).uv(minU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, max, z).color(darkness, darkness, darkness, 255).uv(maxU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, min, z).color(darkness, darkness, darkness, 255).uv(maxU, minV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, min, min, z).color(darkness, darkness, darkness, 255).uv(minU, minV).uv2(combinedLight).endVertex();
    }
}
