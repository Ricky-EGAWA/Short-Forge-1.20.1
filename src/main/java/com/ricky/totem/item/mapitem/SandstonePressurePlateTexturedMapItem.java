package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class SandstonePressurePlateTexturedMapItem extends AbstractTexturedMapItem {

    public SandstonePressurePlateTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "SandstonePressurePlateTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // 砂岩の背景色
        byte[] sandstoneColors = new byte[] {
            (byte)(MapColor.SAND.id * 4 + 1),
            (byte)(MapColor.SAND.id * 4 + 2),
            (byte)(MapColor.SAND.id * 4 + 3)
        };

        // 石の感圧版の色
        byte[] stoneColors = new byte[] {
            (byte)(MapColor.STONE.id * 4 + 0),
            (byte)(MapColor.STONE.id * 4 + 1),
            (byte)(MapColor.STONE.id * 4 + 2)
        };

        java.util.Random random = new java.util.Random(55555);

        // 中央に感圧版を配置
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
}
