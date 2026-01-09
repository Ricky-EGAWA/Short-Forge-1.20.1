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

public class DiamondOreTexturedMapItem extends MapItem {

    public DiamondOreTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTag().putBoolean("DiamondOreTextured", true);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            itemStack.getOrCreateTag().putBoolean("DiamondOreTextured", true);

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
            stack.getOrCreateTag().putBoolean("DiamondOreTextured", true);
        }
    }

    private void initializeWithPattern(MapItemSavedData data) {
        // より明るい色のみを使用
        byte[] stoneColors = new byte[] {
            (byte)(MapColor.STONE.id * 4 + 2),
            (byte)(MapColor.STONE.id * 4 + 3),
            (byte)(MapColor.STONE.id * 4 + 3),
            (byte)(MapColor.STONE.id * 4 + 2)
        };

        byte[] diamondColors = new byte[] {
            (byte)(MapColor.DIAMOND.id * 4 + 2),
            (byte)(MapColor.DIAMOND.id * 4 + 3),
            (byte)(MapColor.DIAMOND.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(101010);

        boolean[][] isDiamond = new boolean[128][128];
        for (int i = 0; i < 40; i++) {
            int dx = random.nextInt(120) + 4;
            int dz = random.nextInt(120) + 4;
            for (int ox = -2; ox <= 2; ox++) {
                for (int oz = -2; oz <= 2; oz++) {
                    if (random.nextDouble() > 0.3) {
                        int nx = dx + ox;
                        int nz = dz + oz;
                        if (nx >= 0 && nx < 128 && nz >= 0 && nz < 128) {
                            isDiamond[nx][nz] = true;
                        }
                    }
                }
            }
        }

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                if (isDiamond[x][z]) {
                    data.colors[x + z * 128] = diamondColors[random.nextInt(diamondColors.length)];
                } else {
                    int noiseValue = (int)((Math.sin(x * 0.1) + Math.cos(z * 0.1) + random.nextDouble()) * 1.5);
                    data.colors[x + z * 128] = stoneColors[Math.abs(noiseValue) % stoneColors.length];
                }
            }
        }

        data.setDirty();
    }

    @Override
    public void update(Level level, net.minecraft.world.entity.Entity entity, MapItemSavedData data) {
    }
}
