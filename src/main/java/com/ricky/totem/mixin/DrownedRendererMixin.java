package com.ricky.totem.mixin;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * DrownedRendererのMixin
 * 「Minnie」という名前のドラウンドにカスタムテクスチャを適用
 */
@Mixin(DrownedRenderer.class)
public abstract class DrownedRendererMixin {

    @Unique
    private static final ResourceLocation MINNIE_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/drowned/minnie.png");

    /**
     * getTextureLocationメソッドをインターセプト
     * ドラウンドの名前が「Minnie」の場合、カスタムテクスチャを返す
     */
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Drowned;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    private void onGetTextureLocation(Drowned drowned, CallbackInfoReturnable<ResourceLocation> cir) {
        if (drowned.hasCustomName()) {
            String name = drowned.getCustomName().getString();
            if ("Minnie".equals(name)) {
                cir.setReturnValue(MINNIE_TEXTURE);
            }
        }
    }
}
