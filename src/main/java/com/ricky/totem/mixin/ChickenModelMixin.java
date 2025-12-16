package com.ricky.totem.mixin;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ChickenModelのMixin
 * 「Donald」という名前の鶏のパーツを非表示にする
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

    /**
     * setupAnimの終了時に、Donaldの場合はパーツを非表示にする
     */
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            at = @At("TAIL"))
    private void onSetupAnimEnd(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Chicken chicken) {
            if (chicken.hasCustomName() && "Donald".equals(chicken.getCustomName().getString())) {
                // 元のパーツを非表示
                head.visible = false;
                body.visible = false;
                rightLeg.visible = false;
                leftLeg.visible = false;
                rightWing.visible = false;
                leftWing.visible = false;
                beak.visible = false;
                redThing.visible = false;
            }
        }
    }
}
