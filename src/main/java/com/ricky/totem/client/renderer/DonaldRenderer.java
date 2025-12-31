package com.ricky.totem.client.renderer;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.client.renderer.layer.DonaldOuterLayer;
import com.ricky.totem.entity.DonaldEntity;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * ドナルドのレンダラー - アウトレイヤー付き
 */
public class DonaldRenderer extends AbstractZombieRenderer<DonaldEntity, ZombieModel<DonaldEntity>> {

    private static final ResourceLocation DONALD_TEXTURE =
            new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/donald.png");

    public DonaldRenderer(EntityRendererProvider.Context context) {
        super(context,
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR)));
        this.addLayer(new DonaldOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(DonaldEntity entity) {
        return DONALD_TEXTURE;
    }
}
