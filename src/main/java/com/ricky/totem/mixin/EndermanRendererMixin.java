package com.ricky.totem.mixin;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EndermanRendererのMixin
 * 「Minnie」という名前のエンダーマンにカスタムテクスチャを適用
 */
@Mixin(EndermanRenderer.class)
public abstract class EndermanRendererMixin {

    @Unique
    private static final ResourceLocation MINNIE_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/enderman/minnie.png");

    /**
     * getTextureLocationメソッドをインターセプト
     * エンダーマンの名前が「Minnie」の場合、カスタムテクスチャを返す
     */
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/EnderMan;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    private void onGetTextureLocation(EnderMan enderman, CallbackInfoReturnable<ResourceLocation> cir) {
        if (enderman.hasCustomName()) {
            String name = enderman.getCustomName().getString();
            if ("Minnie".equals(name)) {
                cir.setReturnValue(MINNIE_TEXTURE);
            }
        }
    }
}
