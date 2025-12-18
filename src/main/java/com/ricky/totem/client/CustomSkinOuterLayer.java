package com.ricky.totem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

/**
 * スキンの外側レイヤー（帽子、上着、袖、ズボンなど）をレンダリングするカスタムレイヤー
 */
public class CustomSkinOuterLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private final Map<String, ResourceLocation> nameToTexture;

    // 外側レイヤー用のカスタムModelPart
    private final ModelPart jacket;
    private final ModelPart leftSleeve;
    private final ModelPart rightSleeve;
    private final ModelPart leftPants;
    private final ModelPart rightPants;

    public CustomSkinOuterLayer(RenderLayerParent<T, M> renderer, Map<String, ResourceLocation> nameToTexture) {
        super(renderer);
        this.nameToTexture = nameToTexture;

        // 外側レイヤーのModelPartを作成
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // 外側レイヤーは0.25の膨張（プレイヤーモデルと同じ）
        CubeDeformation outerDeformation = new CubeDeformation(0.25F);

        // ジャケット（体の外側レイヤー）- UV: 16,32
        partDefinition.addOrReplaceChild("jacket",
                CubeListBuilder.create().texOffs(16, 32)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, outerDeformation),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 左袖 - UV: 48,48
        partDefinition.addOrReplaceChild("left_sleeve",
                CubeListBuilder.create().texOffs(48, 48)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, outerDeformation),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        // 右袖 - UV: 40,32
        partDefinition.addOrReplaceChild("right_sleeve",
                CubeListBuilder.create().texOffs(40, 32)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, outerDeformation),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        // 左ズボン - UV: 0,48
        partDefinition.addOrReplaceChild("left_pants",
                CubeListBuilder.create().texOffs(0, 48)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, outerDeformation),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        // 右ズボン - UV: 0,32
        partDefinition.addOrReplaceChild("right_pants",
                CubeListBuilder.create().texOffs(0, 32)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, outerDeformation),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        ModelPart root = partDefinition.bake(64, 64);
        this.jacket = root.getChild("jacket");
        this.leftSleeve = root.getChild("left_sleeve");
        this.rightSleeve = root.getChild("right_sleeve");
        this.leftPants = root.getChild("left_pants");
        this.rightPants = root.getChild("right_pants");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasCustomName()) {
            return;
        }

        String name = entity.getCustomName().getString();
        ResourceLocation texture = nameToTexture.get(name);

        if (texture == null) {
            return;
        }

        M model = this.getParentModel();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));

        // 帽子（頭の外側レイヤー）- HumanoidModelに存在
        model.hat.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // ジャケット（体の外側レイヤー）
        jacket.copyFrom(model.body);
        jacket.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // 左袖
        leftSleeve.copyFrom(model.leftArm);
        leftSleeve.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // 右袖
        rightSleeve.copyFrom(model.rightArm);
        rightSleeve.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // 左ズボン
        leftPants.copyFrom(model.leftLeg);
        leftPants.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // 右ズボン
        rightPants.copyFrom(model.rightLeg);
        rightPants.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
