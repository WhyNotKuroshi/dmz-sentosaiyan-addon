package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.stats.character.Character;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.lang.reflect.Method;

@Mixin(value = DMZSkinLayer.class, remap = false)
public abstract class DMZSkinLayerSsj4FaceMixin {

    /*
     * Cor da borda vermelha SSJ4 (RGB 0-1)
     * Equivalente ao hex #D11A11
     * 
     * A cor usava bodyColor2 do form (padrao DMZ pra SSJ4 eyes)
     */

    // Cor da borda agora e fixa (vermelho tradicional)
    @Unique
    private static final float[] SENTO_SSJ4_EYE_BORDER_COLOR = new float[] { 0.82F, 0.10F, 0.07F };

    @Unique
    private static final Method sentosaiyan$renderColoredLayer;

    static {
        Method m = null;
        try {
            m = DMZSkinLayer.class.getDeclaredMethod(
                    "renderColoredLayer",
                    BakedGeoModel.class,
                    PoseStack.class,
                    AbstractClientPlayer.class,
                    MultiBufferSource.class,
                    String.class,
                    float[].class,
                    float.class,
                    int.class,
                    int.class,
                    float.class);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            System.err.println("[SentoSaiyanAddon] renderColoredLayer not found: " + e);
        }
        sentosaiyan$renderColoredLayer = m;
    }

    @Unique
    private void sentosaiyan$callRenderColoredLayer(
            BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
            MultiBufferSource bufferSource, String path, float[] rgb,
            float pt, int pl, int po, float alpha) {
        if (sentosaiyan$renderColoredLayer == null)
            return;
        try {
            sentosaiyan$renderColoredLayer.invoke(this,
                    model, poseStack, animatable, bufferSource, path, rgb, pt, pl, po, alpha);
        } catch (Exception e) {
            System.err.println("[SentoSaiyanAddon] renderColoredLayer invoke failed: " + e);
        }
    }

    @ModifyVariable(method = "dispatchFaceRender", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private String sentosaiyan$remapFaceKey(String faceKey) {
        return com.sentosaiyanaddon.dmz.util.SentoModelRegistry.resolveFaceKey(faceKey);
    }

    @Redirect(method = "renderFace", at = @At(value = "INVOKE", target = "Lcom/dragonminez/common/stats/character/Character;getBodyType()I"), remap = false)
    private int sentosaiyan$bypassBodyTypeZeroReturn(Character character) {
        int bodyType = character.getBodyType();
        if (sentosaiyan$isSentoSSJ4(character)) {
            return bodyType == 0 ? 1 : bodyType;
        }
        return bodyType; // outras racas passam intactas, nao vai dar ruim nao eu acho
    }

    @Inject(method = "renderCustomFace", at = @At("TAIL"), remap = false)
    private void sentosaiyan$renderSsj4EyesBorder(
            BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
            MultiBufferSource bufferSource, Character character,
            String faceKey, String race,
            float[] eye1, float[] eye2, float[] skin, float[] hair,
            float pt, int pl, int po, float alpha,
            CallbackInfo ci) {

        if (!"sentosaiyan".equalsIgnoreCase(race))
            return;

        if (!sentosaiyan$isSentoSSJ4(character))
            return;

        // Pega o eyesType do personagem
        int eyesType = character.getEyesType();

        // Path da textura da borda do olho
        String path = "textures/entity/races/sentosaiyan/faces/ssj4_eyes_" + eyesType + ".png";

        sentosaiyan$callRenderColoredLayer(
                model, poseStack, animatable, bufferSource,
                path, SENTO_SSJ4_EYE_BORDER_COLOR,
                pt, pl, po, alpha);
    }

    // AVISO: Isso aqui checa se e Sento + SSJ4, sem isso o olho nao funciona do jeito esperado (com a borda)
    @Unique
    private static boolean sentosaiyan$isSentoSSJ4(Character character) {
        if (character == null)
            return false;
        String race = character.getRaceName();
        if (race == null || !"sentosaiyan".equalsIgnoreCase(race))
            return false;
        String form = character.getActiveForm();
        if (form == null)
            return false;
        String lower = form.toLowerCase();
        return lower.contains("supersaiyan4") || lower.contains("ssj4");
    }
}