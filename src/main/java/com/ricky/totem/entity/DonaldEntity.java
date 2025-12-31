package com.ricky.totem.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;

/**
 * ドナルドエンティティ - ハスクと同様のモブ
 */
public class DonaldEntity extends Husk {

    public DonaldEntity(EntityType<? extends Husk> entityType, Level level) {
        super(entityType, level);
    }
}
