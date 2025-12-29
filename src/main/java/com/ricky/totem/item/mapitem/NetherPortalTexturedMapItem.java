package com.ricky.totem.item.mapitem;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class NetherPortalTexturedMapItem extends AbstractTexturedMapItem {

    public NetherPortalTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "NetherPortalTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // ネザーポータルの紫色
        byte[] portalColors = new byte[] {
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 0),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 1),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 2),
            (byte)(MapColor.COLOR_PURPLE.id * 4 + 3),
            (byte)(MapColor.COLOR_MAGENTA.id * 4 + 2)
        };

        java.util.Random random = new java.util.Random(88888);

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                // ポータルの渦巻きパターン
                double swirl = Math.sin(x * 0.1 + z * 0.05) + Math.cos(z * 0.08 - x * 0.03);
                int noiseValue = (int)((swirl + random.nextDouble() * 2) * 1.5);
                int colorIndex = Math.abs(noiseValue) % portalColors.length;
                data.colors[x + z * 128] = portalColors[colorIndex];
            }
        }

        data.setDirty();
    }
}
