package com.ricky.totem.client.renderer;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.client.renderer.layer.NotchOuterLayer;
import com.ricky.totem.entity.NotchEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * ノッチのレンダラー - プレイヤーモデルを使用（腕を自然に振る）
 */
public class NotchRenderer extends MobRenderer<NotchEntity, PlayerModel<NotchEntity>> {

    private static final ResourceLocation NOTCH_TEXTURE =
            new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/notch.png");

    public NotchRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.addLayer(new NotchOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(NotchEntity entity) {
        return NOTCH_TEXTURE;
    }
}
