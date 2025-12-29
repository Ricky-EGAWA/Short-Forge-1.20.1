package com.ricky.totem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ItemFrameRendererのMixin
 * 石テクスチャ/ネザーラックテクスチャの地図が入った額縁を
 * 裏側から見たときに透明にする
 */
@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin<T extends ItemFrame> {

    /**
     * renderメソッドの先頭でインターセプト
     * 特定の地図アイテムが入っている場合、裏側から見ているときはレンダリングをキャンセル
     */
    @Inject(method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRender(T itemFrame, float entityYaw, float partialTicks, PoseStack poseStack,
                          MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        ItemStack stack = itemFrame.getItem();

        // テクスチャ地図かチェック（すべてのテクスチャ地図タグを確認）
        if (stack.hasTag() && isTexturedMap(stack)) {

            // カメラの位置を取得
            Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

            // 額縁の位置を取得
            Vec3 framePos = itemFrame.position();

            // 額縁の向き（法線方向）を取得
            Direction facing = itemFrame.getDirection();
            Vec3 normal = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

            // カメラから額縁への方向ベクトル
            Vec3 toCamera = cameraPos.subtract(framePos).normalize();

            // 内積を計算（正なら表側、負なら裏側から見ている）
            double dot = normal.dot(toCamera);

            // 裏側から見ている場合はレンダリングをキャンセル
            if (dot < 0) {
                ci.cancel();
            }
        }
    }

    /**
     * スタックがテクスチャ地図かどうかをチェック
     */
    private boolean isTexturedMap(ItemStack stack) {
        if (!stack.hasTag()) return false;
        var tag = stack.getTag();
        return tag.getBoolean("StoneTextured") ||
               tag.getBoolean("NetherrackTextured") ||
               tag.getBoolean("GrassTextured") ||
               tag.getBoolean("LavaTextured") ||
               tag.getBoolean("WaterTextured") ||
               tag.getBoolean("OakPlanksTextured") ||
               tag.getBoolean("SandstonePressurePlateTextured") ||
               tag.getBoolean("TntSideTextured") ||
               tag.getBoolean("SlimeTextured") ||
               tag.getBoolean("BlackTextured") ||
               tag.getBoolean("NetherPortalTextured") ||
               tag.getBoolean("EndPortalTextured") ||
               tag.getBoolean("DiamondOreTextured") ||
               tag.getBoolean("DiamondBlockTextured") ||
               tag.getBoolean("ObsidianTextured") ||
               tag.getBoolean("StoneLadderTextured");
    }
}
