package com.sentosaiyanaddon.dmz.client.event;

import com.mojang.blaze3d.shaders.FogShape;

import com.sentosaiyanaddon.dmz.server.SentoWeatherState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon",
        bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SentoFogHandler {

    private static final ResourceLocation ARCTIC_BIOME =
        ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "sento_arctic");

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!SentoWeatherState.thundering) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.level() == null) return;

        ResourceLocation biome = player.level()
            .getBiome(player.blockPosition())
            .unwrapKey()
            .map(k -> k.location())
            .orElse(null);

        if (!ARCTIC_BIOME.equals(biome)) return;

        event.setNearPlaneDistance(2.0f);
        event.setFarPlaneDistance(25.0f);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!SentoWeatherState.thundering) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        ResourceLocation biome = mc.level
            .getBiome(player.blockPosition())
            .unwrapKey()
            .map(k -> k.location())
            .orElse(null);

        if (!ARCTIC_BIOME.equals(biome)) return;

        Level level = mc.level;
        for (int i = 0; i < 6; i++) {
            double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 20;
            double y = player.getY() + 10 + RANDOM.nextDouble() * 10;
            double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 20;

            double vx = 0.8 + RANDOM.nextDouble() * 0.4;
            double vy = -1.5 - RANDOM.nextDouble() * 0.5;
            double vz = (RANDOM.nextDouble() - 0.5) * 0.2;

            level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, vx, vy, vz);
        }
    }

    private SentoFogHandler() {}
}