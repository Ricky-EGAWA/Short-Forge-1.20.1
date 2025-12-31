package com.ricky.totem.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.entity.NotchEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * ノッチのアウトレイヤー（服など）を描画するレイヤー
 */
public class NotchOuterLayer<T extends NotchEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation NOTCH_OUTER_TEXTURE =
            new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/skin/notch.png");

    private final PlayerModel<T> outerModel;

    public NotchOuterLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.outerModel = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER), false);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(
                this.getParentModel(),
                this.outerModel,
                NOTCH_OUTER_TEXTURE,
                poseStack,
                buffer,
                packedLight,
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch,
                partialTicks,
                1.0F, 1.0F, 1.0F
        );
    }
}
