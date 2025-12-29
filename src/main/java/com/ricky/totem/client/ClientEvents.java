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
import net.minecraft.resources.ResourceLocation;
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
        int colorR = 148, colorG = 148, colorB = 148; // デフォルトの暗さ

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
        // 草ブロックテクスチャの地図かチェック（上面テクスチャを直接取得）
        else if (stack.getTag().getBoolean("GrassTextured")) {
            textureSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(new ResourceLocation("minecraft", "block/grass_block_top"));
            // 草の緑色のティント
            colorR = 100; colorG = 148; colorB = 80;
        }
        // 溶岩テクスチャの地図かチェック（明るくする）
        else if (stack.getTag().getBoolean("LavaTextured")) {
            textureSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(new ResourceLocation("minecraft", "block/lava_still"));
            // 溶岩は明るく
            colorR = 255; colorG = 255; colorB = 255;
        }
        // 水テクスチャの地図かチェック（青色ティント）
        else if (stack.getTag().getBoolean("WaterTextured")) {
            textureSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(new ResourceLocation("minecraft", "block/water_still"));
            // 平原の水の青色ティント
            colorR = 63; colorG = 118; colorB = 228;
        }
        // オークの板材テクスチャの地図かチェック
        else if (stack.getTag().getBoolean("OakPlanksTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.OAK_PLANKS.defaultBlockState())
                .getParticleIcon();
        }
        // 砂岩背景の石の感圧版テクスチャの地図かチェック（特別な2層レンダリング）
        else if (stack.getTag().getBoolean("SandstonePressurePlateTextured")) {
            // 特別な処理：砂岩の上に石の感圧版を描画
            event.setCanceled(true);

            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            int combinedLight = event.getPackedLight();

            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);

            // 砂岩のテクスチャを取得
            TextureAtlasSprite sandstoneSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(new ResourceLocation("minecraft", "block/sandstone_top"));
            // 石の感圧版のテクスチャを取得
            TextureAtlasSprite pressurePlateSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.STONE_PRESSURE_PLATE.defaultBlockState())
                .getParticleIcon();

            // まず砂岩を背景として描画（明るめに）
            renderBlockTexture(poseStack, bufferSource, combinedLight, sandstoneSprite, 200, 200, 200);
            // その上に中央に石の感圧版を描画（14/16サイズ）
            renderCenteredTexture(poseStack, bufferSource, combinedLight, pressurePlateSprite, 200, 200, 200);

            poseStack.popPose();
            return; // 通常のレンダリング処理をスキップ
        }
        // TNT側面テクスチャの地図かチェック
        else if (stack.getTag().getBoolean("TntSideTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.TNT.defaultBlockState())
                .getParticleIcon();
        }
        // スライムブロックテクスチャの地図かチェック
        else if (stack.getTag().getBoolean("SlimeTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.SLIME_BLOCK.defaultBlockState())
                .getParticleIcon();
        }
        // 黒テクスチャの地図かチェック
        else if (stack.getTag().getBoolean("BlackTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.BLACK_CONCRETE.defaultBlockState())
                .getParticleIcon();
        }
        // ネザーポータルテクスチャの地図かチェック
        else if (stack.getTag().getBoolean("NetherPortalTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.NETHER_PORTAL.defaultBlockState())
                .getParticleIcon();
        }
        // エンドポータルテクスチャの地図かチェック
        else if (stack.getTag().getBoolean("EndPortalTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.BLACK_CONCRETE.defaultBlockState())
                .getParticleIcon();
        }
        // ダイヤモンド鉱石テクスチャの地図かチェック
        else if (stack.getTag().getBoolean("DiamondOreTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.DIAMOND_ORE.defaultBlockState())
                .getParticleIcon();
        }
        // ダイヤモンドブロックテクスチャの地図かチェック
        else if (stack.getTag().getBoolean("DiamondBlockTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.DIAMOND_BLOCK.defaultBlockState())
                .getParticleIcon();
        }
        // 黒曜石テクスチャの地図かチェック
        else if (stack.getTag().getBoolean("ObsidianTextured")) {
            textureSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(Blocks.OBSIDIAN.defaultBlockState())
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

            renderBlockTexture(poseStack, bufferSource, combinedLight, textureSprite, colorR, colorG, colorB);

            poseStack.popPose();
        }
    }

    private static void renderBlockTexture(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, TextureAtlasSprite textureSprite, int r, int g, int b) {
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

        // 4つの頂点で四角形を描画（地図と同じサイズ）
        vertexConsumer.vertex(matrix4f, min, max, z).color(r, g, b, 255).uv(minU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, max, z).color(r, g, b, 255).uv(maxU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, min, z).color(r, g, b, 255).uv(maxU, minV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, min, min, z).color(r, g, b, 255).uv(minU, minV).uv2(combinedLight).endVertex();
    }

    // 中央に小さくテクスチャを描画（砂岩背景の石の感圧版用）
    private static void renderCenteredTexture(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, TextureAtlasSprite textureSprite, int r, int g, int b) {
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(MAP_TEXTURE_CULLED);

        float minU = textureSprite.getU0();
        float maxU = textureSprite.getU1();
        float minV = textureSprite.getV0();
        float maxV = textureSprite.getV1();

        // 中央に描画（14/16サイズ = 112ピクセル幅）
        float min = -56.0F;  // 128 * 14/16 / 2 = 56
        float max = 56.0F;
        float z = -0.02F;    // 砂岩より手前に描画

        vertexConsumer.vertex(matrix4f, min, max, z).color(r, g, b, 255).uv(minU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, max, z).color(r, g, b, 255).uv(maxU, maxV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, max, min, z).color(r, g, b, 255).uv(maxU, minV).uv2(combinedLight).endVertex();
        vertexConsumer.vertex(matrix4f, min, min, z).color(r, g, b, 255).uv(minU, minV).uv2(combinedLight).endVertex();
    }
}
