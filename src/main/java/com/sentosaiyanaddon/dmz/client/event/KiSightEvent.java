package com.sentosaiyanaddon.dmz.client.event;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.character.Character;
import com.dragonminez.common.stats.character.Effects;
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

        StatsData data = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
        if (data == null) return;

        Character character = data.getCharacter();
        if (character == null) return;

        String raceName = character.getRaceName();
        if (raceName == null) return;
        if (!"sentosaiyan".equalsIgnoreCase(raceName)) return;

        Effects effects = data.getEffects();
        if (effects == null) return;
        if (!effects.hasEffect("mutant")) return;

        observerEligible = true;
    }

    public static boolean isObserverEligible() {
        return observerEligible;
    }

    private KiSightEvent() {}
}