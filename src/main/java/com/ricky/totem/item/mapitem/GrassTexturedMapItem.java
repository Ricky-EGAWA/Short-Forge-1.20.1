package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class GrassTexturedMapItem extends AbstractTexturedMapItem {

    public GrassTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "GrassTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        byte[] grassColors = new byte[] {
            (byte)(MapColor.GRASS.id * 4 + 0),
            (byte)(MapColor.GRASS.id * 4 + 1),
            (byte)(MapColor.GRASS.id * 4 + 2),
            (byte)(MapColor.GRASS.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(11111);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                int noiseValue = (int)((Math.sin(x * 0.2) + Math.cos(z * 0.2) + random.nextDouble()) * 1.5);
                int colorIndex = Math.abs(noiseValue) % grassColors.length;
                data.colors[x + z * 128] = grassColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
