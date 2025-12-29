package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class OakPlanksTexturedMapItem extends AbstractTexturedMapItem {

    public OakPlanksTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "OakPlanksTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        byte[] woodColors = new byte[] {
            (byte)(MapColor.WOOD.id * 4 + 0),
            (byte)(MapColor.WOOD.id * 4 + 1),
            (byte)(MapColor.WOOD.id * 4 + 2),
            (byte)(MapColor.WOOD.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(44444);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // 木目パターン（横縞）
                int stripeValue = (z / 8) % 2;
                int noiseValue = stripeValue + (int)(random.nextDouble() * 2);
                int colorIndex = Math.abs(noiseValue) % woodColors.length;
                data.colors[x + z * 128] = woodColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
