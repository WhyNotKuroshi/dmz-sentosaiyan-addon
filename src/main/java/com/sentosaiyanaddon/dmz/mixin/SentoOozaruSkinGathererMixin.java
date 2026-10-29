package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.client.util.SkinGathererProvider;
import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.character.Character;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkinGathererProvider.class, remap = false)
public class SentoOozaruSkinGathererMixin {

    @Inject(method = "gatherBodyLayers", at = @At("HEAD"), cancellable = true, remap = false)
    private void sentosaiyan$oozaruLayersOnly(
            AbstractClientPlayer player, 
            StatsData stats,
            float partialTick,
            java.util.function.BiConsumer<net.minecraft.resources.ResourceLocation, float[]> consumer,
            CallbackInfo ci) {
        
        if (ci.isCancelled() || stats == null || stats.getCharacter() == null) return;

        Character character = stats.getCharacter();
        if (!"sentosaiyan".equalsIgnoreCase(character.getRaceName())) return;

        String currentForm = character.getActiveForm();
        boolean isOozaru = currentForm != null && (
                currentForm.equalsIgnoreCase("oozaru") || 
                currentForm.equalsIgnoreCase("goldenoozaru") ||
                currentForm.equalsIgnoreCase("sentosaiyanoozaru")
        );

        if (!isOozaru) {
            return;
        }

        float[][] colors = new float[][] {
                character.getRgbBodyColor(),
                character.getRgbBodyColor2(),
                character.getRgbBodyColor3()
        };
        sentosaiyan$applyFormColors(character, colors);

        float[] b1 = colors[0];
        float[] b2 = colors[1];
        float[] b3 = colors[2];

        String base = "textures/entity/races/sentosaiyan/sentosaiyan_oozaru_";
        consumer.accept(DMZSkinLayer.getSafeTexture(
                ResourceLocation.fromNamespaceAndPath("dragonminez", base + "layer1.png")), b2);
        consumer.accept(DMZSkinLayer.getSafeTexture(
                ResourceLocation.fromNamespaceAndPath("dragonminez", base + "layer2.png")), b1);
        consumer.accept(DMZSkinLayer.getSafeTexture(
                ResourceLocation.fromNamespaceAndPath("dragonminez", base + "layer3.png")), b3);

        ci.cancel();
    }

    @Unique
    private static void sentosaiyan$applyFormColors(Character character, float[][] colors) {
        if (character.hasActiveForm() && character.getActiveFormData() != null) {
            FormConfig.FormData f = character.getActiveFormData();
            if (!f.getBodyColor1().isEmpty()) colors[0] = f.getRgbBodyColor1();
            if (!f.getBodyColor2().isEmpty()) colors[1] = f.getRgbBodyColor2();
            if (!f.getBodyColor3().isEmpty()) colors[2] = f.getRgbBodyColor3();
        }
        if (character.hasActiveStackForm() && character.getActiveStackFormData() != null) {
            FormConfig.FormData sf = character.getActiveStackFormData();
            if (!sf.getBodyColor1().isEmpty()) colors[0] = sf.getRgbBodyColor1();
            if (!sf.getBodyColor2().isEmpty()) colors[1] = sf.getRgbBodyColor2();
            if (!sf.getBodyColor3().isEmpty()) colors[2] = sf.getRgbBodyColor3();
        }
    }
}