package com.sentosaiyanaddon.dmz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.character.Character;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.cache.object.BakedGeoModel;


    @Mixin(value = DMZSkinLayer.class, remap = false)
public class DMZSkinLayerHairMixin {

    @Inject(method = "renderHair", at = @At("HEAD"), cancellable = true, remap = false)
    private void sentosaiyan$skipOozaruHairBase(
            PoseStack poseStack, AbstractClientPlayer animatable, BakedGeoModel model,
            MultiBufferSource bufferSource, AbstractClientPlayer player, StatsData stats,
            float partialTick, int packedLight, int packedOverlay, float alpha,
            CallbackInfo ci) {
        if (stats == null) return;
        Character character = stats.getCharacter();
        if (character == null) return;
        if (!"sentosaiyan".equalsIgnoreCase(character.getRaceName())) return;
        String form = character.getActiveForm();
        if (form != null && (form.equalsIgnoreCase("oozaru") || form.equalsIgnoreCase("goldenoozaru"))) {
            ci.cancel();
        }
    }
}
