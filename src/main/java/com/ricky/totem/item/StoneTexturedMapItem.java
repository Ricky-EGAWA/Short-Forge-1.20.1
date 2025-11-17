package com.ricky.totem.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class StoneTexturedMapItem extends MapItem {

    public StoneTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
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

            // 石テクスチャで初期化
            initializeStoneTexture(data);

            // 地図データを保存
            serverLevel.setMapData(mapName, data);

            // アイテムに地図IDを設定
            stack.getOrCreateTag().putInt("map", mapId);
        }
    }

    private void initializeStoneTexture(MapItemSavedData data) {
        // 地図全体を石の色で塗りつぶす
        // MapColor.STONEのIDは12
        byte stoneColorId = 12;

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                data.colors[x + z * 128] = stoneColorId;
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
