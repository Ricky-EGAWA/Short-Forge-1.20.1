package com.ricky.totem.network;

import com.ricky.totem.client.TotemTextureTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * サーバーからクライアントにトーテム発動時のアイテムテクスチャ情報を送信するパケット
 */
public class TotemActivationPacket {
    private final ResourceLocation textureLocation;

    public TotemActivationPacket(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }

    public TotemActivationPacket(FriendlyByteBuf buf) {
        this.textureLocation = buf.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(textureLocation);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // クライアント側で処理
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                TotemTextureTracker.setCustomTotemTexture(textureLocation);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
