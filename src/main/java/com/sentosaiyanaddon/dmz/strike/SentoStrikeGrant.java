package com.sentosaiyanaddon.dmz.strike;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.ProgressionSyncS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.techniques.StrikeAttackData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sentosaiyanaddon", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SentoStrikeGrant {

    private SentoStrikeGrant() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        updateStrikes(player);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!(event.player instanceof ServerPlayer player))
            return;
        if (player.tickCount % 40 != 0)
            return;
        updateStrikes(player);
    }

    private static void updateStrikes(ServerPlayer player) {
        StatsProvider.get(StatsCapability.INSTANCE, (Entity) player).ifPresent(data -> {
            if (data.getStatus() == null || !data.getStatus().isHasCreatedCharacter())
                return;

            boolean sento = isSento(data);
            boolean mutant = data.getEffects() != null && data.getEffects().hasEffect("mutant");

            // Todas as tres sao habilidades exclusivas da raca
            // Trigger Wave e a unica habilidade que exige Mutant
            updateStrike(player, data, SentoStrikeTemplates.SOUND_BREAKER_ID, sento);
            updateStrike(player, data, SentoStrikeTemplates.FLEX_ID, sento);
            updateStrike(player, data, SentoStrikeTemplates.TRIGGER_WAVE_ID, sento && mutant);
        });
    }

    private static boolean isSento(StatsData data) {
        if (data.getCharacter() == null)
            return false;
        String race = data.getCharacter().getRaceName();
        return race != null && "sentosaiyan".equalsIgnoreCase(race);
    }

    // Grant/revoke. Se shouldHave e true e tem, entao nao adiciona
    // Se shouldHave e false e tem, entao remove e se nao muda nada entao faz nada

    private static void updateStrike(ServerPlayer player, StatsData data, String strikeId, boolean shouldHave) {
        boolean has = data.getTechniques().getUnlockedTechniques().containsKey(strikeId);

        if (shouldHave && !has) {
            StrikeAttackData technique = SentoStrikeTemplates.copy(strikeId);
            if (technique == null)
                return;
            data.getTechniques().unlockTechnique(technique);
            NetworkHandler.sendToTrackingEntityAndSelf(new ProgressionSyncS2C(player), (Entity) player);
        } else if (!shouldHave && has) {
            data.getTechniques().removeTechnique(strikeId);
            NetworkHandler.sendToTrackingEntityAndSelf(new ProgressionSyncS2C(player), (Entity) player);
        }
    }
}