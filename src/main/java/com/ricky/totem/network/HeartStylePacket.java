package com.ricky.totem.network;

import com.ricky.totem.client.HeartStyleTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * サーバーからクライアントにハートスタイルを同期するパケット
 */
public class HeartStylePacket {
    private final boolean hardcoreEnabled;

    public HeartStylePacket(boolean hardcoreEnabled) {
        this.hardcoreEnabled = hardcoreEnabled;
    }

    public HeartStylePacket(FriendlyByteBuf buf) {
        this.hardcoreEnabled = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(hardcoreEnabled);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // クライアント側で処理
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                HeartStyleTracker.setHardcoreHeartsEnabled(hardcoreEnabled);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
