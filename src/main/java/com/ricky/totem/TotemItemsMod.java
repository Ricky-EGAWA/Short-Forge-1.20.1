package com.ricky.totem;

import com.mojang.logging.LogUtils;
import com.ricky.totem.block.ModBlocks;
import com.ricky.totem.client.renderer.DonaldRenderer;
import com.ricky.totem.client.renderer.NotchRenderer;
import com.ricky.totem.entity.ModEntities;
import com.ricky.totem.fluid.ModFluidTypes;
import com.ricky.totem.fluid.ModFluids;
import com.ricky.totem.item.ModCreativeModTabs;
import com.ricky.totem.item.ModItems;
import com.ricky.totem.item.totem.TotemEffectHandler;
import com.ricky.totem.network.ModNetworkHandler;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.entity.monster.Zombie;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TotemItemsMod.MOD_ID)
public class TotemItemsMod {
    public static final String MOD_ID = "totem";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TotemItemsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(new TotemEffectHandler());

        ModCreativeModTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerEntityAttributes);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DONALD.get(), Zombie.createAttributes().build());
        event.put(ModEntities.NOTCH.get(), Zombie.createAttributes().build());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetworkHandler.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                // ドアとトラップドアの透明部分を正しく表示するため
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.FAKE_IRON_DOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.FAKE_IRON_TRAPDOOR.get(), RenderType.cutout());

                // 床はしごの透明部分を正しく表示するため
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.GROUND_LADDER.get(), RenderType.cutout());

                // 逆水ブロックの描画設定
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_REVERSE_WATER.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_REVERSE_WATER.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.REVERSE_FALLING_BLOCK.get(), FallingBlockRenderer::new);
            event.registerEntityRenderer(ModEntities.DONALD.get(), DonaldRenderer::new);
            event.registerEntityRenderer(ModEntities.NOTCH.get(), NotchRenderer::new);
        }
    }
}
