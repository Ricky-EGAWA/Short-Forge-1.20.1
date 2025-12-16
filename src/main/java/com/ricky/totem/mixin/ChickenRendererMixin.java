package com.ricky.totem.mixin;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ChickenRendererのMixin
 * 「Donald」という名前の鶏にカスタムテクスチャを適用
 */
@Mixin(ChickenRenderer.class)
public abstract class ChickenRendererMixin {

    @Unique
    private static final ResourceLocation DONALD_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/chicken/donald.png");

    /**
     * getTextureLocationメソッドをインターセプト
     * 鶏の名前が「Donald」の場合、カスタムテクスチャを返す
     */
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Chicken;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    private void onGetTextureLocation(Chicken chicken, CallbackInfoReturnable<ResourceLocation> cir) {
        if (chicken.hasCustomName()) {
            String name = chicken.getCustomName().getString();
            if ("Donald".equals(name)) {
                cir.setReturnValue(DONALD_TEXTURE);
            }
        }
    }
}
