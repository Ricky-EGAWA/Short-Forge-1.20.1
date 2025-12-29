package com.ricky.totem.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class EndPortalTexturedMapItem extends AbstractTexturedMapItem {

    public EndPortalTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "EndPortalTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // エンドポータルの暗い色（黒、深緑、紫の混合）
        byte[] endColors = new byte[] {
            (byte)(MapColor.COLOR_BLACK.id * 4 + 0),
            (byte)(MapColor.COLOR_BLACK.id * 4 + 1),
            (byte)(MapColor.TERRACOTTA_CYAN.id * 4 + 0),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 0),
            (byte)(MapColor.COLOR_GREEN.id * 4 + 0)
        };

        java.util.Random random = new java.util.Random(99999);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // 星空のようなパターン
                double pattern = Math.sin(x * 0.05) * Math.cos(z * 0.05) + random.nextDouble();
                int colorIndex;
                if (random.nextDouble() > 0.95) {
                    // たまに明るい点（星）
                    colorIndex = 2 + (int)(random.nextDouble() * 3);
                } else {
                    colorIndex = (int)(Math.abs(pattern) * 2) % 2;
                }
                data.colors[x + z * 128] = endColors[colorIndex % endColors.length];
            }
        }

        data.setDirty();
    }
}
