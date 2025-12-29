package com.ricky.totem.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class DiamondOreTexturedMapItem extends AbstractTexturedMapItem {

    public DiamondOreTexturedMapItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getTextureTag() {
        return "DiamondOreTextured";
    }

    @Override
    protected void initializePattern(MapItemSavedData data) {
        // 石の背景色
        byte[] stoneColors = new byte[] {
            (byte)(MapColor.STONE.id * 4 + 0),
            (byte)(MapColor.STONE.id * 4 + 1),
            (byte)(MapColor.STONE.id * 4 + 2),
            (byte)(MapColor.STONE.id * 4 + 3)
        };

        // ダイヤモンドの色
        byte[] diamondColors = new byte[] {
            (byte)(MapColor.DIAMOND.id * 4 + 1),
            (byte)(MapColor.DIAMOND.id * 4 + 2),
            (byte)(MapColor.DIAMOND.id * 4 + 3)
        };

        java.util.Random random = new java.util.Random(101010);

        // ダイヤモンドの位置をランダムに配置
        boolean[][] isDiamond = new boolean[128][128];
        for (int i = 0; i < 40; i++) {
            int dx = random.nextInt(120) + 4;
            int dz = random.nextInt(120) + 4;
            // 各ダイヤは小さな塊として配置
            for (int ox = -2; ox <= 2; ox++) {
                for (int oz = -2; oz <= 2; oz++) {
                    if (random.nextDouble() > 0.3) {
                        int nx = dx + ox;
                        int nz = dz + oz;
                        if (nx >= 0 && nx < 128 && nz >= 0 && nz < 128) {
                            isDiamond[nx][nz] = true;
                        }
                    }
                }
            }
        }

        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                if (isDiamond[x][z]) {
                    data.colors[x + z * 128] = diamondColors[random.nextInt(diamondColors.length)];
                } else {
                    int noiseValue = (int)((Math.sin(x * 0.1) + Math.cos(z * 0.1) + random.nextDouble()) * 1.5);
                    data.colors[x + z * 128] = stoneColors[Math.abs(noiseValue) % stoneColors.length];
                }
            }
        }

        data.setDirty();
    }
}
