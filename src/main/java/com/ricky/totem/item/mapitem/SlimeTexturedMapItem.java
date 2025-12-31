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

public class SlimeTexturedMapItem extends MapItem {

    public SlimeTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("SlimeTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            itemStack.getOrCreateTag().putBoolean("SlimeTextured", true);

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
            stack.getOrCreateTag().putBoolean("SlimeTextured", true);
        }
    }

    private void initializeWithPattern(MapItemSavedData data) {
        byte[] colors = new byte[] {
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 0),
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 1),
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 2),
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(77777);

        int coreStart = 40;
        int coreEnd = 88;

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                boolean isCore = x >= coreStart && x < coreEnd && z >= coreStart && z < coreEnd;

                if (isCore) {
                    data.colors[x + z * 128] = colors[(int)(random.nextDouble() * 2)];
                } else {
                    data.colors[x + z * 128] = colors[2 + (int)(random.nextDouble() * 2)];
                }
            }
        }

        data.setDirty();
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
    }
}
