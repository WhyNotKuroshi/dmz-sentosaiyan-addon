package com.sentosaiyanaddon.dmz.event;

import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;

import com.dragonminez.common.init.entities.MastersEntity; 

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SentoSaiyanAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SentoEntityEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SentoSaiyanAddon.SABONA.get(), MastersEntity.createAttributes().build());
        event.put(SentoSaiyanAddon.SABON.get(), MastersEntity.createAttributes().build());
    }
}