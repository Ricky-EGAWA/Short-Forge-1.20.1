package com.ricky.totem.item;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.world.item.Items.BUCKET;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TotemItemsMod.MOD_ID);

    public static final FoodProperties COOKED_BEEF_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8F)
            .meat()
            .build();

    public static final RegistryObject<Item> TOTEM_WATER_BUCKET = ITEMS.register("totem_water_bucket",
            () -> new BucketItem(Fluids.WATER,(new Item.Properties()).craftRemainder(BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> TOTEM_COOKED_BEEF = ITEMS.register("totem_cooked_beef",
            () -> new Item(new Item.Properties().stacksTo(1).food(COOKED_BEEF_FOOD)));

    public static final RegistryObject<Item> TOTEM_SWORD = ITEMS.register("totem_sword",
            () -> new SwordItem(Tiers.DIAMOND, 3, -2.4F,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TOTEM_PICKAXE = ITEMS.register("totem_pickaxe",
            () -> new PickaxeItem(Tiers.DIAMOND, 1, -2.8F,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WATER_BUCKET = ITEMS.register("water_bucket",
            () -> new EdibleBucketItem(new Item.Properties().food(ModFoodProperties.WATER_BUCKET)));
    public static final RegistryObject<Item> LAVA_BUCKET = ITEMS.register("lava_bucket",
            () -> new EdibleBucketItem(new Item.Properties().food(ModFoodProperties.LAVA_BUCKET)));

    public static final RegistryObject<Item> STONE_TEXTURED_MAP = ITEMS.register("stone_textured_map",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
