package com.ricky.totem.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class LavaTexturedMapItem extends AbstractTexturedMapItem {

    public LavaTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "LavaTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        byte[] lavaColors = new byte[] {
            (byte)(MapColor.FIRE.id * 4 + 0),
            (byte)(MapColor.FIRE.id * 4 + 1),
            (byte)(MapColor.FIRE.id * 4 + 2),
            (byte)(MapColor.FIRE.id * 4 + 3),
            (byte)(MapColor.COLOR_ORANGE.id * 4 + 2),
            (byte)(MapColor.COLOR_YELLOW.id * 4 + 2)
        };

        java.util.Random random = new java.util.Random(22222);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                double wave = Math.sin(x * 0.1 + z * 0.05) + Math.cos(z * 0.08);
                int noiseValue = (int)((wave + random.nextDouble() * 2) * 1.5);
                int colorIndex = Math.abs(noiseValue) % lavaColors.length;
                data.colors[x + z * 128] = lavaColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
