package com.sentosaiyanaddon.dmz.server;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon")
public final class SentoWeatherState {

    public static final ResourceKey<Level> SENTO_KEY = ResourceKey.create(
        Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "sento_planet")
    );

    public static boolean raining = false;
    public static boolean thundering = false;
    public static int weatherTimer = 12000;

    public static float visualRain = 0f;
    public static float visualThunder = 0f;

    public static int set(CommandSourceStack src, boolean rain, boolean thunder) {
        raining = rain;
        thundering = thunder;
        weatherTimer = rain ? 12000 : 6000;

        ServerLevel level = src.getServer().getLevel(SENTO_KEY);
        if (level == null) {
            src.sendFailure(Component.literal("Planet Sento not loaded"));
            return 0;
        }

        for (ServerPlayer p : level.players()) {
            syncTo(p);
        }

        String label = thunder ? "thunder" : (rain ? "rain" : "clear");
        src.sendSuccess(() -> Component.literal("Sento weather: " + label), true);
        return 1;
    }

    public static void syncTo(ServerPlayer p) {
        p.connection.send(new ClientboundGameEventPacket(
            ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, visualRain));
        p.connection.send(new ClientboundGameEventPacket(
            ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, visualThunder));
    }

    @SubscribeEvent
    public static void onDimChange(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!p.level().dimension().equals(SENTO_KEY)) return;
        syncTo(p);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!p.level().dimension().equals(SENTO_KEY)) return;
        syncTo(p);
    }

    private SentoWeatherState() {}
}