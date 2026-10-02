package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.common.stats.StatsData;
import com.sentosaiyanaddon.dmz.ability.SentoEffects;
import com.sentosaiyanaddon.dmz.config.SentoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatsData.class)
public class StatsDataFlexMixin {

    @Inject(method = "getMeleeDamage", at = @At("RETURN"), cancellable = true, remap = false)
    private void sentosaiyan$flexMeleeBonus(CallbackInfoReturnable<Double> cir) {
        if (cir.isCancelled()) return;
        StatsData self = (StatsData) (Object) this;
        if (self.getPlayer() == null) return;
        if (!self.getPlayer().hasEffect(SentoEffects.FLEX.get())) return;
        cir.setReturnValue(cir.getReturnValue() * (1.0 + SentoConfig.FLEX_ATTACK_BONUS.get()));
    }

    @Inject(method = "getMaxMeleeDamage", at = @At("RETURN"), cancellable = true, remap = false)
    private void sentosaiyan$flexMaxMeleeBonus(CallbackInfoReturnable<Double> cir) {
        if (cir.isCancelled()) return;
        StatsData self = (StatsData) (Object) this;
        if (self.getPlayer() == null) return;
        if (!self.getPlayer().hasEffect(SentoEffects.FLEX.get())) return;
        cir.setReturnValue(cir.getReturnValue() * (1.0 + SentoConfig.FLEX_ATTACK_BONUS.get()));
    }
}