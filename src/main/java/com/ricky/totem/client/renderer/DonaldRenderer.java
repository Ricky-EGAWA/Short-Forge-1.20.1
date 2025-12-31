package com.ricky.totem.client.renderer;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.client.renderer.layer.DonaldOuterLayer;
import com.ricky.totem.entity.DonaldEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * ドナルドのレンダラー - プレイヤーモデルを使用（腕を自然に振る）
 */
public class DonaldRenderer extends MobRenderer<DonaldEntity, PlayerModel<DonaldEntity>> {

    private static final ResourceLocation DONALD_TEXTURE =
            new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/donald.png");

    public DonaldRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.addLayer(new DonaldOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(DonaldEntity entity) {
        return DONALD_TEXTURE;
    }
}
