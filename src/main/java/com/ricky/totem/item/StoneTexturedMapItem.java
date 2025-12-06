package com.ricky.totem.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class StoneTexturedMapItem extends MapItem {

    public StoneTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("StoneTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // StoneTexturedタグを確実に設定
            itemStack.getOrCreateTag().putBoolean("StoneTextured", true);

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

            // 石テクスチャで初期化（地図の色パレットを使用）
            initializeWithStonePattern(data);

            // 地図データを保存
            serverLevel.setMapData(mapName, data);

            // アイテムに地図IDを設定し、カスタムマーカーを追加
            stack.getOrCreateTag().putInt("map", mapId);
            stack.getOrCreateTag().putBoolean("StoneTextured", true);
        }
    }

    private void initializeWithStonePattern(MapItemSavedData data) {
        // 石のテクスチャパターンを地図の色で再現
        // 地図は128x128ピクセル

        // DEEPSLATEはSTONEより暗い色
        // brightness: 0 = 最も暗い(71%), 1 = やや暗い(86%)
        byte[] stoneColors = new byte[] {
            (byte)(MapColor.DEEPSLATE.id * 4 + 0),  // 深層岩 最も暗い
            (byte)(MapColor.DEEPSLATE.id * 4 + 0),  // 深層岩 最も暗い
            (byte)(MapColor.DEEPSLATE.id * 4 + 1),  // 深層岩 やや暗い
        };

        // ランダムなパターンで石のテクスチャを模倣
        java.util.Random random = new java.util.Random(12345); // 固定シード

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // ノイズベースのパターンで石のテクスチャを再現
                int noiseValue = (int)((Math.sin(x * 0.1) + Math.cos(z * 0.1) + random.nextDouble()) * 1.5);
                int colorIndex = Math.abs(noiseValue) % stoneColors.length;
                data.colors[x + z * 128] = stoneColors[colorIndex];
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
