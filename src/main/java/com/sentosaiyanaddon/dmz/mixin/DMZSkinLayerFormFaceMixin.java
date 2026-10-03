package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.stats.character.Character;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.lang.reflect.Method;

@Mixin(value = DMZSkinLayer.class, remap = false)
public abstract class DMZSkinLayerFormFaceMixin {

    /*
     * cor da borda ssj4 (RGB 0-1)
     * equivalente ao hex #D11A11
     */
    @Unique
    private static final float[] SENTO_SSJ4_EYE_BORDER_COLOR = new float[] { 0.82F, 0.10F, 0.07F };

    /*
     * cor da borda ssj full power (RGB 0-1)
     * branco #FFFFFF
     */
    @Unique
    private static final float[] SENTO_SSJFP_EYE_BORDER_COLOR = new float[] { 1.0F, 1.0F, 1.0F };

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

    @WrapOperation(method = "renderFace", at = @At(value = "INVOKE", target = "Lcom/dragonminez/common/stats/character/Character;getBodyType()I"), remap = false)
    private int sentosaiyan$bypassBodyTypeZeroReturn(Character character, Operation<Integer> original) {
        int bodyType = original.call(character);
        if (sentosaiyan$isSentoSSJ4(character)) {
            return bodyType == 0 ? 1 : bodyType;
        }
        return bodyType;
    }

@Inject(method = "renderCustomFace", at = @At("TAIL"), remap = false)
private void sentosaiyan$renderFormEyesBorder(
        BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
        MultiBufferSource bufferSource, Character character,
        String faceKey, String race,
        float[] eye1, float[] eye2, float[] skin, float[] hair,
        float pt, int pl, int po, float alpha,
        CallbackInfo ci) {

    if (!"sentosaiyan".equalsIgnoreCase(race))
        return;

    // padrao: vermelho em todas as formas
    float[] borderColor = SENTO_SSJ4_EYE_BORDER_COLOR;

    // excecao: branco no ssj full power
    if (sentosaiyan$isSentoSSJFullpower(character)) {
        borderColor = SENTO_SSJFP_EYE_BORDER_COLOR;
    }

    int eyesType = character.getEyesType();
    String path = "textures/entity/races/sentosaiyan/faces/sentospecialform_eyes_" + eyesType + ".png";

    sentosaiyan$callRenderColoredLayer(
            model, poseStack, animatable, bufferSource,
            path, borderColor,
            pt, pl, po, alpha);
}

    // verifica se e sento + ssj4
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

    // verifica se e sento + ssj full power
    @Unique
    private static boolean sentosaiyan$isSentoSSJFullpower(Character character) {
        if (character == null)
            return false;
        String race = character.getRaceName();
        if (race == null || !"sentosaiyan".equalsIgnoreCase(race))
            return false;
        String form = character.getActiveForm();
        if (form == null)
            return false;
        String lower = form.toLowerCase();
        return lower.contains("ssjfullpower");
    }
}