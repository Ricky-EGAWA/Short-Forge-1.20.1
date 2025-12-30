package com.ricky.totem.block;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.item.EdibleBlockItem;
import com.ricky.totem.item.ModFoodProperties;
import com.ricky.totem.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TotemItemsMod.MOD_ID);

    public static final RegistryObject<Block> OBSIDIAN = registerEdibleBlockItem("obsidian",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)));

    public static final RegistryObject<Block> DIAMOND8 = registerEdibleBlockItem("diamond8",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)));

    public static final RegistryObject<Block> STONE104 = registerBlock("stone104",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> STONE116 = registerBlock("stone116",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> STONE127 = registerBlock("stone127",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> STONE143 = registerBlock("stone143",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> MAGMA_STONE = registerBlock("magma_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // 岩盤の見た目、石の性質
    public static final RegistryObject<Block> BEDROCK_STONE = registerBlock("bedrock_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // 鉄のドアの見た目、オークのドアの性質
    public static final RegistryObject<Block> FAKE_IRON_DOOR = registerBlock("fake_iron_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).noOcclusion(), BlockSetType.OAK));

    // 鉄のトラップドアの見た目、オークのトラップドアの性質
    public static final RegistryObject<Block> FAKE_IRON_TRAPDOOR = registerBlock("fake_iron_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).noOcclusion(), BlockSetType.OAK));

    // 砂の見た目、土の性質
    public static final RegistryObject<Block> SAND_DIRT = registerBlock("sand_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));

    // 砂利の見た目、土の性質
    public static final RegistryObject<Block> GRAVEL_DIRT = registerBlock("gravel_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerEdibleBlockItem(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerEdibleBlockItem(name, toReturn);
        return toReturn;
    }

    // 通常のBlockItem登録
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // 食べられるBlockItem登録（黒曜石用）
    private static <T extends Block> RegistryObject<Item> registerEdibleBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new EdibleBlockItem(block.get(),
                new Item.Properties().food(ModFoodProperties.FOOD)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}