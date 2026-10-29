package com.sentosaiyanaddon.dmz.client.gui;

import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;
import com.sentosaiyanaddon.dmz.ability.SentoStressHandler;
import com.sentosaiyanaddon.dmz.server.SentoWeatherState;
import com.sentosaiyanaddon.dmz.util.SentoUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SentoSaiyanAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SentoStressOverlay {

    public static final IGuiOverlay HUD_STRESS = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (!SentoUtils.isSento(player)) return;
        if (!player.level().dimension().equals(SentoWeatherState.SENTO_KEY)) return;

        float stress = player.getPersistentData().getFloat(SentoStressHandler.NBT_STRESS);
        int barHeight = 60; 
        int barWidth = 6;   
        int x = 12; 
        int y = (height / 2) - (barHeight / 2);

        int filledHeight = (int) ((stress / SentoStressHandler.MAX_STRESS) * barHeight);

        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0x99000000);

        int color = stress > 80 ? 0xFFFF1111 : 0xFFDD6600; 
        guiGraphics.fill(x, y + barHeight - filledHeight, x + barWidth, y + barHeight, color);
    };

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.FOOD_LEVEL.id(), "sento_stress_bar", HUD_STRESS);
    }
}