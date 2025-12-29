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

public class TntSideTexturedMapItem extends MapItem {

    public TntSideTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("TntSideTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            itemStack.getOrCreateTag().putBoolean("TntSideTextured", true);

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

            initializeWithPattern(data);

            serverLevel.setMapData(mapName, data);

            stack.getOrCreateTag().putInt("map", mapId);
            stack.getOrCreateTag().putBoolean("TntSideTextured", true);
        }
    }

    private void initializeWithPattern(MapItemSavedData data) {
        byte redColor = (byte)(MapColor.FIRE.id * 4 + 2);
        byte whiteColor = (byte)(MapColor.SNOW.id * 4 + 2);
        byte darkRedColor = (byte)(MapColor.FIRE.id * 4 + 0);

        java.util.Random random = new java.util.Random(66666);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                boolean isWhiteBand = z >= 48 && z < 80;

                if (isWhiteBand) {
                    if (random.nextDouble() > 0.9) {
                        data.colors[x + z * 128] = (byte)(MapColor.SNOW.id * 4 + 1);
                    } else {
                        data.colors[x + z * 128] = whiteColor;
                    }
                } else {
                    if (random.nextDouble() > 0.8) {
                        data.colors[x + z * 128] = darkRedColor;
                    } else {
                        data.colors[x + z * 128] = redColor;
                    }
                }
            }
        }

        data.setDirty();
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
    }
}
