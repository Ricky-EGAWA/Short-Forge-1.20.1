package com.ricky.totem.client.renderer;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.client.renderer.layer.NotchOuterLayer;
import com.ricky.totem.entity.NotchEntity;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * ミニーのレンダラー - アウトレイヤー付き
 */
public class NotchRenderer extends AbstractZombieRenderer<NotchEntity, ZombieModel<NotchEntity>> {

    private static final ResourceLocation MINNIE_TEXTURE =
            new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/notch.png");

    public NotchRenderer(EntityRendererProvider.Context context) {
        super(context,
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR)));
        this.addLayer(new NotchOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(NotchEntity entity) {
        return MINNIE_TEXTURE;
    }
}
