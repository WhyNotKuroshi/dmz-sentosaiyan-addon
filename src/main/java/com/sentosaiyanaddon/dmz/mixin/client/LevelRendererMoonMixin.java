package com.sentosaiyanaddon.dmz.mixin.client;

import com.sentosaiyanaddon.dmz.server.SentoWeatherState;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMoonMixin {

    private static final ResourceLocation MOON_PATH =
        ResourceLocation.tryParse("textures/environment/moon_phases.png");
    private static final ResourceLocation EMPTY =
        ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "textures/environment/nada.png");

    @WrapOperation(
        method = "renderSky",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
    )
    private void sento$hideMoon(int index, ResourceLocation loc, Operation<Void> original) {
        Minecraft mc = Minecraft.getInstance();
        boolean inSento = mc.level != null
            && mc.level.dimension().location().equals(SentoWeatherState.SENTO_KEY.location());
        if (inSento && MOON_PATH.equals(loc)) {
            original.call(index, EMPTY);
            return;
        }
        original.call(index, loc);
    }
}