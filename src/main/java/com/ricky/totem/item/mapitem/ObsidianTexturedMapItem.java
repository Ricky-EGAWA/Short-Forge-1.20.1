package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ObsidianTexturedMapItem extends AbstractTexturedMapItem {

    public ObsidianTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "ObsidianTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // 黒曜石の暗い紫/黒色
        byte[] obsidianColors = new byte[] {
            (byte)(MapColor.COLOR_BLACK.id * 4 + 0),
            (byte)(MapColor.COLOR_BLACK.id * 4 + 1),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 0),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 1)
        };

        java.util.Random random = new java.util.Random(121212);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // 黒曜石の光沢パターン
                double pattern = Math.sin(x * 0.08) * Math.cos(z * 0.08) + random.nextDouble() * 0.5;
                int colorIndex;
                if (pattern > 0.8) {
                    // 時々紫の光沢
                    colorIndex = 2 + (int)(random.nextDouble() * 2);
                } else {
                    colorIndex = (int)(random.nextDouble() * 2);
                }
                data.colors[x + z * 128] = obsidianColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
