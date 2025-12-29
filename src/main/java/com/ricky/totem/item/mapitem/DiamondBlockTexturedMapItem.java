package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class DiamondBlockTexturedMapItem extends AbstractTexturedMapItem {

    public DiamondBlockTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "DiamondBlockTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // ダイヤモンドブロックの色
        byte[] diamondColors = new byte[] {
            (byte)(MapColor.DIAMOND.id * 4 + 0),
            (byte)(MapColor.DIAMOND.id * 4 + 1),
            (byte)(MapColor.DIAMOND.id * 4 + 2),
            (byte)(MapColor.DIAMOND.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(111111);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // ダイヤモンドブロックの輝きパターン
                double shine = Math.sin(x * 0.2 + z * 0.15) + Math.cos(z * 0.18);
                int noiseValue = (int)((shine + random.nextDouble()) * 1.5);
                int colorIndex = Math.abs(noiseValue) % diamondColors.length;
                data.colors[x + z * 128] = diamondColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
