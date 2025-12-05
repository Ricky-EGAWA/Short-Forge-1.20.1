package com.ricky.totem.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.ricky.totem.network.ModNetworkHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Collections;

/**
 * Modのコマンドを登録するクラス
 */
@Mod.EventBusSubscriber(modid = "totem")
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        registerHardcoreHeartsCommand(dispatcher);
    }

    /**
     * /hardcorehearts コマンドを登録
     * 使用法:
     *   /hardcorehearts <true|false> - 自分のハートスタイルを設定
     *   /hardcorehearts <true|false> <players> - 指定プレイヤーのハートスタイルを設定
     */
    private static void registerHardcoreHeartsCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("hardcorehearts")
                .requires(source -> source.hasPermission(2)) // OP権限が必要
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                    // /hardcorehearts <true|false> - 自分に適用
                    .executes(context -> {
                        boolean enabled = BoolArgumentType.getBool(context, "enabled");
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        return setHardcoreHearts(context.getSource(), Collections.singleton(player), enabled);
                    })
                    // /hardcorehearts <true|false> <players> - 指定プレイヤーに適用
                    .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                            return setHardcoreHearts(context.getSource(), targets, enabled);
                        })
                    )
                )
        );
    }

    /**
     * ハートスタイルを設定してクライアントに同期
     */
    private static int setHardcoreHearts(CommandSourceStack source, Collection<ServerPlayer> players, boolean enabled) {
        for (ServerPlayer player : players) {
            ModNetworkHandler.sendHeartStyle(player, enabled);
        }

        String status = enabled ? "有効" : "無効";
        if (players.size() == 1) {
            ServerPlayer target = players.iterator().next();
            source.sendSuccess(() -> Component.literal(
                    target.getName().getString() + " のハードコアハートを" + status + "にしました"
            ), true);
        } else {
            source.sendSuccess(() -> Component.literal(
                    players.size() + " 人のプレイヤーのハードコアハートを" + status + "にしました"
            ), true);
        }

        return players.size();
    }
}
