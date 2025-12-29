package com.ricky.totem.item.mapitem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public abstract class AbstractTexturedMapItem extends MapItem {

    public AbstractTexturedMapItem(Properties properties) {
        super(properties);
    }

    protected abstract String getTextureTag();
    protected abstract void initializePattern(MapItemSavedData data);

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean(getTextureTag(), true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            itemStack.getOrCreateTag().putBoolean(getTextureTag(), true);

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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // 地図IDがない場合は作成（クリエイティブモードで取得したアイテム用）
        if (!level.isClientSide && (!stack.hasTag() || !stack.getTag().contains("map"))) {
            createMapData(stack, level);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private void createMapData(ItemStack stack, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            Integer mapId = level.getFreeMapId();
            String mapName = MapItem.makeKey(mapId);

            MapItemSavedData data = MapItemSavedData.createFresh(
                serverLevel.getSharedSpawnPos().getX(),
                serverLevel.getSharedSpawnPos().getZ(),
                (byte)3,
                false,
                false,
                serverLevel.dimension()
            );

            initializePattern(data);

            serverLevel.setMapData(mapName, data);

            stack.getOrCreateTag().putInt("map", mapId);
            stack.getOrCreateTag().putBoolean(getTextureTag(), true);
        }
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
        // 更新処理をスキップ（静的な地図）
    }
}
