package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.extras.ActionMode;
import com.dragonminez.common.util.TransformationsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransformationsHelper.class, remap = false)
public class TransformationsHelperMoonMixin {

    private static final ResourceLocation SENTO_PLANET =
            new ResourceLocation("sentosaiyanaddon", "sento_planet");

    @Inject(method = "shouldAutoChargeOozaru", at = @At("RETURN"), cancellable = true, remap = false)
    private static void sentosaiyan$allowOozaruOnSento(Player player, StatsData statsData,
                                                       CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue())) return;

        if (player == null || statsData == null) return;
        if (statsData.getCharacter() == null) return;
        if (!"sentosaiyan".equalsIgnoreCase(statsData.getCharacter().getRaceName())) return;

        // sp age no planeta sento
        if (!player.level().dimension().location().equals(SENTO_PLANET)) return;

        if (statsData.getStatus().getSelectedAction() != ActionMode.FORM) return;
        if (statsData.getCharacter().hasActiveForm()) return;
        if (statsData.getCharacter().hasActiveStackForm()) return;

        FormConfig.FormData nextForm = TransformationsHelper.getNextAvailableForm(statsData);
        if (!TransformationsHelper.isOozaruForm(nextForm)) return;

        // TODO: quando o sistema de stress existir, descomentar e usar como gatilho para forma
        // if (!SentoStressSystem.isAboveThreshold(player)) return;
        // cir.setReturnValue(true);
    }
}