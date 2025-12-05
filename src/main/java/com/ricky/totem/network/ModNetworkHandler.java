package com.ricky.totem.network;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Modのネットワーク通信を管理するハンドラー
 */
public class ModNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TotemItemsMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.messageBuilder(TotemActivationPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(TotemActivationPacket::new)
                .encoder(TotemActivationPacket::encode)
                .consumerMainThread(TotemActivationPacket::handle)
                .add();

        INSTANCE.messageBuilder(HeartStylePacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(HeartStylePacket::new)
                .encoder(HeartStylePacket::encode)
                .consumerMainThread(HeartStylePacket::handle)
                .add();
    }

    /**
     * プレイヤーにトーテム発動パケットを送信
     */
    public static void sendToPlayer(ServerPlayer player, ResourceLocation textureLocation) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new TotemActivationPacket(textureLocation));
    }

    /**
     * 指定したプレイヤーの周囲のプレイヤーにパケットを送信
     */
    public static void sendToTracking(ServerPlayer player, ResourceLocation textureLocation) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new TotemActivationPacket(textureLocation));
    }

    /**
     * プレイヤーにハートスタイルパケットを送信
     */
    public static void sendHeartStyle(ServerPlayer player, boolean hardcoreEnabled) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new HeartStylePacket(hardcoreEnabled));
    }
}
