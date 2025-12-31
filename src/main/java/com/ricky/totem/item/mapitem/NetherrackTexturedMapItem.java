package com.ricky.totem.item.mapitem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class NetherrackTexturedMapItem extends MapItem {

    public NetherrackTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("NetherrackTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // NetherrackTexturedタグを確実に設定
            itemStack.getOrCreateTag().putBoolean("NetherrackTextured", true);

            // 地図IDがない場合は作成
            if (!itemStack.hasTag() || !itemStack.getTag().contains("map")) {
                createMapData(itemStack, level);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide) {
            createMapData(stack, level);
        }
    }

    private void createMapData(ItemStack stack, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            // 新しい地図IDを作成
            Integer mapId = level.getFreeMapId();
            String mapName = MapItem.makeKey(mapId);

            // 地図データを作成
            MapItemSavedData data = MapItemSavedData.createFresh(
                serverLevel.getSharedSpawnPos().getX(),
                serverLevel.getSharedSpawnPos().getZ(),
                (byte)3,
                false,
                false,
                serverLevel.dimension()
            );

            // ネザーラックテクスチャで初期化
            initializeWithNetherrackPattern(data);

            // 地図データを保存
            serverLevel.setMapData(mapName, data);

            // アイテムに地図IDを設定し、カスタムマーカーを追加
            stack.getOrCreateTag().putInt("map", mapId);
            stack.getOrCreateTag().putBoolean("NetherrackTextured", true);
        }
    }

    private void initializeWithNetherrackPattern(MapItemSavedData data) {
        // ネザーラックのテクスチャパターンを地図の色で再現
        // 複数の赤茶色の色調を使用
        byte[] netherrackColors = new byte[] {
            (byte)(MapColor.NETHER.id * 4 + 0),  // 最も暗い
            (byte)(MapColor.NETHER.id * 4 + 1),  // やや暗い
            (byte)(MapColor.NETHER.id * 4 + 2),  // 通常
            (byte)(MapColor.NETHER.id * 4 + 3)   // やや明るい
        };

        // ランダムなパターンでネザーラックのテクスチャを模倣
        java.util.Random random = new java.util.Random(54321); // 固定シード

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // ノイズベースのパターンでネザーラックのテクスチャを再現
                int noiseValue = (int)((Math.sin(x * 0.15) + Math.cos(z * 0.12) + random.nextDouble()) * 1.5);
                int colorIndex = Math.abs(noiseValue) % netherrackColors.length;
                data.colors[x + z * 128] = netherrackColors[colorIndex];
            }
        }

        // 地図を変更済みとしてマーク
        data.setDirty();
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
        // 更新処理をスキップ（静的な地図）
    }
}
