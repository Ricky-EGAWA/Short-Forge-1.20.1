package com.ricky.totem.item;

import com.ricky.totem.TotemItemsMod;
import com.ricky.totem.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TotemItemsMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TOTEM_TAB = CREATIVE_MODE_TABS.register("totem_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.TOTEM_OF_UNDYING))
                    .title(Component.translatable("creativetab.totem_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.TOTEM_COOKED_BEEF.get());
                        pOutput.accept(ModItems.TOTEM_WATER_BUCKET.get());
                        pOutput.accept(ModItems.TOTEM_SWORD.get());
                        pOutput.accept(ModItems.TOTEM_PICKAXE.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> EDIBLE_TAB = CREATIVE_MODE_TABS.register("edible_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.COOKED_BEEF))
                    .title(Component.translatable("creativetab.edible_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.WATER_BUCKET.get());
                        pOutput.accept(ModItems.LAVA_BUCKET.get());
                        pOutput.accept(ModBlocks.OBSIDIAN.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
