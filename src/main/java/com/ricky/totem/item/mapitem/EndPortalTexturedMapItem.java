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

public class EndPortalTexturedMapItem extends MapItem {

    public EndPortalTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("EndPortalTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            itemStack.getOrCreateTag().putBoolean("EndPortalTextured", true);

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
            stack.getOrCreateTag().putBoolean("EndPortalTextured", true);
        }
    }

    private void initializeWithPattern(MapItemSavedData data) {
        byte[] colors = new byte[] {
            (byte)(MapColor.COLOR_BLACK.id * 4 + 0),
            (byte)(MapColor.COLOR_BLACK.id * 4 + 1),
            (byte)(MapColor.TERRACOTTA_CYAN.id * 4 + 0),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 0),
            (byte)(MapColor.COLOR_GREEN.id * 4 + 0)
        };

        java.util.Random random = new java.util.Random(99999);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                double pattern = Math.sin(x * 0.05) * Math.cos(z * 0.05) + random.nextDouble();
                int colorIndex;
                if (random.nextDouble() > 0.95) {
                    colorIndex = 2 + (int)(random.nextDouble() * 3);
                } else {
                    colorIndex = (int)(Math.abs(pattern) * 2) % 2;
                }
                data.colors[x + z * 128] = colors[colorIndex % colors.length];
            }
        }

        data.setDirty();
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
    }
}
