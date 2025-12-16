package com.ricky.totem.mixin;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EndermanModelのMixin
 * 「Minnie」という名前のエンダーマンのパーツを非表示にする
 */
@Mixin(EndermanModel.class)
public abstract class EndermanModelMixin<T extends LivingEntity> {

    // HumanoidModelから継承されているフィールド
    @Shadow public ModelPart head;
    @Shadow public ModelPart body;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart leftLeg;
    @Shadow public ModelPart hat;

    /**
     * setupAnimの終了時に、Minnieの場合はパーツを非表示にする
     */
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("TAIL"))
    private void onSetupAnimEnd(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof EnderMan enderman) {
            if (enderman.hasCustomName() && "Minnie".equals(enderman.getCustomName().getString())) {
                // 元のパーツを非表示
                head.visible = false;
                body.visible = false;
                rightArm.visible = false;
                leftArm.visible = false;
                rightLeg.visible = false;
                leftLeg.visible = false;
                hat.visible = false;
            }
        }
    }
}
