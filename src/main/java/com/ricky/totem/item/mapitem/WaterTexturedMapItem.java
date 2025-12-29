package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class WaterTexturedMapItem extends AbstractTexturedMapItem {

    public WaterTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "WaterTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        byte[] waterColors = new byte[] {
            (byte)(MapColor.WATER.id * 4 + 0),
            (byte)(MapColor.WATER.id * 4 + 1),
            (byte)(MapColor.WATER.id * 4 + 2),
            (byte)(MapColor.WATER.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(33333);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                double wave = Math.sin(x * 0.15 + z * 0.1) + Math.sin(z * 0.12);
                int noiseValue = (int)((wave + random.nextDouble()) * 1.5);
                int colorIndex = Math.abs(noiseValue) % waterColors.length;
                data.colors[x + z * 128] = waterColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
