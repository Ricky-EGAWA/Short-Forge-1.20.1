package com.ricky.totem.client.renderer;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.client.renderer.layer.MinnieOuterLayer;
import com.ricky.totem.entity.MinnieEntity;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * ミニーのレンダラー - アウトレイヤー付き
 */
public class MinnieRenderer extends AbstractZombieRenderer<MinnieEntity, ZombieModel<MinnieEntity>> {

    private static final ResourceLocation MINNIE_TEXTURE =
            new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/minnie.png");

    public MinnieRenderer(EntityRendererProvider.Context context) {
        super(context,
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR)));
        this.addLayer(new MinnieOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(MinnieEntity entity) {
        return MINNIE_TEXTURE;
    }
}
