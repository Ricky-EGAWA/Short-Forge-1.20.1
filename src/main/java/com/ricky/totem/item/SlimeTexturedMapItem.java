package com.ricky.totem.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class SlimeTexturedMapItem extends AbstractTexturedMapItem {

    public SlimeTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "SlimeTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // スライムの緑色
        byte[] slimeColors = new byte[] {
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 0),
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 1),
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 2),
            (byte)(MapColor.COLOR_LIGHT_GREEN.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(77777);

        // 中央の濃い部分（スライムの核）
        int coreStart = 40;
        int coreEnd = 88;

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                boolean isCore = x >= coreStart && x < coreEnd && z >= coreStart && z < coreEnd;

                if (isCore) {
                    // 中央は暗め
                    data.colors[x + z * 128] = slimeColors[(int)(random.nextDouble() * 2)];
                } else {
                    // 外側は明るめ
                    data.colors[x + z * 128] = slimeColors[2 + (int)(random.nextDouble() * 2)];
                }
            }
        }

        data.setDirty();
    }
}
