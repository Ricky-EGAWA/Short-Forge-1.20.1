package com.ricky.totem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * カスタム名前付きエンティティ用のプレイヤーモデルレンダラー
 * 鶏の「Donald」やエンダーマンの「Minnie」に使用
 */
public class NamedEntityPlayerRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, PlayerModel<T>> {

    private final ResourceLocation textureLocation;
    private final float scale;

    public NamedEntityPlayerRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float scale) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.textureLocation = texture;
        this.scale = scale;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return textureLocation;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        // モデルの設定
        this.model.young = entity.isBaby();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
