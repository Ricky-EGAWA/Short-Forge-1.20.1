package com.ricky.totem.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class TntSideTexturedMapItem extends AbstractTexturedMapItem {

    public TntSideTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "TntSideTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // TNTの赤い部分
        byte redColor = (byte)(MapColor.FIRE.id * 4 + 2);
        // TNTの白い帯部分
        byte whiteColor = (byte)(MapColor.SNOW.id * 4 + 2);
        // TNTの暗い赤
        byte darkRedColor = (byte)(MapColor.FIRE.id * 4 + 0);

        java.util.Random random = new java.util.Random(66666);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // 中央の白い帯（TNTのラベル部分）
                boolean isWhiteBand = z >= 48 && z < 80;

                if (isWhiteBand) {
                    // 白い帯に少しノイズを加える
                    if (random.nextDouble() > 0.9) {
                        data.colors[x + z * 128] = (byte)(MapColor.SNOW.id * 4 + 1);
                    } else {
                        data.colors[x + z * 128] = whiteColor;
                    }
                } else {
                    // 赤い部分
                    if (random.nextDouble() > 0.8) {
                        data.colors[x + z * 128] = darkRedColor;
                    } else {
                        data.colors[x + z * 128] = redColor;
                    }
                }
            }
        }

        data.setDirty();
    }
}
