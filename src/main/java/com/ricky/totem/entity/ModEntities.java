package com.ricky.totem.entity;

import com.ricky.totem.TotemItemsMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TotemItemsMod.MOD_ID);

    public static final RegistryObject<EntityType<ReverseFallingBlockEntity>> REVERSE_FALLING_BLOCK =
            ENTITY_TYPES.register("reverse_falling_block",
                    () -> EntityType.Builder.<ReverseFallingBlockEntity>of(ReverseFallingBlockEntity::new, MobCategory.MISC)
                            .sized(0.98F, 0.98F)
                            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("reverse_falling_block"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
