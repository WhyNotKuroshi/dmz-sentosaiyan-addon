package com.sentosaiyanaddon.dmz.server;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon")
public final class SentoWeatherTickHandler {

    private static final float STEP = 0.015f;
    private static final int RAIN_MIN  = 6000;
    private static final int RAIN_MAX  = 18000;
    private static final int CLEAR_MIN = 12000;
    private static final int CLEAR_MAX = 24000;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        MinecraftServer server = e.getServer();
        ServerLevel level = server.getLevel(SentoWeatherState.SENTO_KEY);
        if (level == null) return;

        // Ciclo aleatório de chuva
        if (--SentoWeatherState.weatherTimer <= 0) {
            boolean startRain = !SentoWeatherState.raining;
            SentoWeatherState.raining = startRain;
            SentoWeatherState.thundering = false;
            SentoWeatherState.weatherTimer = startRain
                ? RAIN_MIN  + level.random.nextInt(RAIN_MAX  - RAIN_MIN)
                : CLEAR_MIN + level.random.nextInt(CLEAR_MAX - CLEAR_MIN);
        }

        float targetRain    = SentoWeatherState.raining    ? 1f : 0f;
        float targetThunder = SentoWeatherState.thundering ? 1f : 0f;

        float oldRain    = SentoWeatherState.visualRain;
        float oldThunder = SentoWeatherState.visualThunder;

        SentoWeatherState.visualRain    = step(SentoWeatherState.visualRain,    targetRain,    STEP);
        SentoWeatherState.visualThunder = step(SentoWeatherState.visualThunder, targetThunder, STEP);

        if (SentoWeatherState.visualRain != oldRain
                || SentoWeatherState.visualThunder != oldThunder) {
            for (ServerPlayer p : level.players()) {
                p.connection.send(new ClientboundGameEventPacket(
                    ClientboundGameEventPacket.RAIN_LEVEL_CHANGE,
                    SentoWeatherState.visualRain));
                p.connection.send(new ClientboundGameEventPacket(
                    ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE,
                    SentoWeatherState.visualThunder));
            }
        }
    }

    private static float step(float curr, float target, float step) {
        if (curr < target) return Math.min(curr + step, target);
        if (curr > target) return Math.max(curr - step, target);
        return curr;
    }

    private SentoWeatherTickHandler() {}
}