package com.ricky.totem.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties WATER_BUCKET = new FoodProperties.Builder()
            .nutrition(0)
            .saturationMod(0.0F)
            .build();

    public static final FoodProperties LAVA_BUCKET = new FoodProperties.Builder()
            .nutrition(0)
            .saturationMod(0.0F)
            .build();
    public static final FoodProperties FOOD = new FoodProperties.Builder()
            .nutrition(0)
            .saturationMod(0.0F)
            .build();
}