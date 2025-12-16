package com.ricky.totem.mixin;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ZombieRendererのMixin
 * 「Micky」という名前のゾンビにカスタムテクスチャを適用
 */
@Mixin(ZombieRenderer.class)
public abstract class ZombieRendererMixin {

    @Unique
    private static final ResourceLocation MICKY_TEXTURE = new ResourceLocation(TotemItemsMod.MOD_ID, "textures/entity/zombie/micky.png");

    /**
     * getTextureLocationメソッドをインターセプト
     * ゾンビの名前が「Micky」の場合、カスタムテクスチャを返す
     */
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Zombie;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    private void onGetTextureLocation(Zombie zombie, CallbackInfoReturnable<ResourceLocation> cir) {
        if (zombie.hasCustomName()) {
            String name = zombie.getCustomName().getString();
            if ("Micky".equals(name)) {
                cir.setReturnValue(MICKY_TEXTURE);
            }
        }
    }
}
