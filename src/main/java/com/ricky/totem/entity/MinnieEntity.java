package com.ricky.totem.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;

/**
 * ミニーエンティティ - ハスクと同様のモブ
 */
public class MinnieEntity extends Husk {

    public MinnieEntity(EntityType<? extends Husk> entityType, Level level) {
        super(entityType, level);
    }
}
