package com.sentosaiyanaddon.dmz.mixin;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.techniques.StrikeAttackData;
import com.dragonminez.common.stats.techniques.TechniqueData;
import com.dragonminez.server.events.players.combat.StrikeAttackHandler;
import com.sentosaiyanaddon.dmz.ability.SentoAbilities;
import com.sentosaiyanaddon.dmz.ability.SentoFlexHandler;
import com.sentosaiyanaddon.dmz.strike.SentoStrikeTemplates;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StrikeAttackHandler.class)
public class SentoStrikeHandlerMixin {

    @Inject(method = "requestStrike", at = @At("HEAD"), cancellable = true, remap = false)
    private static void sentosaiyan$handleSentoStrikes(ServerPlayer player, int targetId, CallbackInfo ci) {
        if (player == null)
            return;

        StatsProvider.get(StatsCapability.INSTANCE, (Entity) player).ifPresent(data -> {
            TechniqueData selected = data.getTechniques().getSelectedTechnique();
            if (!(selected instanceof StrikeAttackData strike))
                return;

            String id = strike.getId();
            if (id == null)
                return;

            switch (id) {
                case SentoStrikeTemplates.SOUND_BREAKER_ID -> {
                    SentoAbilities.castSoundBreaker(player);
                    ci.cancel();
                }
                case SentoStrikeTemplates.TRIGGER_WAVE_ID -> {
                    SentoAbilities.castTriggerWave(player);
                    ci.cancel();
                }
                case SentoStrikeTemplates.FLEX_ID -> {
                    SentoFlexHandler.toggle(player);
                    ci.cancel();
                }
                default -> {
                    /*nao mexe.*/ }
            }
        });
    }
}