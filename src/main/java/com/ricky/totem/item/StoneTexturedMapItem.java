package com.ricky.totem.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class StoneTexturedMapItem extends MapItem {
    private static final int STONE_MAP_ID = 999999; // 固定の地図ID

    public StoneTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("map", STONE_MAP_ID);
        return stack;
    }

    public static ItemStack create(Level level) {
        ItemStack stack = new ItemStack(ModItems.STONE_TEXTURED_MAP.get());
        stack.getOrCreateTag().putInt("map", STONE_MAP_ID);
        return stack;
    }

    @Nullable
    @Override
    public MapItemSavedData getSavedData(@Nullable Integer mapId, Level level) {
        // どの地図IDでも、石テクスチャの地図データを返す
        if (mapId == null) {
            mapId = STONE_MAP_ID;
        }

        // カスタムの地図データを作成
        String mapName = "map_stone_texture";
        MapItemSavedData data = null;

        if (level.getServer() != null) {
            data = level.getServer().overworld().getDataStorage().get(MapItemSavedData.factory(), mapName);

            if (data == null) {
                data = MapItemSavedData.createFresh(0.0, 0.0, (byte)3, false, false, level.dimension());
                initializeStoneTexture(data);
                level.getServer().overworld().getDataStorage().set(mapName, data);
            }
        }

        return data;
    }

    private void initializeStoneTexture(MapItemSavedData data) {
        // 地図全体を石の色で塗りつぶす
        // 石のMapColorを使用
        byte stoneColor = MapColor.STONE.id;

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                data.colors[x + z * 128] = stoneColor;
            }
        }
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
        // 更新処理をスキップ（静的な地図）
    }
}
