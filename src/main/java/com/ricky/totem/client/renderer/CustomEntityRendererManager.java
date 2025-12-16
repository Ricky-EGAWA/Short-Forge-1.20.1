package com.ricky.totem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.EnderMan;

/**
 * カスタムエンティティレンダラーの管理クラス
 */
public class CustomEntityRendererManager {

    private static PlayerModel<LivingEntity> playerModel;

    public static final ResourceLocation DONALD_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/chicken/donald.png");
    public static final ResourceLocation MINNIE_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/enderman/minnie.png");

    private static boolean initialized = false;

    /**
     * プレイヤーモデルを初期化（遅延初期化）
     */
    public static void ensureInitialized() {
        if (initialized) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getEntityModels() == null) return;

        playerModel = new PlayerModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
        initialized = true;
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

    /**
     * プレイヤーモデルでエンティティをレンダリング
     */
    public static void renderAsPlayer(LivingEntity entity, float partialTicks, PoseStack poseStack,
                                       MultiBufferSource buffer, int packedLight, ResourceLocation texture, float scale) {
        ensureInitialized();
        if (playerModel == null) return;

        poseStack.pushPose();

        // スケール調整
        poseStack.scale(scale, scale, scale);

        // モデルのアニメーション設定
        float limbSwing = entity.walkAnimation.position();
        float limbSwingAmount = entity.walkAnimation.speed(partialTicks);
        float ageInTicks = entity.tickCount + partialTicks;
        float headYaw = entity.getYHeadRot() - entity.yBodyRot;
        float headPitch = entity.getXRot();

        // モデルのポーズ設定
        playerModel.young = entity.isBaby();
        playerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        playerModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);

        // レンダリング
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        playerModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
