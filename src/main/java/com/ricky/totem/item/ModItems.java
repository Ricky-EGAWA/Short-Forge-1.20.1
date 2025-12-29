package com.ricky.totem.item;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.item.mapitem.*;
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
            () -> new Item(new Item.Properties().stacksTo(64).food(COOKED_BEEF_FOOD)));

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
            () -> new StoneTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> NETHERRACK_TEXTURED_MAP = ITEMS.register("netherrack_textured_map",
            () -> new NetherrackTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> GRASS_TEXTURED_MAP = ITEMS.register("grass_textured_map",
            () -> new GrassTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> LAVA_TEXTURED_MAP = ITEMS.register("lava_textured_map",
            () -> new LavaTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> WATER_TEXTURED_MAP = ITEMS.register("water_textured_map",
            () -> new WaterTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> OAK_PLANKS_TEXTURED_MAP = ITEMS.register("oak_planks_textured_map",
            () -> new OakPlanksTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> SANDSTONE_PRESSURE_PLATE_TEXTURED_MAP = ITEMS.register("sandstone_pressure_plate_textured_map",
            () -> new SandstonePressurePlateTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> TNT_SIDE_TEXTURED_MAP = ITEMS.register("tnt_side_textured_map",
            () -> new TntSideTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> SLIME_TEXTURED_MAP = ITEMS.register("slime_textured_map",
            () -> new SlimeTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> BLACK_TEXTURED_MAP = ITEMS.register("black_textured_map",
            () -> new BlackTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> NETHER_PORTAL_TEXTURED_MAP = ITEMS.register("nether_portal_textured_map",
            () -> new NetherPortalTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> END_PORTAL_TEXTURED_MAP = ITEMS.register("end_portal_textured_map",
            () -> new EndPortalTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> DIAMOND_ORE_TEXTURED_MAP = ITEMS.register("diamond_ore_textured_map",
            () -> new DiamondOreTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> DIAMOND_BLOCK_TEXTURED_MAP = ITEMS.register("diamond_block_textured_map",
            () -> new DiamondBlockTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> OBSIDIAN_TEXTURED_MAP = ITEMS.register("obsidian_textured_map",
            () -> new ObsidianTexturedMapItem(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
