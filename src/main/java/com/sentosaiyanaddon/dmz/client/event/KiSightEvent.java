package com.sentosaiyanaddon.dmz.client.event;

import com.sentosaiyanaddon.dmz.util.SentoUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = "sentosaiyanaddon",
    value = Dist.CLIENT,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class KiSightEvent {

    private static boolean observerEligible = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        observerEligible = false;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        observerEligible = SentoUtils.isSentoMutant(player);
    }

    public static boolean isObserverEligible() {
        return observerEligible;
    }

    private KiSightEvent() {}
}