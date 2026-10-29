package com.sentosaiyanaddon.dmz.init.entities;

import com.dragonminez.common.init.entities.MastersEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class MasterSabonaEntity extends MastersEntity {
    
    public MasterSabonaEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.masterName = "sabona"; 
    }
}