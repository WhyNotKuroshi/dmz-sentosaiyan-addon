package com.sentosaiyanaddon.dmz.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import com.sentosaiyanaddon.dmz.client.event.KiSightEvent;

import com.dragonminez.client.render.effects.KiSenseAuraRenderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KiSenseAuraRenderer.class)
public abstract class KiSenseAuraThroughWallsMixin {

    @Inject(method = "customSetup", at = @At("TAIL"), remap = false)
    private static void sentosaiyan$disableDepthForKiSense(
            RenderType type, ResourceLocation texture, ShaderInstance shader,
            CallbackInfo ci) {
        // You only pass through walls if the observer is Sento + Mutant
        if (KiSightEvent.isObserverEligible()) {
            RenderSystem.disableDepthTest();
        }
    }

    @Inject(method = "customClear", at = @At("HEAD"), remap = false)
    private static void sentosaiyan$restoreDepthAfterKiSense(
            RenderType type, CallbackInfo ci) {
        // It always restores [the state], to prevent state leakage if the observer changes midway through
        if (KiSightEvent.isObserverEligible()) {
            RenderSystem.enableDepthTest();
        }
    }
}