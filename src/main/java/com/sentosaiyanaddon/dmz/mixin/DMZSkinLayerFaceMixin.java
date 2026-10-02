package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.character.Character;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@Mixin(value = DMZSkinLayer.class, remap = false)
public class DMZSkinLayerFaceMixin {

    @Inject(method = "renderFace", at = @At("HEAD"), cancellable = true, remap = false)
    private void sentosaiyan$skipOozaruFace(
            PoseStack poseStack, AbstractClientPlayer animatable, BakedGeoModel model,
            MultiBufferSource bufferSource, AbstractClientPlayer player, StatsData stats,
            float partialTick, int packedLight, int packedOverlay, float alpha,
            CallbackInfo ci) {
        if (ci.isCancelled()) return;
        Character character = stats.getCharacter();
        if (!"sentosaiyan".equalsIgnoreCase(character.getRaceName())) return;
        String form = character.getActiveForm();
        if (form != null && (form.equalsIgnoreCase("oozaru") || form.equalsIgnoreCase("goldenoozaru"))) {
            ci.cancel();
        }
    }
}