package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.extras.ActionMode;
import com.dragonminez.common.util.TransformationsHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = TransformationsHelper.class, remap = false)
public class TransformationsHelperMoonMixin {

    private static final Map<UUID, Long> sentosaiyan$lastTrigger = new ConcurrentHashMap<>();

    @Inject(method = "shouldAutoChargeOozaru", at = @At("HEAD"), cancellable = true, remap = false)
    private static void sentosaiyan$allowOozaru(Player player, StatsData statsData,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (cir.isCancelled()) return;
        if (player == null || statsData == null) return;
        if (statsData.getCharacter() == null) return;
        if (!"sentosaiyan".equalsIgnoreCase(statsData.getCharacter().getRaceName())) return;

        if (statsData.getStatus().getSelectedAction() != ActionMode.FORM) return;
        if (statsData.getCharacter().hasActiveForm()) return;
        if (statsData.getCharacter().hasActiveStackForm()) return;

        long now = player.level().getGameTime();
        Long last = sentosaiyan$lastTrigger.get(player.getUUID());
        if (last != null && now - last < 60L) return;

        FormConfig.FormData nextForm = TransformationsHelper.getNextAvailableForm(statsData);
        if (!TransformationsHelper.isOozaruForm(nextForm)) return;

        sentosaiyan$lastTrigger.put(player.getUUID(), now);
        cir.setReturnValue(true);
    }
}