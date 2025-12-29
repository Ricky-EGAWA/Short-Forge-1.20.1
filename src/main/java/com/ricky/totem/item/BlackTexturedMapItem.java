package com.ricky.totem.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class BlackTexturedMapItem extends AbstractTexturedMapItem {

    public BlackTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "BlackTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // 真っ黒
        byte blackColor = (byte)(MapColor.COLOR_BLACK.id * 4 + 0);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                data.colors[x + z * 128] = blackColor;
            }
        }

        data.setDirty();
    }
}
