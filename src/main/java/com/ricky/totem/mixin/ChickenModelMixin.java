package com.ricky.totem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ChickenModelのMixin
 * 「Donald」という名前の鶏をプレイヤーモデルでレンダリング
 */
@Mixin(ChickenModel.class)
public abstract class ChickenModelMixin<T extends Entity> {

    @Shadow @Final private ModelPart head;
    @Shadow @Final private ModelPart body;
    @Shadow @Final private ModelPart rightLeg;
    @Shadow @Final private ModelPart leftLeg;
    @Shadow @Final private ModelPart rightWing;
    @Shadow @Final private ModelPart leftWing;
    @Shadow @Final private ModelPart beak;
    @Shadow @Final private ModelPart redThing;

    @Unique
    private static PlayerModel<LivingEntity> totem$playerModel;

    @Unique
    private static boolean totem$initialized = false;

    @Unique
    private boolean totem$isDonald = false;

    @Unique
    private static void totem$ensurePlayerModelInitialized() {
        if (totem$initialized) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getEntityModels() != null) {
            totem$playerModel = new PlayerModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
            totem$initialized = true;
        }
    }

    /**
     * setupAnimの開始時に、Donaldかどうかをチェック
     */
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/animal/Chicken;FFFFF)V",
            at = @At("HEAD"))
    private void onSetupAnimStart(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        totem$isDonald = false;
        if (entity instanceof Chicken chicken) {
            if (chicken.hasCustomName() && "Donald".equals(chicken.getCustomName().getString())) {
                totem$isDonald = true;
                totem$ensurePlayerModelInitialized();

                // 元のパーツを非表示
                head.visible = false;
                body.visible = false;
                rightLeg.visible = false;
                leftLeg.visible = false;
                rightWing.visible = false;
                leftWing.visible = false;
                beak.visible = false;
                redThing.visible = false;

                // プレイヤーモデルのアニメーションを設定
                if (totem$playerModel != null) {
                    totem$playerModel.young = chicken.isBaby();
                    totem$playerModel.setupAnim((LivingEntity) entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                }
            }
        }
    }

    /**
     * renderToBufferをインターセプトして、Donaldの場合はプレイヤーモデルでレンダリング
     */
    @Inject(method = "renderToBuffer",
            at = @At("HEAD"),
            cancellable = true)
    private void onRenderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (totem$isDonald && totem$playerModel != null) {
            poseStack.pushPose();
            // 鶏のサイズに合わせてスケール調整
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0, 1.5F, 0);
            totem$playerModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
