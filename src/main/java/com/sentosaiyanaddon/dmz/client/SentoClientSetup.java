package com.sentosaiyanaddon.dmz.client;

import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;
import com.sentosaiyanaddon.dmz.block.SentoFluids;
import com.sentosaiyanaddon.dmz.client.render.SabonRenderer;
import com.sentosaiyanaddon.dmz.client.render.SabonaRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SentoSaiyanAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SentoClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(SentoFluids.HOT_SPRING.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(SentoFluids.FLOWING_HOT_SPRING.get(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void registerEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(SentoSaiyanAddon.MOD_ID, "sento"),
                new SentoDimensionEffects());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SentoSaiyanAddon.SABONA.get(), SabonaRenderer::new);
        event.registerEntityRenderer(SentoSaiyanAddon.SABON.get(), SabonRenderer::new);
    }
}