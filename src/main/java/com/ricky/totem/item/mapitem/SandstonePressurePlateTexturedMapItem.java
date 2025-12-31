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

public class SandstonePressurePlateTexturedMapItem extends MapItem {

    public SandstonePressurePlateTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("SandstonePressurePlateTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            itemStack.getOrCreateTag().putBoolean("SandstonePressurePlateTextured", true);

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
            stack.getOrCreateTag().putBoolean("SandstonePressurePlateTextured", true);
        }
    }

    private void initializeWithPattern(MapItemSavedData data) {
        byte[] sandstoneColors = new byte[] {
            (byte)(MapColor.SAND.id * 4 + 1),
            (byte)(MapColor.SAND.id * 4 + 2),
            (byte)(MapColor.SAND.id * 4 + 3)
        };

        byte[] stoneColors = new byte[] {
            (byte)(MapColor.STONE.id * 4 + 0),
            (byte)(MapColor.STONE.id * 4 + 1),
            (byte)(MapColor.STONE.id * 4 + 2)
        };

        java.util.Random random = new java.util.Random(55555);

        int plateStart = 32;
        int plateEnd = 96;

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                boolean isPlate = x >= plateStart && x < plateEnd && z >= plateStart && z < plateEnd;

                if (isPlate) {
                    int noiseValue = (int)(random.nextDouble() * 3);
                    data.colors[x + z * 128] = stoneColors[noiseValue % stoneColors.length];
                } else {
                    int noiseValue = (int)((Math.sin(x * 0.1) + Math.cos(z * 0.1) + random.nextDouble()) * 1.5);
                    data.colors[x + z * 128] = sandstoneColors[Math.abs(noiseValue) % sandstoneColors.length];
                }
            }
        }

        data.setDirty();
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
    }
}
