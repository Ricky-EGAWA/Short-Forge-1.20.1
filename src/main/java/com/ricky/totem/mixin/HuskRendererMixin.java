package com.ricky.totem.mixin;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * HuskRendererのMixin
 * 「Donald」という名前のハスクにカスタムテクスチャを適用
 */
@Mixin(HuskRenderer.class)
public abstract class HuskRendererMixin {

    @Unique
    private static final ResourceLocation DONALD_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/husk/donald.png");

    /**
     * getTextureLocationメソッドをインターセプト
     * ハスクの名前が「Donald」の場合、カスタムテクスチャを返す
     */
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Zombie;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    private void onGetTextureLocation(Zombie zombie, CallbackInfoReturnable<ResourceLocation> cir) {
        if (zombie.hasCustomName()) {
            String name = zombie.getCustomName().getString();
            if ("Donald".equals(name)) {
                cir.setReturnValue(DONALD_TEXTURE);
            }
        }
    }
}
