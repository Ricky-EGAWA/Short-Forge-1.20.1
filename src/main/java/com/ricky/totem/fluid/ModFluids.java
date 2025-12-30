package com.ricky.totem.fluid;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, TotemItemsMod.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_REVERSE_WATER =
            FLUIDS.register("reverse_water", ReverseWaterFluid.Source::new);

    public static final RegistryObject<FlowingFluid> FLOWING_REVERSE_WATER =
            FLUIDS.register("flowing_reverse_water", ReverseWaterFluid.Flowing::new);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
