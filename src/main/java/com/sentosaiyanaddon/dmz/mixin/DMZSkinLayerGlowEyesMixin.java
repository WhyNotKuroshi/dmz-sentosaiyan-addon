package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.stats.character.Character;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@Mixin(value = DMZSkinLayer.class, remap = false)
public class DMZSkinLayerGlowEyesMixin {

    @Unique
    private static final ThreadLocal<Boolean> sentosaiyan$isSentoFace =
        ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = "renderCustomFace", at = @At("HEAD"), remap = false)
    private void sentosaiyan$markSentoFace(
            BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
            MultiBufferSource bufferSource, Character character, String faceKey, String race,
            float[] eye1, float[] eye2, float[] skin, float[] hair,
            float pt, int pl, int po, float alpha, CallbackInfo ci) {
        sentosaiyan$isSentoFace.set("sentosaiyan".equalsIgnoreCase(race));
    }

    @Inject(method = "renderCustomFace", at = @At("RETURN"), remap = false)
    private void sentosaiyan$unmarkSentoFace(
            BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
            MultiBufferSource bufferSource, Character character, String faceKey, String race,
            float[] eye1, float[] eye2, float[] skin, float[] hair,
            float pt, int pl, int po, float alpha, CallbackInfo ci) {
        sentosaiyan$isSentoFace.set(Boolean.FALSE);
    }

    // Pupila direita = segunda invocacao (ordinal = 1)
    @ModifyArg(
        method = "renderCustomFace",
        at = @At(
            value = "INVOKE",
            target = "Lcom/dragonminez/client/render/layer/DMZSkinLayer;" +
                     "renderColoredLayer(" +
                     "Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;" +
                     "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                     "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                     "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                     "Ljava/lang/String;[FFIIF)V",
            ordinal = 1
        ),
        index = 7,
        remap = false
    )
    private int sentosaiyan$glowRightPupil(int packedLight) {
        if (!Boolean.TRUE.equals(sentosaiyan$isSentoFace.get())) return packedLight;
        return LightTexture.FULL_BRIGHT;
    }

    // Pupila esquerda = terceira invocacao (ordinal = 2)
    @ModifyArg(
        method = "renderCustomFace",
        at = @At(
            value = "INVOKE",
            target = "Lcom/dragonminez/client/render/layer/DMZSkinLayer;" +
                     "renderColoredLayer(" +
                     "Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;" +
                     "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                     "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                     "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                     "Ljava/lang/String;[FFIIF)V",
            ordinal = 2
        ),
        index = 7,
        remap = false
    )
    private int sentosaiyan$glowLeftPupil(int packedLight) {
        if (!Boolean.TRUE.equals(sentosaiyan$isSentoFace.get())) return packedLight;
        return LightTexture.FULL_BRIGHT;
    }
}