package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.client.util.ColorUtils;
import com.dragonminez.client.util.SkinGathererProvider;
import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.config.RaceCharacterConfig;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.character.Character;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.BiConsumer;

@Mixin(value = SkinGathererProvider.class, remap = false)
public class SkinGathererProviderMixin {

    private static final float[] DEFAULT_TAIL_COLOR = ColorUtils.hexToRgb("#572117");

    @Inject(method = "gatherBodyLayers", at = @At("HEAD"), cancellable = true, remap = false)
    private void sentosaiyan$layers(AbstractClientPlayer player, StatsData stats,
                                    float partialTick,
                                    BiConsumer<ResourceLocation, float[]> consumer,
                                    CallbackInfo ci) {
        if (ci.isCancelled()) return;
        Character character = stats.getCharacter();
        if (!character.getRaceName().equalsIgnoreCase("sentosaiyan"))
            return;

        String currentForm = character.getActiveForm();
        boolean isOozaru = currentForm != null &&
                (currentForm.equalsIgnoreCase("oozaru")
                        || currentForm.equalsIgnoreCase("goldenoozaru"));

        // Pega as cores base do personagem e aplica override do form/stack ativo
        float[][] colors = new float[][] {
                character.getRgbBodyColor(),
                character.getRgbBodyColor2(),
                character.getRgbBodyColor3()
        };
        sentosaiyan$applyFormColors(character, colors);
        float[] b1 = colors[0];
        float[] b2 = colors[1];
        float[] b3 = colors[2];

        if (isOozaru) {
            String base = "textures/entity/races/sentosaiyan/sentosaiyan_oozaru_";
            consumer.accept(DMZSkinLayer.getSafeTexture(
                    new ResourceLocation("dragonminez", base + "layer1.png")), b2);
            consumer.accept(DMZSkinLayer.getSafeTexture(
                    new ResourceLocation("dragonminez", base + "layer2.png")), b1);
            consumer.accept(DMZSkinLayer.getSafeTexture(
                    new ResourceLocation("dragonminez", base + "layer3.png")), b3);
            ci.cancel();
            return;
        }

        RaceCharacterConfig rc = ConfigManager.getRaceCharacter("sentosaiyan");
        if (rc == null || !Boolean.TRUE.equals(rc.getIsLayered()))
            return;

        String gender = character.getGender().equalsIgnoreCase("female") ? "_female" : "_male";
        int body = character.getBodyType();

        String prefix = "textures/entity/races/sentosaiyan/sentosaiyan" + gender + "_" + body + "_";
        String fallback = "textures/entity/races/sentosaiyan/sentosaiyan" + gender + "_0_";

        consumer.accept(DMZSkinLayer.getSafeTexture(
                new ResourceLocation("dragonminez", prefix + "layer1.png"),
                new ResourceLocation("dragonminez", fallback + "layer1.png")), b1);
        consumer.accept(DMZSkinLayer.getSafeTexture(
                new ResourceLocation("dragonminez", prefix + "layer2.png")), b2);
        consumer.accept(DMZSkinLayer.getSafeTexture(
                new ResourceLocation("dragonminez", prefix + "layer3.png")), b3);

        // replica a lógica de cauda do DMZ que o cancel pulava, sem isso ela provavelmente vai sumir
        boolean hasSaiyanTail = (rc.getHasSaiyanTail() != null && rc.getHasSaiyanTail().booleanValue());
        boolean isSSJ4Active = (currentForm != null
                && (currentForm.contains("supersaiyan4") || currentForm.contains("ssj4")));
        boolean renderSaiyanTail = (hasSaiyanTail
                && (isSSJ4Active || (stats.getStatus().isTailVisible()
                        && character.isHasSaiyanTail())));

        if (renderSaiyanTail) {
            float[] tailColor = (b2 != null) ? b2 : DEFAULT_TAIL_COLOR;
            consumer.accept(DMZSkinLayer.getSafeTexture(
                    new ResourceLocation("dragonminez", "textures/entity/races/tail1.png")),
                    tailColor);
        }

        ci.cancel();
    }

     // Resolve override de cores do form ativo (e do stack form) por cima das cores base
     // do personagem, igual ao que o DMZSkinLayer base faz em dispatchFaceRender.
     // Modifica o array colors in-place: [0]=bodyColor1, [1]=bodyColor2, [2]=bodyColor3.
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